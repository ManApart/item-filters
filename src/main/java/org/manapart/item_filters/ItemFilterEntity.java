package org.manapart.item_filters;

import net.minecraft.block.HopperBlock;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponent;

import javax.annotation.Nullable;


public class ItemFilterEntity extends HopperTileEntity {
    private int transferCooldown = -1;
    private long tickedGameTime;

    public ItemFilterEntity() {
        setCustomName(new TextComponent() {
            @Override
            public String getUnformattedComponentText() {
                return "Item Filter";
            }

            @Override
            public ITextComponent shallowCopy() {
                return null;
            }
        });
    }

    @Override
    public TileEntityType<?> getType() {
        return ItemFilters.tileType;
    }

    @Override
    public void tick() {
        if (this.world != null && !this.world.isRemote) {
            --this.transferCooldown;
            this.tickedGameTime = this.world.getGameTime();
            if (!this.isOnTransferCooldown()) {
                this.setTransferCooldown(0);
                filterItems();
            }

        }
    }

    private void filterItems() {
        pullItems();
        pushItems();
    }

    private void pullItems() {
        if (!this.isFull()) {
            IInventory sourceInventory = getSourceInventory(this);
            if (sourceInventory != null) {
                for (ItemStack item : this.getItems()) {
                    attemptToPull(item, sourceInventory);
                }
            }
        }
    }

    private void pushItems() {
        if (!this.isEmpty()) {
            IInventory destinationInventory = this.getInventoryForHopperTransfer();
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
                for (int i = 0; i < sourceInventory.getSizeInventory(); i++) {
                    ItemStack sourceItem = sourceInventory.getStackInSlot(i);
                    if (!sourceItem.isEmpty() && matchName.equals(sourceItem.getItem().getRegistryName())) {
                        int itemCount = Math.min(desiredItemCount, sourceItem.getCount());
                        item.setCount(item.getCount() + itemCount);
                        sourceItem.setCount(sourceItem.getCount() - itemCount);
                        if (sourceItem.isEmpty()) {
                            sourceInventory.markDirty();
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
            for (int i = 0; i < destinationInventory.getSizeInventory(); i++) {
                ItemStack destItem = destinationInventory.getStackInSlot(i);
                if (destItem.isEmpty() || matchName.equals(destItem.getItem().getRegistryName())) {
                    int itemCount = Math.min(destItem.getMaxStackSize() - destItem.getCount(), item.getCount() - 1);
                    if (itemCount > 0) {
                        if (destItem.isEmpty()) {
                            destinationInventory.setInventorySlotContents(i, item.copy());
                            destItem = destinationInventory.getStackInSlot(i);
                            destItem.setCount(itemCount);
                            markDirty();
                        } else {
                            destItem.setCount(destItem.getCount() + itemCount);
                        }

                        item.setCount(item.getCount() - itemCount);
                        if (destItem.isEmpty()) {
                            destinationInventory.markDirty();
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

    @Nullable
    private IInventory getInventoryForHopperTransfer() {
        Direction direction = this.getBlockState().get(HopperBlock.FACING);
        return getInventoryAtPosition(this.getWorld(), this.pos.offset(direction));
    }

}
