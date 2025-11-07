package aqario.deathkeeper.common.entity;

import aqario.deathkeeper.common.config.DeathkeeperConfig;
import aqario.deathkeeper.common.screen.GraveScreenHandler;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class GraveEntity extends Entity implements InventoryChangedListener, NamedScreenHandlerFactory {
    public static final TrackedData<Optional<UUID>> OWNER = DataTracker.registerData(GraveEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    public static final TrackedData<NbtCompound> INVENTORY = DataTracker.registerData(GraveEntity.class, TrackedDataHandlerRegistry.NBT_COMPOUND);
    public final float uniqueOffset;
    private final SimpleInventory items;

    public GraveEntity(EntityType<?> type, World world) {
        super(type, world);
        this.uniqueOffset = this.random.nextFloat() * (float) Math.PI * 2.0F;
        this.items = new SimpleInventory(54);
        this.items.addListener(this);
    }

    public static GraveEntity create(PlayerEntity player) {
        GraveEntity grave = new GraveEntity(DeathkeeperEntityType.GRAVE, player.getWorld());
        grave.setPos(player.getX(), player.getY(), player.getZ());
        grave.setCustomName(player.getName());
        grave.dataTracker.set(OWNER, Optional.of(player.getUuid()));

        NbtList list = new NbtList();
        player.getInventory().writeNbt(list);
        grave.items.readNbtList(list);
        if(FabricLoader.getInstance().isModLoaded("trinkets")) {
            TrinketsApi.getTrinketComponent(player).ifPresent(trinkets -> {
                for(Pair<SlotReference, ItemStack> slotReferenceItemStackPair : trinkets.getAllEquipped()) {
                    grave.items.addStack(slotReferenceItemStackPair.getRight());
                }
            });
        }

        grave.resetPosition();
        grave.refreshPosition();
        return grave;
    }

    @Override
    protected Entity.MoveEffect getMoveEffect() {
        return Entity.MoveEffect.NONE;
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if(!DeathkeeperConfig.openOtherGraves && !player.getUuid().equals(this.getOwnerUuid())) {
            return super.interact(player, hand);
        }
        if(!player.getWorld().isClient()
            && player.getStackInHand(hand).isEmpty()
            && player instanceof ServerPlayerEntity serverPlayer
        ) {
            serverPlayer.openHandledScreen(this);
            return ActionResult.CONSUME;
        }
        return super.interact(player, hand);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new GraveScreenHandler(syncId, playerInventory, this.items, this);
    }

    @Override
    public Text getDisplayName() {
        return super.getDisplayName(); // TODO: mixin since this overrides the entity display name
    }

    @Override
    public void tick() {
        super.tick();
        this.prevX = this.getX();
        this.prevY = this.getY();
        this.prevZ = this.getZ();
        Vec3d vec3d = this.getVelocity();

        float f = this.getStandingEyeHeight() - 0.11111111F;
        if(this.isTouchingWater() && this.getFluidHeight(FluidTags.WATER) > (double) f) {
            this.applyWaterBuoyancy();
        }
        else if(this.isInLava() && this.getFluidHeight(FluidTags.LAVA) > (double) f) {
            this.applyLavaBuoyancy();
        }
        else if(!this.hasNoGravity()) {
            this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
        }

        if(this.getWorld().isClient()) {
            this.noClip = false;
        }
        else {
            this.noClip = !this.getWorld().isSpaceEmpty(this, this.getBoundingBox().contract(1.0E-7));
            if(this.noClip) {
                this.pushOutOfBlocks(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0, this.getZ());
            }
        }

        if(!this.isOnGround() || this.getVelocity().horizontalLengthSquared() > 1.0E-5F || (this.age + this.getId()) % 4 == 0) {
            this.move(MovementType.SELF, this.getVelocity());
            float g = 0.98F;
            if(this.isOnGround()) {
                g = this.getWorld().getBlockState(new BlockPos((int) this.getX(), (int) (this.getY() - 1.0), (int) this.getZ())).getBlock().getSlipperiness() * 0.98F;
            }

            this.setVelocity(this.getVelocity().multiply(g, 0.98, g));
            if(this.isOnGround()) {
                Vec3d vec3d2 = this.getVelocity();
                if(vec3d2.y < 0.0) {
                    this.setVelocity(vec3d2.multiply(1.0, -0.5, 1.0));
                }
            }
        }

        this.velocityDirty |= this.updateWaterState();
        if(!this.getWorld().isClient()) {
            double d = this.getVelocity().subtract(vec3d).lengthSquared();
            if(d > 0.01) {
                this.velocityDirty = true;
            }
        }
    }

    private void applyWaterBuoyancy() {
        Vec3d vec3d = this.getVelocity();
        this.setVelocity(vec3d.x * 0.99F, vec3d.y + (double) (vec3d.y < 0.06F ? 5.0E-4F : 0.0F), vec3d.z * 0.99F);
    }

    private void applyLavaBuoyancy() {
        Vec3d vec3d = this.getVelocity();
        this.setVelocity(vec3d.x * 0.95F, vec3d.y + (double) (vec3d.y < 0.06F ? 5.0E-4F : 0.0F), vec3d.z * 0.95F);
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        if(!this.getWorld().isClient()) {
            if(!this.getBoundingBox().intersects(player.getBoundingBox())) {
                return;
            }

            if(player.getUuid().equals(this.dataTracker.get(OWNER).orElse(null))) {
                if(this.items != null) {
                    for(int i = 0; i < this.items.size(); ++i) {
                        ItemStack itemStack = this.items.getStack(i);
                        if(itemStack.isEmpty()) continue;
                        this.dropStack(itemStack);
                    }
                }
                this.discard();
            }
        }
    }

    @Nullable
    @Override
    public ItemEntity dropStack(ItemStack stack, float yOffset) {
        if(stack.isEmpty()) {
            return null;
        }
        if(this.getWorld().isClient()) {
            return null;
        }
        ItemEntity itemEntity = new ItemEntity(this.getWorld(), this.getX(), this.getY() + (double) yOffset, this.getZ(), stack);
        itemEntity.resetPickupDelay();
        this.getWorld().spawnEntity(itemEntity);
        return itemEntity;
    }

    @Override
    public boolean canHit() {
        return !this.isRemoved();
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public boolean isGlowing() {
        return DeathkeeperConfig.highlightGraves && this.getWorld().isClient() && this.getOwnerUuid() != null || super.isGlowing();
    }

    @Nullable
    public UUID getOwnerUuid() {
        return this.dataTracker.get(OWNER).orElse(null);
    }

    public void setOwnerUuid(@Nullable UUID uuid) {
        this.dataTracker.set(OWNER, Optional.ofNullable(uuid));
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(OWNER, Optional.empty());
        this.dataTracker.startTracking(INVENTORY, new NbtCompound());
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        if(this.getOwnerUuid() != null) {
            nbt.putUuid("Owner", this.getOwnerUuid());
        }
        NbtList nbtList = new NbtList();

        for(int i = 0; i < this.items.size(); ++i) {
            ItemStack itemStack = this.items.getStack(i);
            if(!itemStack.isEmpty()) {
                NbtCompound nbtCompound = new NbtCompound();
                nbtCompound.putByte("Slot", (byte) i);
                itemStack.writeNbt(nbtCompound);
                nbtList.add(nbtCompound);
            }
        }

        nbt.put("Items", nbtList);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if(nbt.getUuid("Owner") != null) {
            this.setOwnerUuid(nbt.getUuid("Owner"));
        }
        NbtList nbtList = nbt.getList("Items", NbtElement.COMPOUND_TYPE);

        for(int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            int j = nbtCompound.getByte("Slot") & 255;
            if(j < this.items.size()) {
                this.items.setStack(j, ItemStack.fromNbt(nbtCompound));
            }
        }
    }

    public float getRotation(float tickDelta) {
        return (this.age + tickDelta) / 20.0F + this.uniqueOffset;
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.AMBIENT;
    }

    @Override
    public float getBodyYaw() {
        return 180.0F - this.getRotation(0.5F) / (float) (Math.PI * 2) * 360.0F;
    }

    @Override
    public void onInventoryChanged(Inventory sender) {
    }
}
