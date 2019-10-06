package org.manapart.item_filters;

import net.minecraft.block.HopperBlock;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponent;

import javax.annotation.Nullable;
import java.util.function.Supplier;
import java.util.stream.IntStream;


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
//                this.updateHopper(() -> pullItems(this));
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

    private void attemptToPull(ItemStack item, IInventory sourceInventory) {
        if (!item.isEmpty() && item.isStackable()) {
            ResourceLocation matchName = item.getItem().getRegistryName();
            int desiredItemCount = item.getMaxStackSize() - item.getCount();
            if (desiredItemCount > 0) {
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


    private void attemptToPush(ItemStack item, IInventory destinationInventory) {
        if (!item.isEmpty() && item.isStackable() && item.getCount() > 1) {
            ResourceLocation matchName = item.getItem().getRegistryName();
            //Transfer first stack that matches this item
            for (int i = 0; i < destinationInventory.getSizeInventory(); i++) {
                ItemStack destItem = destinationInventory.getStackInSlot(i);
                if (destItem.isEmpty() || matchName.equals(destItem.getItem().getRegistryName())) {
                    int itemCount = Math.min(destItem.getMaxStackSize() - destItem.getCount(), item.getCount()-1);
                    if (itemCount > 0) {
                        if (destItem.isEmpty()){
                            destinationInventory.setInventorySlotContents(i, item.copy());
                            markDirty();
                            destItem = destinationInventory.getStackInSlot(i);
                        }

                        destItem.setCount(destItem.getCount() + itemCount);
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


//    private boolean isInventoryFull(IInventory inventoryIn) {
//        inventoryIn.getSizeInventory()
//        return func_213972_a(inventoryIn, side).allMatch((p_213970_1_) -> {
//            ItemStack itemstack = inventoryIn.getStackInSlot(p_213970_1_);
//            return itemstack.getCount() >= itemstack.getMaxStackSize();
//        });
//    }

    private boolean updateHopper(Supplier<Boolean> p_200109_1_) {
        if (this.world != null && !this.world.isRemote) {
            if (!this.isOnTransferCooldown() && this.getBlockState().get(HopperBlock.ENABLED)) {
                boolean flag = false;
                if (!this.isInventoryEmpty()) {
                    flag = this.transferItemsOut();
                }

                if (!this.isFull()) {
                    flag |= p_200109_1_.get();
                }

                if (flag) {
                    this.setTransferCooldown(8);
                    this.markDirty();
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    private boolean isInventoryEmpty() {
        for (ItemStack itemstack : this.getItems()) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public boolean isEmpty() {
        return this.isInventoryEmpty();
    }

    private boolean isFull() {
        for (ItemStack itemstack : this.getItems()) {
            if (itemstack.isEmpty() || itemstack.getCount() != itemstack.getMaxStackSize()) {
                return false;
            }
        }

        return true;
    }


    private boolean isInventoryFull(IInventory inventoryIn, Direction side) {
        return func_213972_a(inventoryIn, side).allMatch((p_213970_1_) -> {
            ItemStack itemstack = inventoryIn.getStackInSlot(p_213970_1_);
            return itemstack.getCount() >= itemstack.getMaxStackSize();
        });
    }

    private boolean transferItemsOut() {
        if (net.minecraftforge.items.VanillaInventoryCodeHooks.insertHook(this)) return true;
        IInventory iinventory = this.getInventoryForHopperTransfer();
        if (iinventory == null) {
            return false;
        } else {
            Direction direction = this.getBlockState().get(HopperBlock.FACING).getOpposite();
            if (this.isInventoryFull(iinventory, direction)) {
                return false;
            } else {
                for (int i = 0; i < this.getSizeInventory(); ++i) {
                    if (!this.getStackInSlot(i).isEmpty()) {
                        ItemStack itemstack = this.getStackInSlot(i).copy();
                        ItemStack itemstack1 = putStackInInventoryAllSlots(this, iinventory, this.decrStackSize(i, 1), direction);
                        if (itemstack1.isEmpty()) {
                            iinventory.markDirty();
                            return true;
                        }

                        this.setInventorySlotContents(i, itemstack);
                    }
                }

                return false;
            }
        }
    }

    private boolean isOnTransferCooldown() {
        return this.transferCooldown > 0;
    }

    @Nullable
    private IInventory getInventoryForHopperTransfer() {
        Direction direction = this.getBlockState().get(HopperBlock.FACING);
        return getInventoryAtPosition(this.getWorld(), this.pos.offset(direction));
    }

    private static IntStream func_213972_a(IInventory p_213972_0_, Direction p_213972_1_) {
        return p_213972_0_ instanceof ISidedInventory ? IntStream.of(((ISidedInventory) p_213972_0_).getSlotsForFace(p_213972_1_)) : IntStream.range(0, p_213972_0_.getSizeInventory());
    }


    public static boolean pullItems(IHopper hopper) {
        Boolean ret = net.minecraftforge.items.VanillaInventoryCodeHooks.extractHook(hopper);
        if (ret != null) return ret;
        IInventory iinventory = getSourceInventory(hopper);
        if (iinventory != null) {
            Direction direction = Direction.DOWN;
            return isInventoryEmpty(iinventory, direction) ? false : func_213972_a(iinventory, direction).anyMatch((p_213971_3_) -> {
                return pullItemFromSlot(hopper, iinventory, p_213971_3_, direction);
            });
        } else {
            for (ItemEntity itementity : getCaptureItems(hopper)) {
                if (captureItem(hopper, itementity)) {
                    return true;
                }
            }

            return false;
        }
    }

    /**
     * Pulls from the specified slot in the inventory and places in any available slot in the hopper. Returns true if the
     * entire stack was moved
     */
    private static boolean pullItemFromSlot(IHopper hopper, IInventory inventoryIn, int index, Direction direction) {
        ItemStack itemstack = inventoryIn.getStackInSlot(index);
        if (!itemstack.isEmpty() && canExtractItemFromSlot(inventoryIn, itemstack, index, direction)) {
            ItemStack itemstack1 = itemstack.copy();
            ItemStack itemstack2 = putStackInInventoryAllSlots(inventoryIn, hopper, inventoryIn.decrStackSize(index, 1), (Direction) null);
            if (itemstack2.isEmpty()) {
                inventoryIn.markDirty();
                return true;
            }

            inventoryIn.setInventorySlotContents(index, itemstack1);
        }

        return false;
    }


    private static boolean canExtractItemFromSlot(IInventory inventoryIn, ItemStack stack, int index, Direction side) {
        return !(inventoryIn instanceof ISidedInventory) || ((ISidedInventory) inventoryIn).canExtractItem(index, stack, side);
    }

    private static boolean isInventoryEmpty(IInventory inventoryIn, Direction side) {
        return func_213972_a(inventoryIn, side).allMatch((p_213973_1_) -> {
            return inventoryIn.getStackInSlot(p_213973_1_).isEmpty();
        });
    }

}
