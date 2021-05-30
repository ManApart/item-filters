package org.manapart.item_filters;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class ItemFilterBlock extends HopperBlock {
    public ItemFilterBlock() {
        super(createProps());
    }

    private static AbstractBlock.Properties createProps() {
        Material padMat = new Material.Builder(MaterialColor.COLOR_BLUE).build();
        AbstractBlock.Properties props = AbstractBlock.Properties.of(padMat);
        props.requiresCorrectToolForDrops();
        props.harvestTool(ToolType.PICKAXE);
        props.sound(SoundType.METAL);
        props.strength(4);
        return props;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ItemFilterEntity(false);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isClientSide) {
            TileEntity tileentity = world.getBlockEntity(pos);
            if (tileentity instanceof ItemFilterEntity) {
                player.openMenu((ItemFilterEntity) tileentity);
                player.awardStat(Stats.INSPECT_HOPPER);
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity tileentity = world.getBlockEntity(pos);
            if (tileentity instanceof ItemFilterEntity) {
                InventoryHelper.dropContents(world, pos, (ItemFilterEntity) tileentity);
//                worldIn.updateComparatorOutputLevel(pos, this);
            }

            super.onRemove(state, world, pos, newState, isMoving);
        }
    }

//        public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
//        TileEntity tileentity = worldIn.getBlockEntity(pos);
//        if (tileentity instanceof ItemFilterEntity) {
//            ((ItemFilterEntity) tileentity).onEntityCollision(entityIn);
//        }
//    }

}
