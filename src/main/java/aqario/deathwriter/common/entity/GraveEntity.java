package aqario.deathwriter.common.entity;

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
import net.minecraft.nbt.NbtList;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.tag.FluidTags;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class GraveEntity extends Entity implements InventoryChangedListener, NamedScreenHandlerFactory {
    public static final TrackedData<Optional<UUID>> OWNER = DataTracker.registerData(GraveEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    public static final TrackedData<NbtCompound> INVENTORY = DataTracker.registerData(GraveEntity.class, TrackedDataHandlerRegistry.TAG_COMPOUND);
    public final float uniqueOffset;
    private final SimpleInventory inventory;

    public GraveEntity(EntityType<?> type, World world) {
        super(type, world);
        this.uniqueOffset = this.random.nextFloat() * (float) Math.PI * 2.0F;
        this.inventory = new SimpleInventory(54);
        this.inventory.addListener(this);
    }

    public static GraveEntity create(ServerPlayerEntity player) {
        GraveEntity grave = new GraveEntity(DeathwriterEntityType.GRAVE, player.world);
        grave.setPos(player.getX(), player.getY(), player.getZ());
        grave.setCustomName(player.getName());
        grave.dataTracker.set(OWNER, Optional.of(player.getUuid()));

        NbtList list = new NbtList();
        player.getInventory().writeNbt(list);
        grave.inventory.readNbtList(list);

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
        this.dataTracker.set(OWNER, Optional.of(player.getUuid()));
        this.setCustomName(player.getName());
        player.sendMessage(this.getDisplayName().copy().append(Text.literal(" open handled screen")), true);
        return super.interact(player, hand);
    }

    @Override
    public void tick() {
        super.tick();
        this.prevX = this.getX();
        this.prevY = this.getY();
        this.prevZ = this.getZ();
        Vec3d vec3d = this.getVelocity();

        float f = this.getStandingEyeHeight() - 0.11111111F;
        if (this.isTouchingWater() && this.getFluidHeight(FluidTags.WATER) > (double) f) {
            this.applyWaterBuoyancy();
        } else if (this.isInLava() && this.getFluidHeight(FluidTags.LAVA) > (double) f) {
            this.applyLavaBuoyancy();
        } else if (!this.hasNoGravity()) {
            this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
        }

        if (this.world.isClient) {
            this.noClip = false;
        } else {
            this.noClip = !this.world.isSpaceEmpty(this, this.getBoundingBox().contract(1.0E-7));
            if (this.noClip) {
                this.pushOutOfBlocks(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0, this.getZ());
            }
        }

        if (!this.onGround || this.getVelocity().horizontalLengthSquared() > 1.0E-5F || (this.age + this.getId()) % 4 == 0) {
            this.move(MovementType.SELF, this.getVelocity());
            float g = 0.98F;
            if (this.onGround) {
                g = this.world.getBlockState(new BlockPos(this.getX(), this.getY() - 1.0, this.getZ())).getBlock().getSlipperiness() * 0.98F;
            }

            this.setVelocity(this.getVelocity().multiply(g, 0.98, g));
            if (this.onGround) {
                Vec3d vec3d2 = this.getVelocity();
                if (vec3d2.y < 0.0) {
                    this.setVelocity(vec3d2.multiply(1.0, -0.5, 1.0));
                }
            }
        }

        this.velocityDirty |= this.updateWaterState();
        if (!this.world.isClient) {
            double d = this.getVelocity().subtract(vec3d).lengthSquared();
            if (d > 0.01) {
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
        if (!this.world.isClient) {
            if (!this.getBoundingBox().intersects(player.getBoundingBox())) {
                return;
            }

            if (player.getUuid() == this.dataTracker.get(OWNER).orElse(null)) {
                if (this.inventory != null) {
                    for (int i = 0; i < this.inventory.size(); ++i) {
                        ItemStack itemStack = this.inventory.getStack(i);
                        if (itemStack.isEmpty()) continue;
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
        if (stack.isEmpty()) {
            return null;
        }
        if (this.world.isClient) {
            return null;
        }
        ItemEntity itemEntity = new ItemEntity(this.world, this.getX(), this.getY() + (double) yOffset, this.getZ(), stack);
        itemEntity.resetPickupDelay();
        this.world.spawnEntity(itemEntity);
        return itemEntity;
    }

    @Override
    public boolean collides() {
        return !this.isRemoved();
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(OWNER, Optional.empty());
        this.dataTracker.startTracking(INVENTORY, new NbtCompound());
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putUuid("Owner", this.dataTracker.get(OWNER).orElse(null));
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.dataTracker.set(OWNER, Optional.of(nbt.getUuid("Owner")));
    }

    public float getRotation(float tickDelta) {
        return (this.age + tickDelta) / 20.0F + this.uniqueOffset;
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.AMBIENT;
    }

    @Override
    public float getVisualYaw() {
        return 180.0F - this.getRotation(0.5F) / (float) (Math.PI * 2) * 360.0F;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    @Override
    public void onInventoryChanged(Inventory sender) {
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return GenericContainerScreenHandler.createGeneric9x6(syncId, playerInventory, this.inventory);
    }
}
