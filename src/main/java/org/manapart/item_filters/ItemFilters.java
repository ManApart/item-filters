package org.manapart.item_filters;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.Teleporter;
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
    public static Item itemFiltersIcon = createIcon();

    public ItemFilters() {
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerBlocks);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerItems);
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        System.out.println("Registering blocks");
        if (!ForgeRegistries.BLOCKS.containsKey(itemFilterBlock.getRegistryName())) {
            ForgeRegistries.BLOCKS.register(itemFilterBlock);
        }
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        System.out.println("Registering items");
        if (!ForgeRegistries.ITEMS.containsKey(itemFilterItem.getRegistryName())) {
            ForgeRegistries.ITEMS.register(itemFilterItem);
            ForgeRegistries.ITEMS.register(itemFiltersIcon);
        }
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


}
