package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V4067 extends NamespacedSchema {
    public V4067(int p_376142_, Schema p_376515_) {
        super(p_376142_, p_376515_);
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_376838_) {
        Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_376838_);
        map.remove("minecraft:boat");
        map.remove("minecraft:chest_boat");
        this.registerSimple(map, "minecraft:oak_boat");
        this.registerSimple(map, "minecraft:spruce_boat");
        this.registerSimple(map, "minecraft:birch_boat");
        this.registerSimple(map, "minecraft:jungle_boat");
        this.registerSimple(map, "minecraft:acacia_boat");
        this.registerSimple(map, "minecraft:cherry_boat");
        this.registerSimple(map, "minecraft:dark_oak_boat");
        this.registerSimple(map, "minecraft:mangrove_boat");
        this.registerSimple(map, "minecraft:bamboo_raft");
        this.registerChestBoat(map, "minecraft:oak_chest_boat");
        this.registerChestBoat(map, "minecraft:spruce_chest_boat");
        this.registerChestBoat(map, "minecraft:birch_chest_boat");
        this.registerChestBoat(map, "minecraft:jungle_chest_boat");
        this.registerChestBoat(map, "minecraft:acacia_chest_boat");
        this.registerChestBoat(map, "minecraft:cherry_chest_boat");
        this.registerChestBoat(map, "minecraft:dark_oak_chest_boat");
        this.registerChestBoat(map, "minecraft:mangrove_chest_boat");
        this.registerChestBoat(map, "minecraft:bamboo_chest_raft");
        return map;
    }

    private void registerChestBoat(Map<String, Supplier<TypeTemplate>> p_376752_, String p_376088_) {
        this.register(p_376752_, p_376088_, p_376233_ -> DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(this))));
    }
}
