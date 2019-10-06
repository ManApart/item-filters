package org.manapart.item_filters;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ItemGroupIF extends ItemGroup {
    public static final ItemGroupIF instance = new ItemGroupIF(ItemGroup.GROUPS.length, "item_filters");

    private ItemGroupIF(int index, String label) {
        super(index, label);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ItemFilters.itemFiltersIcon);
    }

}
