package org.manapart.item_filters;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import static org.manapart.item_filters.ItemFilters.tileType;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntities {

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<TileEntityType<?>> event) {
//        if (!ForgeRegistries.TILE_ENTITIES.containsKey(tileType.getRegistryName())) {
        ForgeRegistries.TILE_ENTITIES.register(tileType);
//        }
    }
}
