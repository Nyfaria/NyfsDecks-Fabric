package com.nyfaria.nyfsdecks.init;

import com.nyfaria.nyfsdecks.NyfsDecks;
import com.nyfaria.nyfsdecks.block.DeckBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Material;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;



public class BlockInit {

    public static final DeckBlock BASIC_DECK = new DeckBlock(FabricBlockSettings.of(Material.WOOD));

    public static void registerBlocks()
    {
        Registry.register(Registry.BLOCK, new Identifier(NyfsDecks.MOD_ID,"card_deck"),BASIC_DECK);
    }
}
