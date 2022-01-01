package com.nyfaria.nyfsdecks.item;

import com.nyfaria.nyfsdecks.block.DeckBlock;
import com.nyfaria.nyfsdecks.ui.DeckScreenHandler;
import com.nyfaria.nyfsdecks.NyfsDecks;
import com.nyfaria.nyfsdecks.config.DeckInfo;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DeckItem extends BlockItem {

    private final DeckInfo deck;

    public DeckItem(DeckBlock block, DeckInfo deck, Item.Settings settings) {
        super(block,settings);
        this.deck = deck;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);
        openScreen(user, user.getStackInHand(hand));
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    public static void openScreen(PlayerEntity player, ItemStack deckItemStack) {
        if(player.world != null && !player.world.isClient) {
            player.openHandledScreen(new ExtendedScreenHandlerFactory() {
                @Override
                public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
                    packetByteBuf.writeItemStack(deckItemStack);
                }

                @Override
                public Text getDisplayName() {
                    return new TranslatableText(deckItemStack.getItem().getTranslationKey());
                }

                @Override
                public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                    return new DeckScreenHandler(syncId, inv, deckItemStack);
                }
            });
        }
    }

    public DeckInfo getTier() {
        return deck;
    }
}
