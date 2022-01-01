package com.nyfaria.nyfsdecks.network;

import com.nyfaria.nyfsdecks.NyfsDecks;
import com.nyfaria.nyfsdecks.item.DeckItem;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ServerNetworking {

    public static Identifier OPEN_BACKPACK = NyfsDecks.id("open_backpack");

    public static void init() {
        registerOpenBackpackPacketHandler();
    }

    private static void registerOpenBackpackPacketHandler() {
        ServerPlayNetworking.registerGlobalReceiver(OPEN_BACKPACK, ServerNetworking::receiveOpenBackpackPacket);
    }

    private static void receiveOpenBackpackPacket(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        ItemStack firstBackpackItemStack = Stream.concat(player.getInventory().offHand.stream(), player.getInventory().main.stream())
                .filter((itemStack) -> itemStack.getItem() instanceof DeckItem)
                .findFirst()
                .orElse(ItemStack.EMPTY);
        if (firstBackpackItemStack != ItemStack.EMPTY) {
            DeckItem.openScreen(player, firstBackpackItemStack);
        }
    }
}
