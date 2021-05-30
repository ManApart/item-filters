package org.manapart.item_filters;

import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nullable;


public class ItemFilterEntity extends HopperTileEntity {

    private int transferCooldown = -1;
    private boolean isCorner = false;

    public ItemFilterEntity() {
    }

    public ItemFilterEntity(boolean isCorner) {
        this.isCorner = isCorner;
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        super.load(state, compound);
        this.isCorner = compound.getBoolean("IsCorner");
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        super.save(compound);
        compound.putBoolean("IsCorner", this.isCorner);
        return compound;
    }

    @Override
    public TileEntityType<?> getType() {
        return ItemFilters.tileType;
    }

    @Override
    public void tick() {
//        if (this.world != null && !this.world.isRemote) {
            --this.transferCooldown;
            if (!this.isOnTransferCooldown()) {
                suckInItems(this);
                setCooldown(0);
                filterItems();
            }
//        }
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new StringTextComponent("Item Filter");
    }

    private void filterItems() {
        pullItems();
        pushItems();
    }

    private void pullItems() {
        if (!this.isFull()) {
            IInventory sourceInventory = this.getInventoryToPullItemsFrom();
            if (sourceInventory != null) {
                for (ItemStack item : this.getItems()) {
                    attemptToPull(item, sourceInventory);
                }
            }
        }
    }

    private void pushItems() {
        if (!this.isEmpty()) {
            IInventory destinationInventory = this.getInventoryToPushItemsTo();
            if (destinationInventory != null) {
                for (ItemStack item : this.getItems()) {
                    attemptToPush(item, destinationInventory);
                }
            }
        }
    }

    private void attemptToPull(ItemStack item, IInventory sourceInventory) {
        if (!item.isEmpty() && item.isStackable()) {
            int desiredItemCount = item.getMaxStackSize() - item.getCount();
            if (desiredItemCount > 0) {
                ResourceLocation matchName = item.getItem().getRegistryName();
                //Transfer first stack that matches this item
                for (int i = 0; i < sourceInventory.getContainerSize(); i++) {
                    ItemStack sourceItem = sourceInventory.getItem(i);
                    if (!sourceItem.isEmpty() && matchName.equals(sourceItem.getItem().getRegistryName())) {
                        int itemCount = Math.min(desiredItemCount, sourceItem.getCount());
                        item.setCount(item.getCount() + itemCount);
                        sourceItem.setCount(sourceItem.getCount() - itemCount);
                        if (sourceItem.isEmpty()) {
                            sourceInventory.setChanged();
                        }
                        break;
                    }
                }
            }
        }
    }

    private void attemptToPush(ItemStack item, IInventory destinationInventory) {
        if (!item.isEmpty() && item.isStackable() && item.getCount() > 1) {
            ResourceLocation matchName = item.getItem().getRegistryName();
            for (int i = 0; i < destinationInventory.getContainerSize(); i++) {
                ItemStack destItem = destinationInventory.getItem(i);
                if (destItem.isEmpty() || matchName.equals(destItem.getItem().getRegistryName())) {
                    int itemCount = Math.min(destItem.getMaxStackSize() - destItem.getCount(), item.getCount() - 1);
                    if (itemCount > 0) {
                        if (destItem.isEmpty()) {
                            destinationInventory.setItem(i, item.copy());
                            destItem = destinationInventory.getItem(i);
                            destItem.setCount(itemCount);
                            setChanged();
                        } else {
                            destItem.setCount(destItem.getCount() + itemCount);
                        }

                        item.setCount(item.getCount() - itemCount);
                        if (destItem.isEmpty()) {
                            destinationInventory.setChanged();
                        }
                        break;
                    }
                }
            }
        }
    }

    private boolean isFull() {
        for (ItemStack itemstack : this.getItems()) {
            if (itemstack.isEmpty() || itemstack.getCount() != itemstack.getMaxStackSize()) {
                return false;
            }
        }

        return true;
    }

    private boolean isOnTransferCooldown() {
        return this.transferCooldown > 0;
    }

//    protected Container createMenu(int p_213906_1_, PlayerInventory p_213906_2_) {
//        return new HopperContainer(p_213906_1_, p_213906_2_, this);
//    }

    private IInventory getInventoryToPushItemsTo() {
//        Direction direction = this.getBlockState().get(HopperBlock.FACING);
        Direction direction = this.getBlockState().getValue(HopperBlock.FACING);
        if (isCorner) {
            direction = Direction.DOWN;
        }

        return getContainerAt(getLevel(), getBlockPos().offset(direction.getNormal()));
//        return getInventoryAtPosition(this.getWorld(), this.getBlockPos().offset(direction.getNormal()));
    }

    private IInventory getInventoryToPullItemsFrom() {
        Direction direction = this.getBlockState().getValue(HopperBlock.FACING).getOpposite();
        if (isCorner) {
            direction = direction.getOpposite();
        }
        return getContainerAt(getLevel(), getBlockPos().offset(direction.getNormal()));
//        return getInventoryAtPosition(this.getWorld(), this.getBlockPos().offset(direction.getNormal()));
    }

}
