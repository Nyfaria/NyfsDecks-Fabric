package com.nyfaria.nyfsdecks.config;

import draylar.omegaconfig.api.Comment;
import draylar.omegaconfig.api.Config;
import net.minecraft.sound.SoundEvents;

import java.util.Arrays;
import java.util.List;

public class NyfsDecksConfig implements Config {
    public List<DeckInfo> decks = Arrays.asList(
            DeckInfo.of("basic", 13, 4, false, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER)
    );


    @Override
    public String getName() {
        return "nyfsdecks";
    }
}
