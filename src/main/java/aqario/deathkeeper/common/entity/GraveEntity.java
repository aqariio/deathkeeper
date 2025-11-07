package aqario.deathkeeper.common.entity;

import aqario.deathkeeper.common.Deathkeeper;
import aqario.deathkeeper.common.config.DeathkeeperConfig;
import aqario.deathkeeper.common.integration.TrinketsIntegration;
import aqario.deathkeeper.common.screen.GraveScreenHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class GraveEntity extends Entity implements ContainerListener, MenuProvider {
    public static final EntityDataAccessor<Optional<UUID>> OWNER = SynchedEntityData.defineId(GraveEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    public static final EntityDataAccessor<CompoundTag> INVENTORY = SynchedEntityData.defineId(GraveEntity.class, EntityDataSerializers.COMPOUND_TAG);
    public final float uniqueOffset;
    public final SimpleContainer items;

    public GraveEntity(EntityType<?> type, Level world) {
        super(type, world);
        this.uniqueOffset = this.random.nextFloat() * (float) Math.PI * 2.0F;
        this.items = new SimpleContainer(54);
        this.items.addListener(this);
    }

    public static GraveEntity create(Player player) {
        GraveEntity grave = new GraveEntity(DeathkeeperEntityType.GRAVE, player.level());
        grave.setPosRaw(player.getX(), player.getY(), player.getZ());
        grave.setCustomName(player.getName());
        grave.entityData.set(OWNER, Optional.of(player.getUUID()));

        ListTag list = new ListTag();
        player.getInventory().save(list);
        grave.items.fromTag(list);
        if(Deathkeeper.isTrinketsLoaded()) {
            TrinketsIntegration.putTrinketsInGrave(player, grave);
        }
        grave.setOldPosAndRot();
        grave.reapplyPosition();
        return grave;
    }

    @NotNull
    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    @NotNull
    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if(!DeathkeeperConfig.openOtherGraves && !player.getUUID().equals(this.getOwnerUuid())) {
            return super.interact(player, hand);
        }
        if(!player.level().isClientSide()
            && player.getItemInHand(hand).isEmpty()
            && player instanceof ServerPlayer serverPlayer
        ) {
            serverPlayer.openMenu(this);
            return InteractionResult.CONSUME;
        }
        return super.interact(player, hand);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new GraveScreenHandler(syncId, playerInventory, this.items, this);
    }

    @NotNull
    @Override
    public Component getDisplayName() {
        return super.getDisplayName(); // TODO: mixin since this overrides the entity display name
    }

    @Override
    public void tick() {
        super.tick();
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
        Vec3 vec3d = this.getDeltaMovement();

        float f = this.getEyeHeight() - 0.11111111F;
        if(this.isInWater() && this.getFluidHeight(FluidTags.WATER) > (double) f) {
            this.applyWaterBuoyancy();
        }
        else if(this.isInLava() && this.getFluidHeight(FluidTags.LAVA) > (double) f) {
            this.applyLavaBuoyancy();
        }
        else if(!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.04, 0.0));
        }

        if(this.level().isClientSide()) {
            this.noPhysics = false;
        }
        else {
            this.noPhysics = !this.level().noCollision(this, this.getBoundingBox().deflate(1.0E-7));
            if(this.noPhysics) {
                this.moveTowardsClosestSpace(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0, this.getZ());
            }
        }

        if(!this.onGround() || this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-5F || (this.tickCount + this.getId()) % 4 == 0) {
            this.move(MoverType.SELF, this.getDeltaMovement());
            float g = 0.98F;
            if(this.onGround()) {
                g = this.level().getBlockState(new BlockPos((int) this.getX(), (int) (this.getY() - 1.0), (int) this.getZ())).getBlock().getFriction() * 0.98F;
            }

            this.setDeltaMovement(this.getDeltaMovement().multiply(g, 0.98, g));
            if(this.onGround()) {
                Vec3 vec3d2 = this.getDeltaMovement();
                if(vec3d2.y < 0.0) {
                    this.setDeltaMovement(vec3d2.multiply(1.0, -0.5, 1.0));
                }
            }
        }

        this.hasImpulse |= this.updateInWaterStateAndDoFluidPushing();
        if(!this.level().isClientSide()) {
            double d = this.getDeltaMovement().subtract(vec3d).lengthSqr();
            if(d > 0.01) {
                this.hasImpulse = true;
            }
        }
    }

    private void applyWaterBuoyancy() {
        Vec3 vec3d = this.getDeltaMovement();
        this.setDeltaMovement(vec3d.x * 0.99F, vec3d.y + (double) (vec3d.y < 0.06F ? 5.0E-4F : 0.0F), vec3d.z * 0.99F);
    }

    private void applyLavaBuoyancy() {
        Vec3 vec3d = this.getDeltaMovement();
        this.setDeltaMovement(vec3d.x * 0.95F, vec3d.y + (double) (vec3d.y < 0.06F ? 5.0E-4F : 0.0F), vec3d.z * 0.95F);
    }

    @Override
    public void playerTouch(Player player) {
        if(!this.level().isClientSide()) {
            if(!this.getBoundingBox().intersects(player.getBoundingBox())) {
                return;
            }

            if(player.getUUID().equals(this.entityData.get(OWNER).orElse(null))) {
                if(this.items != null) {
                    for(int i = 0; i < this.items.getContainerSize(); ++i) {
                        ItemStack itemStack = this.items.getItem(i);
                        if(itemStack.isEmpty()) continue;
                        this.spawnAtLocation(itemStack);
                    }
                }
                this.discard();
            }
        }
    }

    @Nullable
    @Override
    public ItemEntity spawnAtLocation(ItemStack stack, float yOffset) {
        if(stack.isEmpty()) {
            return null;
        }
        if(this.level().isClientSide()) {
            return null;
        }
        ItemEntity itemEntity = new ItemEntity(this.level(), this.getX(), this.getY() + (double) yOffset, this.getZ(), stack);
        itemEntity.setNoPickUpDelay();
        this.level().addFreshEntity(itemEntity);
        return itemEntity;
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public boolean isCurrentlyGlowing() {
        return DeathkeeperConfig.highlightGraves && this.level().isClientSide() && this.getOwnerUuid() != null || super.isCurrentlyGlowing();
    }

    @Nullable
    public UUID getOwnerUuid() {
        return this.entityData.get(OWNER).orElse(null);
    }

    public void setOwnerUuid(@Nullable UUID uuid) {
        this.entityData.set(OWNER, Optional.ofNullable(uuid));
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(OWNER, Optional.empty());
        this.entityData.define(INVENTORY, new CompoundTag());
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        if(this.getOwnerUuid() != null) {
            nbt.putUUID("Owner", this.getOwnerUuid());
        }
        ListTag nbtList = new ListTag();

        for(int i = 0; i < this.items.getContainerSize(); ++i) {
            ItemStack itemStack = this.items.getItem(i);
            if(!itemStack.isEmpty()) {
                CompoundTag nbtCompound = new CompoundTag();
                nbtCompound.putByte("Slot", (byte) i);
                itemStack.save(nbtCompound);
                nbtList.add(nbtCompound);
            }
        }

        nbt.put("Items", nbtList);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        this.setOwnerUuid(nbt.getUUID("Owner"));
        ListTag nbtList = nbt.getList("Items", Tag.TAG_COMPOUND);

        for(int i = 0; i < nbtList.size(); ++i) {
            CompoundTag nbtCompound = nbtList.getCompound(i);
            int j = nbtCompound.getByte("Slot") & 255;
            if(j < this.items.getContainerSize()) {
                this.items.setItem(j, ItemStack.of(nbtCompound));
            }
        }
    }

    public float getRotation(float tickDelta) {
        return (this.tickCount + tickDelta) / 20.0F + this.uniqueOffset;
    }

    @NotNull
    @Override
    public SoundSource getSoundSource() {
        return SoundSource.AMBIENT;
    }

    @Override
    public float getVisualRotationYInDegrees() {
        return 180.0F - this.getRotation(0.5F) / (float) (Math.PI * 2) * 360.0F;
    }

    @Override
    public void containerChanged(Container sender) {
    }
}
