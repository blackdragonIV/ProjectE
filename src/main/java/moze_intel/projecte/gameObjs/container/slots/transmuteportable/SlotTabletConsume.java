package moze_intel.projecte.gameObjs.container.slots.transmuteportable;

import moze_intel.projecte.gameObjs.container.inventory.TransmuteTabletInventory;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotTabletConsume extends Slot
{
	private TransmuteTabletInventory table;
	
	public SlotTabletConsume(TransmuteTabletInventory table, int par2, int par3, int par4) 
	{
		super(table, par2, par3, par4);
		this.table = table;
	}
	
	@Override
	public void putStack(ItemStack stack)
	{
		if (stack == null)
		{
			return;
		}
		
		ItemStack cache = stack.copy();
		
		double toAdd = 0;
		
		while (!table.hasMaxedEmc() && stack.stackSize > 0)
		{
			toAdd += EMCHelper.getEmcValue(stack);
			stack.stackSize--;
		}
		
		table.addEmc(toAdd);
		this.onSlotChanged();
		table.handleKnowledge(cache);
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return !table.hasMaxedEmc() && EMCHelper.doesItemHaveEmc(stack);
	}
}
