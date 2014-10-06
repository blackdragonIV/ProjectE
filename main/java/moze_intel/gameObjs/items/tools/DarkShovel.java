package moze_intel.gameObjs.items.tools;

import java.util.ArrayList;
import java.util.List;

import moze_intel.gameObjs.entity.LootBall;
import moze_intel.gameObjs.items.ItemCharge;
import moze_intel.network.PacketHandler;
import moze_intel.network.packets.SwingItemPKT;
import moze_intel.utils.CoordinateBox;
import moze_intel.utils.Coordinates;
import moze_intel.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class DarkShovel extends ItemCharge
{
	public DarkShovel() 
	{
		super("dm_shovel", (byte) 3);
	}
	
	@Override
	public boolean canHarvestBlock(Block block, ItemStack stack)
	{
		if (block == Blocks.obsidian)
		{
			return false;
		}
		
		String harvest = block.getHarvestTool(0);
		
		if (harvest == null || harvest.equals("shovel"))
		{
			return true;
		}
		
		return false;
	}
	
	@Override
	public float getDigSpeed(ItemStack stack, Block block, int metadata)
	{
		if(block.getHarvestTool(metadata) != null && block.getHarvestTool(metadata).equals("shovel"))
		{
			return 14.0f + (12.0f * this.getCharge(stack));
		}
		
		return 1.0F;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, false);
			
			if (mop != null && mop.typeOfHit.equals(MovingObjectType.BLOCK))
			{
				CoordinateBox box = getRelativeBox(new Coordinates(mop), ForgeDirection.getOrientation(mop.sideHit), this.getCharge(stack) + 1);
				List<ItemStack> drops = new ArrayList();
				byte charge = this.getCharge(stack);

				for (int x = (int) box.minX; x <= box.maxX; x++)
					for (int y = (int) box.minY; y <= box.maxY; y++)
						for (int z = (int) box.minZ; z <= box.maxZ; z++)
						{
							Block block = world.getBlock(x, y, z);
							
							if (block == Blocks.air || !canHarvestBlock(block, stack))
							{
								continue;
							}
							
							ArrayList<ItemStack> blockDrops = Utils.getBlockDrops(world, player, block, stack, x, y, z);
							
							if (!blockDrops.isEmpty())
							{
								drops.addAll(blockDrops);
							}
							
							world.setBlockToAir(x, y, z);
						}

				if (!drops.isEmpty())
				{
					world.spawnEntityInWorld(new LootBall(world, drops, player.posX, player.posY, player.posZ));
					PacketHandler.sendTo(new SwingItemPKT(), (EntityPlayerMP) player);
				}
			}
		}
		
		return stack;
	}
	
	private CoordinateBox getRelativeBox(Coordinates coords, ForgeDirection direction, int charge)
	{
		if (direction.offsetX != 0)
		{
			return new CoordinateBox(coords.x, coords.y - charge, coords.z - charge, coords.x, coords.y + charge, coords.z + charge);
		}
		else if (direction.offsetY != 0)
		{
			return new CoordinateBox(coords.x - charge, coords.y, coords.z - charge, coords.x + charge, coords.y, coords.z + charge);
		}
		else
		{
			return new CoordinateBox(coords.x - charge, coords.y - charge, coords.z, coords.x + charge, coords.y + charge, coords.z);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public boolean isFull3D()
    {
		return true;
    }

	@Override
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register)
	{
		this.itemIcon = register.registerIcon(this.getTexture("dm_tools", "shovel"));
	}
}
