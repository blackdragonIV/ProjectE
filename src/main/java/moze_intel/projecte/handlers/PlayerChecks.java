package moze_intel.projecte.handlers;

import com.google.common.collect.Lists;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.armor.GemArmor;
import moze_intel.projecte.utils.PELogger;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import java.util.Iterator;
import java.util.List;

public final class PlayerChecks
{
	private static List<EntityPlayerMP> flyChecks = Lists.newArrayList();
	private static List<EntityPlayerMP> fireChecks = Lists.newArrayList();
	private static List<EntityPlayerMP> stepChecks = Lists.newArrayList();

	public static void update()
	{
		Iterator<EntityPlayerMP> iter = flyChecks.iterator();

		while (iter.hasNext())
		{
			EntityPlayerMP player = iter.next();

			if (!canPlayerFly(player))
			{
				if (player.capabilities.allowFlying)
				{
					PlayerHelper.updateClientFlight(player, false);
				}

				iter.remove();
				PELogger.logDebug("Removed " + player.getCommandSenderName() + " from flight checks.");
			}
		}

		iter = fireChecks.iterator();

		while (iter.hasNext())
		{
			EntityPlayerMP player = iter.next();

			if (!isPlayerFireImmune(player))
			{
				if (player.isImmuneToFire())
				{
					PlayerHelper.setPlayerFireImmunity(player, false);
				}

				iter.remove();
				PELogger.logDebug("Removed " + player.getCommandSenderName() + " from fire checks.");
			}
		}

		iter = stepChecks.iterator();

		while (iter.hasNext())
		{
			EntityPlayerMP player = iter.next();

			if (!canPlayerStep(player))
			{
				player.stepHeight = 0.5f;
				PlayerHelper.updateClientStepHeight(player, 0.5F);

				iter.remove();
				PELogger.logDebug("Removed " + player.getCommandSenderName() + " from step checks.");
			}
		}
	}

	public static void onPlayerChangeDimension(EntityPlayerMP playerMP)
	{
		if (canPlayerFly(playerMP))
		{
			PlayerHelper.updateClientFlight(playerMP, true);
		}

		if (isPlayerFireImmune(playerMP))
		{
			PlayerHelper.setPlayerFireImmunity(playerMP, true);
		}

		if (canPlayerStep(playerMP))
		{
			playerMP.stepHeight = 1.0f;
			PlayerHelper.updateClientStepHeight(playerMP, 1.0F);
		}
	}

	public static void addPlayerFlyChecks(EntityPlayerMP player)
	{
		if (!flyChecks.contains(player))
		{
			flyChecks.add(player);
			PELogger.logDebug("Added " + player.getCommandSenderName() + " to flight checks.");
		}
	}
	
	public static void addPlayerFireChecks(EntityPlayerMP player)
	{
		if (!fireChecks.contains(player))
		{
			fireChecks.add(player);
			PELogger.logDebug("Added " + player.getCommandSenderName() + " to fire checks.");
		}
	}
	
	public static void addPlayerStepChecks(EntityPlayerMP player)
	{
		if (!stepChecks.contains(player))
		{
			stepChecks.add(player);
			PELogger.logDebug("Added " + player.getCommandSenderName() + " to step height checks.");
		}
	}

	public static void removePlayerFlyChecks(EntityPlayerMP player)
	{
		if (flyChecks.contains(player))
		{
			Iterator<EntityPlayerMP> iterator = flyChecks.iterator();
			
			while (iterator.hasNext())
			{
				if (iterator.next().equals(player))
				{
					iterator.remove();
					PELogger.logDebug("Removed " + player.getCommandSenderName() + " from flight checks.");
					return;
				}
			}
		}
	}
	
	public static void removePlayerFireChecks(EntityPlayerMP player)
	{
		if (fireChecks.contains(player))
		{
			Iterator<EntityPlayerMP> iterator = fireChecks.iterator();
			
			while (iterator.hasNext())
			{
				if (iterator.next().equals(player))
				{
					iterator.remove();
					PELogger.logDebug("Removed " + player + " from fire checks.");
					return;
				}
			}
		}
	}
	
	public static void removePlayerStepChecks(EntityPlayerMP player)
	{
		if (stepChecks.contains(player))
		{
			Iterator<EntityPlayerMP> iterator = stepChecks.iterator();
			
			while (iterator.hasNext())
			{
				if (iterator.next().equals(player))
				{
					iterator.remove();
					PELogger.logDebug("Removed " + player + " from step height checks.");
					return;
				}
			}
		}
	}
	
	public static void removePlayerFromLists(String username)
	{
		Iterator<EntityPlayerMP> iterator = flyChecks.iterator();
		
		while (iterator.hasNext())
		{
			if (iterator.next().getCommandSenderName().equals(username))
			{
				iterator.remove();
				break;
			}
		}
		
		iterator = fireChecks.iterator();
		
		while (iterator.hasNext())
		{
			if (iterator.next().getCommandSenderName().equals(username))
			{
				iterator.remove();
				break;
			}
		}
		
		iterator = stepChecks.iterator();
		
		while (iterator.hasNext())
		{
			if (iterator.next().getCommandSenderName().equals(username))
			{
				iterator.remove();
				break;
			}
		}
	}

	public static boolean isPlayerCheckedForStep(String player)
	{
		Iterator<EntityPlayerMP> iter = stepChecks.iterator();

		while (iter.hasNext())
		{
			if (iter.next().getCommandSenderName().equals(player))
			{
				return true;
			}
		}

		return false;
	}
	
	public static void clearLists()
	{
		flyChecks.clear();
		fireChecks.clear();
		stepChecks.clear();
	}
	
	private static boolean canPlayerFly(EntityPlayer player)
	{
		if (player.capabilities.isCreativeMode)
		{
			return true;
		}
		
		ItemStack boots = player.getCurrentArmor(0);
		
		if (boots != null && boots.getItem() == ObjHandler.gemFeet)
		{
			return true;
		}
		
		for (int i = 0; i <= 8; i++)
		{
			ItemStack stack = player.inventory.getStackInSlot(i);
			
			if (stack != null && stack.getItem() == ObjHandler.swrg)
			{
				return true;
			}
		}
		
		return false;
	}
	
	private static boolean isPlayerFireImmune(EntityPlayer player)
	{
		if (player.capabilities.isCreativeMode)
		{
			return true;
		}
		
		ItemStack chest = player.getCurrentArmor(2);
		
		if (chest != null && chest.getItem() == ObjHandler.gemChest)
		{
			return true;
		}
		
		for (int i = 0; i <= 8; i++)
		{
			ItemStack stack = player.inventory.getStackInSlot(i);
			
			if (stack != null && stack.getItem() == ObjHandler.volcanite)
			{
				return true;
			}
		}
		
		return false;
	}
	
	private static boolean canPlayerStep(EntityPlayer player)
	{
		ItemStack boots = player.getCurrentArmor(0);
		
		return (boots != null && boots.getItem() == ObjHandler.gemFeet && GemArmor.isStepAssistEnabled(boots));
	}
}
