package org.manapart.item_filters;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class ItemFilterCornerBlock extends ItemFilterBlock {

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ItemFilterEntity(true);
    }
}
