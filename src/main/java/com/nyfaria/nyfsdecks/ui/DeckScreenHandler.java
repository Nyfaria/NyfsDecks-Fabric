package com.nyfaria.nyfsdecks.ui;

import com.nyfaria.nyfsdecks.NyfsDecks;
import com.nyfaria.nyfsdecks.api.Dimension;
import com.nyfaria.nyfsdecks.api.Point;
import com.nyfaria.nyfsdecks.config.DeckInfo;
import com.nyfaria.nyfsdecks.item.DeckItem;
import com.nyfaria.nyfsdecks.item.DeckTile;
import com.nyfaria.nyfsdecks.util.InventoryUtils;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class DeckScreenHandler extends ScreenHandler {

    private final ItemStack deckStack;
    private final int padding = 8;
    private final int titleSpace = 10;
    
    public DeckScreenHandler(int synchronizationID, PlayerInventory playerInventory, PacketByteBuf packetByteBuf) {
        this(synchronizationID, playerInventory, packetByteBuf.readItemStack());
    }

    public DeckScreenHandler(int synchronizationID, PlayerInventory playerInventory, ItemStack deckStack) {
        super(NyfsDecks.CONTAINER_TYPE, synchronizationID);
        this.deckStack = deckStack;

        if (deckStack.getItem() instanceof DeckItem) {
            setupContainer(playerInventory, deckStack);
        } else {
            PlayerEntity player = playerInventory.player;
            this.close(player);
        }
    }
    public DeckScreenHandler(int synchronizationID, PlayerInventory playerInventory, DeckTile deckTile )
    {
        super(NyfsDecks.CONTAINER_TYPE,synchronizationID);
        deckStack = ItemStack.EMPTY;
        setupContainer(playerInventory,deckTile);
    }
    
    private void setupContainer(PlayerInventory playerInventory, ItemStack deckStack) {
        DeckInfo tier = getItem().getTier();
        Dimension dimension = getDimension(tier);
        int rowWidth = tier.getRowWidth();
        int numberOfRows = tier.getNumberOfRows();

        NbtList tag = deckStack.getOrCreateTag().getList("Inventory", NbtType.COMPOUND);
        SimpleInventory inventory = new SimpleInventory(rowWidth * numberOfRows) {
            @Override
            public void markDirty() {
                deckStack.getOrCreateTag().put("Inventory", InventoryUtils.toTag(this));
                super.markDirty();
            }
        };

        InventoryUtils.fromTag(tag, inventory);

        for (int y = 0; y < numberOfRows; y++) {
            for (int x = 0; x < rowWidth; x++) {
                Point deckSlotPosition = getDeckSlotPosition(dimension, x, y, tier);
                addSlot(new DeckLockedSlot(inventory, y * rowWidth + x, deckSlotPosition.x + 1, deckSlotPosition.y + 1));
            }
        }
        
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                Point playerInvSlotPosition = getPlayerInvSlotPosition(dimension, x, y);
                this.addSlot(new DeckLockedSlot(playerInventory, x + y * 9 + 9, playerInvSlotPosition.x + 1, playerInvSlotPosition.y + 1));
            }
        }
        
        for (int x = 0; x < 9; ++x) {
            Point playerInvSlotPosition = getPlayerInvSlotPosition(dimension, x, 3);
            this.addSlot(new DeckLockedSlot(playerInventory, x, playerInvSlotPosition.x + 1, playerInvSlotPosition.y + 1));
        }
    }

    private void setupContainer(PlayerInventory playerInventory, DeckTile deckTile) {
        DeckInfo tier =deckTile.tier;
        Dimension dimension = getDimension(tier);
        int rowWidth = tier.getRowWidth();
        int numberOfRows = tier.getNumberOfRows();

        SimpleInventory inventory = deckTile.getInventory();

        for (int y = 0; y < numberOfRows; y++) {
            for (int x = 0; x < rowWidth; x++) {
                Point deckSlotPosition = getDeckSlotPosition(dimension, x, y, tier);
                addSlot(new DeckLockedSlot(inventory, y * rowWidth + x, deckSlotPosition.x + 1, deckSlotPosition.y + 1));
            }
        }

        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                Point playerInvSlotPosition = getPlayerInvSlotPosition(dimension, x, y);
                this.addSlot(new DeckLockedSlot(playerInventory, x + y * 9 + 9, playerInvSlotPosition.x + 1, playerInvSlotPosition.y + 1));
            }
        }

        for (int x = 0; x < 9; ++x) {
            Point playerInvSlotPosition = getPlayerInvSlotPosition(dimension, x, 3);
            this.addSlot(new DeckLockedSlot(playerInventory, x, playerInvSlotPosition.x + 1, playerInvSlotPosition.y + 1));
        }
    }














    public DeckItem getItem() {
        return (DeckItem) deckStack.getItem();
    }
    
    public Dimension getDimension(DeckInfo tier) {
        return new Dimension(padding * 2 + Math.max(tier.getRowWidth(), 9) * 18, padding * 2 + titleSpace * 2 + 8 + (tier.getNumberOfRows() + 4) * 18);
    }
    
    public Point getDeckSlotPosition(Dimension dimension, int x, int y, DeckInfo tier) {
        return new Point(dimension.getWidth() / 2 - tier.getRowWidth() * 9 + x * 18, padding + titleSpace + y * 18);
    }
    
    public Point getPlayerInvSlotPosition(Dimension dimension, int x, int y) {
        return new Point(dimension.getWidth() / 2 - 9 * 9 + x * 18, dimension.getHeight() - padding - 4 * 18 - 3 + y * 18 + (y == 3 ? 4 : 0));
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return deckStack.getItem() instanceof DeckItem;
    }
    
    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack toInsert = slot.getStack();
            itemStack = toInsert.copy();
            DeckInfo tier = getItem().getTier();
            if (index < tier.getNumberOfRows() * tier.getRowWidth()) {
                if (!this.insertItem(toInsert, tier.getNumberOfRows() * tier.getRowWidth(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(toInsert, 0, tier.getNumberOfRows() * tier.getRowWidth(), false)) {
                return ItemStack.EMPTY;
            }
            
            if (toInsert.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        
        return itemStack;
    }

    private class DeckLockedSlot extends Slot {

        public DeckLockedSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }
        
        @Override
        public boolean canTakeItems(PlayerEntity playerEntity) {
            return stackMovementIsAllowed(getStack());
        }
        
        @Override
        public boolean canInsert(ItemStack stack) {
            // If the "unstackables only" config option is turned on,
            // do not allow players to insert stacks with >1 max count.


            return stackMovementIsAllowed(stack);
        }

        private boolean stackMovementIsAllowed(ItemStack stack) {
            return !(stack.getItem() instanceof DeckItem) && stack != deckStack;
        }
    }
}
