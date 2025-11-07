package aqario.deathkeeper.common.entity;

import aqario.deathkeeper.common.Deathkeeper;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class DeathkeeperEntityType {
    public static final EntityType<GraveEntity> GRAVE = register("grave",
        FabricEntityTypeBuilder.create()
            .entityFactory(GraveEntity::new)
            .spawnGroup(MobCategory.MISC)
            .dimensions(EntityDimensions.fixed(0.5F, 0.5F))
    );

    private static <T extends Entity> EntityType<T> register(String id, FabricEntityTypeBuilder<T> builder) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, new ResourceLocation(Deathkeeper.ID, id), builder.build());
    }

    public static void init() {
    }
}
