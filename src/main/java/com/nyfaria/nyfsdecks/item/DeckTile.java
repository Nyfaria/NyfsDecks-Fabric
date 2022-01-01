package com.nyfaria.nyfsdecks.item;

import com.nyfaria.nyfsdecks.config.DeckInfo;
import com.nyfaria.nyfsdecks.config.NyfsDecksConfig;
import com.nyfaria.nyfsdecks.init.EntityInit;
import com.nyfaria.nyfsdecks.ui.DeckScreenHandler;
import com.nyfaria.nyfsdecks.util.InventoryUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.stream.IntStream;

public class DeckTile extends LootableContainerBlockEntity implements SidedInventory {

    private static final int[] AVAILABLE_SLOTS = IntStream.range(0, 27).toArray();
    public SimpleInventory inventory;
    NyfsDecksConfig ndc = new NyfsDecksConfig();
    public DeckInfo tier = ndc.decks.get(0);


    public DeckTile(BlockPos pos, BlockState state) {
        super(EntityInit.DECK_TYPE, pos, state);
        //this.inventory = new SimpleInventory(52);
        //InventoryUtils.fromTag(nbt,this.inventory);

    }


    public SimpleInventory getInventory(){
        return inventory;
    }
    public void setupInventory(NbtList nbt, DeckInfo type){
        this.inventory = new SimpleInventory(52);
        InventoryUtils.fromTag(nbt, this.inventory);
        this.tier = type;

    }


    @Override
    public int size() {
        return this.inventory.size();
    }


    private static void updateNeighborStates(World world, BlockPos pos, BlockState state) {
        state.updateNeighbors(world, pos, 3);
    }


    @Override
    protected Text getContainerName() {
        return new TranslatableText("container.shulkerBox");
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new DeckScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.readInventoryNbt(nbt);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        return this.writeInventoryNbt(nbt);
    }


    public void readInventoryNbt(NbtCompound nbt) {
        this.inventory = new SimpleInventory(52);
        if (!this.deserializeLootTable(nbt) && nbt.contains("Items", 9)) {
            InventoryUtils.fromTag(nbt.getList("inventory",NbtList.COMPOUND_TYPE), this.inventory);
        }
    }

    public NbtCompound writeInventoryNbt(NbtCompound nbt) {
            InventoryUtils.toTag(this.inventory);
        return nbt;
    }

    @Override
    protected DefaultedList<ItemStack> getInvStackList() {
        DefaultedList<ItemStack> n = DefaultedList.ofSize(52,ItemStack.EMPTY);
        for(int i = 0; i < 52; i++ )
        {
            n.set(i,inventory.getStack(i));
        }
        return n;
    }

    @Override
    protected void setInvStackList(DefaultedList<ItemStack> list) {
        inventory = new SimpleInventory(52);
        for(ItemStack i : list){
            inventory.addStack(i);
        }

    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return AVAILABLE_SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return !(Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock);
    }
    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
    }


}
