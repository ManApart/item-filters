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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponent;

import javax.annotation.Nullable;
import java.util.function.Supplier;
import java.util.stream.IntStream;


public class ItemFilterEntity extends HopperTileEntity {
    private NonNullList<ItemStack> inventory = NonNullList.withSize(5, ItemStack.EMPTY);
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
                this.updateHopper(() -> pullItems(this));
            }

        }
    }

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
        for (ItemStack itemstack : this.inventory) {
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
        for (ItemStack itemstack : this.inventory) {
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
