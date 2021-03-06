package moze_intel.projecte.gameObjs.entity;

import cpw.mods.fml.common.network.NetworkRegistry;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.ParticlePKT;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.NovaExplosion;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityLensProjectile extends EntityThrowable
{
	private byte charge;
	
	public EntityLensProjectile(World world) 
	{
		super(world);
	}

	public EntityLensProjectile(World world, EntityLivingBase entity, byte charge) 
	{
		super(world, entity);
		this.charge = charge;
	}

	public EntityLensProjectile(World world, double x, double y, double z, byte charge) 
	{
		super(world, x, y, z);
		this.charge = charge;
	}
	
	@Override
	public void onUpdate()
	{
		super.onUpdate();
		
		if (this.worldObj.isRemote)
		{
			return;
		}

		if (this.isInWater())
		{
			this.playSound("random.fizz", 0.7F, 1.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
			PacketHandler.sendToAllAround(new ParticlePKT("largesmoke", posX, posY, posZ), new NetworkRegistry.TargetPoint(worldObj.provider.dimensionId, posX, posY, posZ, 32));
			this.setDead();
		}
	}
	
	@Override
	protected float getGravityVelocity()
	{	
		return 0;
	}

	@Override
	protected void onImpact(MovingObjectPosition mop) 
	{
		if (this.worldObj.isRemote) return;
		NovaExplosion explosion = new NovaExplosion(this.worldObj, this.getThrower(), this.posX, this.posY, this.posZ, Constants.EXPLOSIVE_LENS_RADIUS[charge]);
		explosion.doExplosionA();
		explosion.doExplosionB(true);
		this.setDead();
	}
	
	public void writeEntityToNBT(NBTTagCompound nbt)
	{
		super.writeEntityToNBT(nbt);
		nbt.setByte("Charge", charge);
	}
	
	public void readEntityFromNBT(NBTTagCompound nbt)
	{
		super.readEntityFromNBT(nbt);
		charge = nbt.getByte("Charge");
	}
}
