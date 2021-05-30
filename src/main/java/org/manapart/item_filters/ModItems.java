package org.manapart.item_filters;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import static org.manapart.item_filters.ItemFilters.*;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems {

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        if (!ForgeRegistries.ITEMS.containsKey(itemFilterItem.getRegistryName())) {
            ForgeRegistries.ITEMS.register(itemFilterItem);
            ForgeRegistries.ITEMS.register(itemFilterCornerItem);
            ForgeRegistries.ITEMS.register(itemFiltersIcon);
        }
    }
}
