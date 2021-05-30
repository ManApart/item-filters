package org.manapart.item_filters;

import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import static org.manapart.item_filters.ItemFilters.itemFilterBlock;
import static org.manapart.item_filters.ItemFilters.itemFilterCornerBlock;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBlocks {

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        if (!ForgeRegistries.BLOCKS.containsKey(itemFilterBlock.getRegistryName())) {
            ForgeRegistries.BLOCKS.register(itemFilterBlock);
            ForgeRegistries.BLOCKS.register(itemFilterCornerBlock);
        }
    }
}
