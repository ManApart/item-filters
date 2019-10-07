package org.manapart.item_filters;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class ItemFilterCornerBlock extends ItemFilterBlock {

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new ItemFilterEntity(true);
    }
}
