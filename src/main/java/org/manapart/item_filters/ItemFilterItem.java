package org.manapart.item_filters;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public class ItemFilterItem extends BlockItem {
    public ItemFilterItem(ItemFilterBlock block) {
        super(block, new Item.Properties().tab(ItemGroupIF.instance));
    }

}
