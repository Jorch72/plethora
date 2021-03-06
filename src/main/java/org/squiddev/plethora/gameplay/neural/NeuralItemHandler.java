package org.squiddev.plethora.gameplay.neural;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.squiddev.plethora.gameplay.ItemBase;

import static org.squiddev.plethora.api.Constants.*;
import static org.squiddev.plethora.gameplay.neural.ItemComputerHandler.DIRTY;
import static org.squiddev.plethora.gameplay.neural.ItemComputerHandler.ITEMS;

/**
 * A horrible item handler implementation that saves to the item's NBT
 * rather than the capability. See #12
 */
public class NeuralItemHandler implements IItemHandler, IItemHandlerModifiable {
	private final ItemStack stack;

	public NeuralItemHandler(ItemStack stack) {
		this.stack = stack;
	}

	private void validateSlotIndex(int slot) {
		if (slot < 0 || slot >= getSlots()) {
			throw new RuntimeException("Slot " + slot + " not in valid range - [0," + getSlots() + ")");
		}
	}

	@Override
	public int getSlots() {
		return NeuralHelpers.INV_SIZE;
	}

	protected int getStackLimit(int slot, ItemStack stack) {
		return 1;
	}

	public boolean isItemValid(int slot, ItemStack stack) {
		if (stack == null) return false;

		if (slot < NeuralHelpers.PERIPHERAL_SIZE) {
			return stack.hasCapability(PERIPHERAL_HANDLER_CAPABILITY, null) ||
				stack.hasCapability(PERIPHERAL_CAPABILITY, null);
		} else {
			return stack.hasCapability(MODULE_HANDLER_CAPABILITY, null);
		}
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		validateSlotIndex(slot);

		NBTTagCompound tag = ItemBase.getTag(this.stack);
		NBTTagCompound items = getItems(tag);
		if (stack == null) {
			items.removeTag("item" + slot);
		} else {
			items.setTag("item" + slot, stack.serializeNBT());
		}

		tag.setShort(DIRTY, (short) (tag.getShort(DIRTY) | 1 << slot));
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		validateSlotIndex(slot);

		NBTTagCompound tag = ItemBase.getTag(this.stack);
		NBTTagCompound items = getItems(tag);
		if (items.hasKey("item" + slot, 10)) {
			return ItemStack.loadItemStackFromNBT(items.getCompoundTag("item" + slot));
		} else {
			return null;
		}
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (stack == null || getStackInSlot(slot) != null) return stack;

		if (isItemValid(slot, stack)) {
			return doInsert(slot, stack, simulate);
		} else {
			return stack;
		}
	}

	private ItemStack doInsert(int slot, ItemStack stack, boolean simulate) {
		if (stack == null || stack.stackSize == 0) return null;

		validateSlotIndex(slot);

		ItemStack existing = getStackInSlot(slot);
		int limit = getStackLimit(slot, stack);

		if (existing != null) {
			if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) {
				return stack;
			}

			limit -= existing.stackSize;
		}

		if (limit <= 0) return stack;

		boolean reachedLimit = stack.stackSize > limit;

		if (!simulate) {
			if (existing == null) {
				setStackInSlot(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
			} else {
				existing.stackSize += reachedLimit ? limit : stack.stackSize;
				setStackInSlot(slot, existing);
			}
		}

		return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.stackSize - limit) : null;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (amount == 0) return null;

		validateSlotIndex(slot);

		ItemStack existing = getStackInSlot(slot);

		if (existing == null) return null;

		int toExtract = Math.min(amount, existing.getMaxStackSize());

		if (existing.stackSize <= toExtract) {
			if (!simulate) {
				setStackInSlot(slot, null);
			}
			return existing;
		} else {
			if (!simulate) {
				setStackInSlot(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.stackSize - toExtract));
			}

			return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
		}
	}

	private static NBTTagCompound getItems(NBTTagCompound tag) {
		NBTTagCompound items;
		if (tag.hasKey(ITEMS, 10)) {
			items = tag.getCompoundTag(ITEMS);
		} else {
			tag.setTag(ITEMS, items = new NBTTagCompound());
		}

		return items;
	}

}
