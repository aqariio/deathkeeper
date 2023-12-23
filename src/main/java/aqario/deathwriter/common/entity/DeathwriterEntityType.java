package aqario.deathwriter.common.entity;

import aqario.deathwriter.common.Deathwriter;
import net.minecraft.entity.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.entity.api.QuiltEntityTypeBuilder;

public class DeathwriterEntityType {
    public static final EntityType<GraveEntity> GRAVE = register("grave",
        QuiltEntityTypeBuilder.create()
            .entityFactory(GraveEntity::new)
            .spawnGroup(SpawnGroup.MISC)
            .setDimensions(EntityDimensions.fixed(0.5F, 0.5F))
    );

    private static <T extends Entity> EntityType<T> register(String id, QuiltEntityTypeBuilder<T> builder) {
        return Registry.register(Registry.ENTITY_TYPE, new Identifier(Deathwriter.ID, id), builder.build());
    }

    public static void init() {
    }
}
