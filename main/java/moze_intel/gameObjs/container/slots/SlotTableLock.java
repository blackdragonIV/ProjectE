package moze_intel.gameObjs.container.slots;

import moze_intel.gameObjs.ObjHandler;
import moze_intel.gameObjs.items.ItemBase;
import moze_intel.gameObjs.tiles.TransmuteTile;
import moze_intel.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotTableLock extends Slot
{
	private TransmuteTile tile;
	
	public SlotTableLock(TransmuteTile tile, int par2, int par3, int par4) 
	{
		super(tile, par2, par3, par4);
		this.tile = tile;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return Utils.doesItemHaveEmc(stack);
	}
	
	@Override
	public void putStack(ItemStack stack)
	{
		if (stack == null)
		{
			return;
		}
		
		super.putStack(stack);
		
		if (stack.getItem() == ObjHandler.kleinStars)
		{
			int remainEmc = tile.getMaxEmc() - (int) Math.ceil(tile.getStoredEMC());
			
			if (ItemBase.getEmc(stack) >= remainEmc)
			{
				tile.addEmcWithPKT(remainEmc);
				ItemBase.removeEmc(stack, remainEmc);
			}
			else
			{
				tile.addEmcWithPKT(ItemBase.getEmc(stack));
				ItemBase.setEmc(stack, 0);
			}
			
	        tile.handleKnowledge(stack.copy());
	        return;
		}
		
		if (stack.getItem() != ObjHandler.tome)
		{
			tile.handleKnowledge(stack.copy());
		}
		else
		{
			tile.updateOutputs();
		}
	}
	
	@Override
	public void onPickupFromSlot(EntityPlayer par1EntityPlayer, ItemStack par2ItemStack)
	{
		super.onPickupFromSlot(par1EntityPlayer, par2ItemStack);
		
		tile.updateOutputs();
	}
	
	@Override
	public int getSlotStackLimit()
    {
		return 1;
    }
}
