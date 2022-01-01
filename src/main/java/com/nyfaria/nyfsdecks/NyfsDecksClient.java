package com.nyfaria.nyfsdecks;

import com.nyfaria.nyfsdecks.ui.DeckHandledScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;

@Environment(EnvType.CLIENT)
public class NyfsDecksClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ScreenRegistry.register(NyfsDecks.CONTAINER_TYPE, DeckHandledScreen::new);
    }
}
