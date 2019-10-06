package org.manapart.item_filters;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ItemFilters.MODID)
@Mod.EventBusSubscriber(modid = ItemFilters.MODID)
public class ItemFilters {

    public static final String MODID = "item_filters";
    public static final ItemFilterBlock itemFilterBlock = createBlock();
    public static final ItemFilterItem itemFilterItem = createItem(itemFilterBlock);
    public static final ItemFilterEntity itemFilterEntity = new ItemFilterEntity();
    public static final TileEntityType<ItemFilterEntity> tileType = createEntityType(itemFilterBlock);
    public static Item itemFiltersIcon = createIcon();

    public ItemFilters() {
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerBlocks);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerItems);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerEntities);
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        if (!ForgeRegistries.BLOCKS.containsKey(itemFilterBlock.getRegistryName())) {
            System.out.println("Registering blocks");
            ForgeRegistries.BLOCKS.register(itemFilterBlock);
        }
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        if (!ForgeRegistries.ITEMS.containsKey(itemFilterItem.getRegistryName())) {
            System.out.println("Registering items");
            ForgeRegistries.ITEMS.register(itemFilterItem);
            ForgeRegistries.ITEMS.register(itemFiltersIcon);
        }
    }

    @SubscribeEvent
    public void registerEntities(RegistryEvent.Register<TileEntityType<?>> event) {
//        if (!ForgeRegistries.TILE_ENTITIES.containsKey(tileType.getRegistryName())) {
            System.out.println("Registering TileEntityTypes");
            ForgeRegistries.TILE_ENTITIES.register(tileType);
//        }
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

    private static ItemFilterItem createItem(ItemFilterBlock block) {
        ItemFilterItem filter = new ItemFilterItem(block);
        filter.setRegistryName(new ResourceLocation(MODID + ":filter_item"));
        return filter;
    }

    private static TileEntityType<ItemFilterEntity> createEntityType(ItemFilterBlock block) {
        TileEntityType.Builder<ItemFilterEntity> builder = TileEntityType.Builder.create(ItemFilterEntity::new, itemFilterBlock);
        TileEntityType<ItemFilterEntity> tileType = builder.build(null);
        tileType.setRegistryName(MODID, "item_filter");
        return tileType;
    }

}
