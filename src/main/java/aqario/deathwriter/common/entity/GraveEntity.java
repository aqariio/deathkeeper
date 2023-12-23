package aqario.deathwriter.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class GraveEntity extends Entity {
    public final float uniqueOffset;

    public GraveEntity(EntityType<?> type, World world) {
        super(type, world);
        this.uniqueOffset = this.random.nextFloat() * (float) Math.PI * 2.0F;
    }

    @Override
    protected Entity.MoveEffect getMoveEffect() {
        return Entity.MoveEffect.NONE;
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        this.setCustomName(player.getDisplayName());
        player.sendMessage(this.getDisplayName(), true);
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
        this.setVelocity(vec3d.x * 0.99F, vec3d.y + (double)(vec3d.y < 0.06F ? 5.0E-4F : 0.0F), vec3d.z * 0.99F);
    }

    private void applyLavaBuoyancy() {
        Vec3d vec3d = this.getVelocity();
        this.setVelocity(vec3d.x * 0.95F, vec3d.y + (double)(vec3d.y < 0.06F ? 5.0E-4F : 0.0F), vec3d.z * 0.95F);
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

    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }

    public float getRotation(float tickDelta) {
        return (this.age + tickDelta) / 20.0F + this.uniqueOffset;
    }

    @Override
    public float getVisualYaw() {
        return 180.0F - this.getRotation(0.5F) / (float) (Math.PI * 2) * 360.0F;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}
