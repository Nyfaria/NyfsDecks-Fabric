package com.nyfaria.nyfsdecks.mixin;

import com.nyfaria.nyfsdecks.item.DeckItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ItemStack) {
            ItemStack thisStack = (ItemStack) (Object) this;
            ItemStack checkStack = (ItemStack) obj;

            Item thisStackItem = thisStack.getItem();
            Item checkStackItem = checkStack.getItem();

            if(thisStackItem instanceof DeckItem && checkStackItem instanceof DeckItem) {
                return true;
            }
        }

        return super.equals(obj);
    }
}
