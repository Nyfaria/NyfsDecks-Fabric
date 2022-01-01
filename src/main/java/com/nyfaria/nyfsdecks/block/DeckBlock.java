package com.nyfaria.nyfsdecks.block;

import com.google.common.collect.ImmutableMap;
import com.nyfaria.nyfsdecks.NyfsDecks;
import com.nyfaria.nyfsdecks.item.DeckItem;
import com.nyfaria.nyfsdecks.item.DeckTile;
import com.nyfaria.nyfsdecks.ui.DeckScreenHandler;
import com.nyfaria.nyfsdecks.util.InventoryUtils;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.VolatileCallSite;
import java.util.function.Function;

public class DeckBlock extends BlockWithEntity {

    protected static final VoxelShape FLOOR_AABB_X = Block.createCuboidShape(4.0D, 0.0D, 5.0D, 12.0D, 3.0D, 11.0D);
    protected static final VoxelShape FLOOR_AABB_Z = Block.createCuboidShape(5.0D, 0.0D, 4.0D, 11.0D, 3.0D, 12.0D);

    public DeckBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DeckTile(pos,state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return FLOOR_AABB_Z;
    }
    /*
    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof DeckTile) {
            DeckTile deckTile = (DeckTile)blockEntity;
            if (!world.isClient && player.isCreative() && !deckTile.isEmpty()) {
                ItemStack itemStack = new ItemStack(NyfsDecks.DECKS.get(0).asItem());
                NbtCompound nbtCompound = InventoryUtils.toTag(deckTile.getInventory()).getCompound(NbtType.COMPOUND);
                if (!nbtCompound.isEmpty()) {
                    itemStack.getOrCreateTag().put("Inventory", InventoryUtils.toTag(deckTile.getInventory()));
                }

                if (deckTile.hasCustomName()) {
                    itemStack.setCustomName(deckTile.getCustomName());
                }

                ItemEntity itemEntity = new ItemEntity(world, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, itemStack);
                itemEntity.setToDefaultPickupDelay();
                world.spawnEntity(itemEntity);
            } else {
                deckTile.checkLootInteraction(player);
            }
        }

        super.onBreak(world, pos, state, player);
    }*/


    //@Override
    //public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    //    user.setCurrentHand(hand);
    //    openScreen(user, user.getStackInHand(hand));
    //    return TypedActionResult.success(user.getStackInHand(hand));

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof DeckTile) {
            DeckTile deckTile = (DeckTile) blockEntity;
            player.setCurrentHand(hand);
            openScreen(player, deckTile);

        }
        return ActionResult.success(true);
    }

    public static void openScreen(PlayerEntity player, DeckTile deckTile) {
        if(player.world != null && !player.world.isClient) {
            player.openHandledScreen(new ExtendedScreenHandlerFactory() {
                @Override
                public Text getDisplayName() {
                    return deckTile.getDisplayName();
                }

                @Override
                public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
                    packetByteBuf.writeItemStack(ItemStack.EMPTY);
                }

                @Override
                public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                    return new DeckScreenHandler(syncId, inv, deckTile);
                }

            });
            //deckTile.createMenu(-1,player.getInventory(),player);
        }


    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof DeckTile) {
            DeckTile deckTile = (DeckTile) blockEntity;
            deckTile.setupInventory(itemStack.getOrCreateTag().getList("Inventory", NbtType.COMPOUND), ((DeckItem)itemStack.getItem()).getTier());
        }
    }
}
