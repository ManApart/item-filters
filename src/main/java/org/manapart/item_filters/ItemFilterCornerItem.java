package org.manapart.item_filters;

import net.minecraft.item.BlockItem;

public class ItemFilterCornerItem extends BlockItem {
    public ItemFilterCornerItem(ItemFilterCornerBlock block) {
        super(block, new Properties().tab(ItemGroupIF.instance));
    }

}
