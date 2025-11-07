package aqario.deathkeeper.common.entity;

import aqario.deathkeeper.common.Deathkeeper;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class DeathkeeperEntityType {
    public static final EntityType<GraveEntity> GRAVE = register("grave",
        FabricEntityTypeBuilder.create()
            .entityFactory(GraveEntity::new)
            .spawnGroup(SpawnGroup.MISC)
            .dimensions(EntityDimensions.fixed(0.5F, 0.5F))
    );

    private static <T extends Entity> EntityType<T> register(String id, FabricEntityTypeBuilder<T> builder) {
        return Registry.register(Registries.ENTITY_TYPE, new Identifier(Deathkeeper.ID, id), builder.build());
    }

    public static void init() {
    }
}
