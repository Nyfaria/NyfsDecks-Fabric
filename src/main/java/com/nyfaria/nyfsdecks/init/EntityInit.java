package com.nyfaria.nyfsdecks.init;

import com.nyfaria.nyfsdecks.NyfsDecks;
import com.nyfaria.nyfsdecks.item.DeckTile;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EntityInit {

    public static final BlockEntityType<DeckTile> DECK_TYPE = FabricBlockEntityTypeBuilder.create(DeckTile::new, BlockInit.BASIC_DECK).build(null);


    public static void registerEntities(){
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(NyfsDecks.MOD_ID,"card_deck"), DECK_TYPE);
    }
}
