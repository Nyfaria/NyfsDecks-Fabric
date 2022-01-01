package com.nyfaria.nyfsdecks;

import com.nyfaria.nyfsdecks.block.DeckBlock;
import com.nyfaria.nyfsdecks.config.NyfsDecksConfig;
import com.nyfaria.nyfsdecks.init.BlockInit;
import com.nyfaria.nyfsdecks.init.EntityInit;
import com.nyfaria.nyfsdecks.item.DeckItem;
import com.nyfaria.nyfsdecks.item.DeckTile;
import com.nyfaria.nyfsdecks.ui.DeckScreenHandler;
import com.nyfaria.nyfsdecks.config.DeckInfo;
import com.nyfaria.nyfsdecks.network.ServerNetworking;
import draylar.omegaconfig.OmegaConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NyfsDecks implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "nyfsdecks";
    public static final Identifier CONTAINER_ID = id("deck");
    public static final ItemGroup GROUP = FabricItemGroupBuilder.build(CONTAINER_ID, () -> new ItemStack(Registry.ITEM.get(id("card_deck"))));
    public static final NyfsDecksConfig CONFIG = OmegaConfig.register(NyfsDecksConfig.class);
    public static final ScreenHandlerType<DeckScreenHandler> CONTAINER_TYPE = ScreenHandlerRegistry.registerExtended(CONTAINER_ID, DeckScreenHandler::new);
    public static final List<Item> DECKS = new ArrayList<>();

    public static BlockEntityType<DeckTile> DECK_ENTITY_TYPE;

    @Override
    public void onInitialize() {
        EntityInit.registerEntities();
        BlockInit.registerBlocks();
        registerDecks();
        ServerNetworking.init();
    }

    private void registerDecks() {
        NyfsDecksConfig defaultConfig = new NyfsDecksConfig();

        for (DeckInfo deck : NyfsDecks.CONFIG.decks) {
            Item.Settings settings = new Item.Settings().group(NyfsDecks.GROUP).maxCount(1);


            DeckItem registered = Registry.register(Registry.ITEM, new Identifier(MOD_ID, deck.getName().toLowerCase() + "_deck"), new DeckItem(BlockInit.BASIC_DECK, deck, settings));
            DECKS.add(registered);
        }
    }

    public static Identifier id(String name) {
        return new Identifier("nyfsdecks", name);
    }
}
