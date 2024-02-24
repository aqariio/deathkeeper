package aqario.deathkeeper.common.entity;

import aqario.deathkeeper.common.Deathkeeper;
import net.minecraft.entity.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.entity.api.QuiltEntityTypeBuilder;

public class DeathkeeperEntityType {
    public static final EntityType<GraveEntity> GRAVE = register("grave",
        QuiltEntityTypeBuilder.create()
            .entityFactory(GraveEntity::new)
            .spawnGroup(SpawnGroup.MISC)
            .setDimensions(EntityDimensions.fixed(0.5F, 0.5F))
    );

    private static <T extends Entity> EntityType<T> register(String id, QuiltEntityTypeBuilder<T> builder) {
        return Registry.register(Registry.ENTITY_TYPE, new Identifier(Deathkeeper.ID, id), builder.build());
    }

    public static void init() {
    }
}
