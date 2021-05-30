package org.manapart.item_filters;

import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(ItemFilters.MODID)
@Mod.EventBusSubscriber(modid = ItemFilters.MODID)
public class ItemFilters {

    public static final String MODID = "item_filters";
    public static final ItemFilterBlock itemFilterBlock = createBlock();
    public static final ItemFilterCornerBlock itemFilterCornerBlock = createCornerBlock();
    public static final ItemFilterItem itemFilterItem = createItem(itemFilterBlock);
    public static final ItemFilterCornerItem itemFilterCornerItem = createCornerItem(itemFilterCornerBlock);
    public static final TileEntityType<ItemFilterEntity> tileType = createEntityType(itemFilterBlock);
    public static Item itemFiltersIcon = createIcon();

    public ItemFilters() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    private static Item createIcon() {
        Item icon = new Item(new Item.Properties());
        icon.setRegistryName(MODID + ":if_icon");
        return icon;
    }

    private static ItemFilterBlock createBlock() {
        ItemFilterBlock filter = new ItemFilterBlock();
        filter.setRegistryName(new ResourceLocation(MODID + ":filter_block"));
        return filter;
    }

    private static ItemFilterCornerBlock createCornerBlock() {
        ItemFilterCornerBlock filter = new ItemFilterCornerBlock();
        filter.setRegistryName(new ResourceLocation(MODID + ":filter_block_corner"));
        return filter;
    }

    private static ItemFilterItem createItem(ItemFilterBlock block) {
        ItemFilterItem filter = new ItemFilterItem(block);
        filter.setRegistryName(new ResourceLocation(MODID + ":filter_item"));
        return filter;
    }

    private static ItemFilterCornerItem createCornerItem(ItemFilterCornerBlock block) {
        ItemFilterCornerItem filter = new ItemFilterCornerItem(block);
        filter.setRegistryName(new ResourceLocation(MODID + ":filter_item_corner"));
        return filter;
    }

    private static TileEntityType<ItemFilterEntity> createEntityType(ItemFilterBlock block) {
        TileEntityType.Builder<ItemFilterEntity> builder = TileEntityType.Builder.of(ItemFilterEntity::new, itemFilterBlock, itemFilterCornerBlock);
        TileEntityType<ItemFilterEntity> tileType = builder.build(null);
        tileType.setRegistryName(MODID, "item_filter");
        return tileType;
    }

}
