package buildcraft.additionalpipes.pipes;

import java.util.Arrays;
import java.util.List;

import buildcraft.additionalpipes.utils.Log;
import buildcraft.core.lib.RFBattery;
import buildcraft.core.lib.utils.Utils;
import buildcraft.core.proxy.CoreProxy;
import buildcraft.transport.PipeTransportFluids;
import buildcraft.transport.TransportProxy;
import buildcraft.transport.TravelingItem;
import buildcraft.transport.pipes.events.PipeEventItem;
import cofh.api.energy.IEnergyHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

public class PipeLiquidsObsidian extends APPipe<PipeTransportFluids> implements IEnergyHandler 
{
	// 		return battery.receiveEnergy(maxReceive, simulate);

	private static final int ICON = 31;
	
	AxisAlignedBB searchBox;
	private RFBattery battery = new RFBattery(2560, 640, 0);

	//rolling queue of entity IDs to avoid picking up
	private int[] entitiesDropped;
	private int entitiesDroppedIndex = 0;
	
	//used to output fluids over time to the pipe system
	private FluidStack fluidInItem = null;

	//item that the pipe is currently holding
	private ItemStack currentItem = null;
	
	public PipeLiquidsObsidian(Item item)
	{
		super(new PipeTransportFluids(), item);
		
		//load the fluid capacities set in mod init
		transport.initFromPipe(getClass());
		
		entitiesDropped = new int[32];
		Arrays.fill(entitiesDropped, -1);
	}

	@Override
	public int getIconIndex(EnumFacing direction)
	{
		return ICON;
	}

	@Override
	public void onEntityCollidedWithBlock(Entity entity) {
		super.onEntityCollidedWithBlock(entity);

		if (entity.isDead) {
			return;
		}
		
		if(entity instanceof EntityItem)
		{
			EntityItem entityItem = (EntityItem) entity;
			if(canSuck(entityItem, 0)) 
			{
				pullItemIntoPipe(entityItem, 0);
			}
		}
	}

	private AxisAlignedBB getSuckingBox(EnumFacing orientation, int distance) {
		Vec3 p1 = new Vec3(container.getPos());
		Vec3 p2 = new Vec3(container.getPos());

		switch (orientation) {
			case EAST:
				p1.addVector(distance, 0, 0);
				p2.addVector(distance + 1, 0, 0);

				break;
			case WEST:
				p1.subtract(distance - 1, 0, 0);
				p2.addVector(distance, 0, 0);
				break;
			case UP:
			case DOWN:
				p1.addVector(distance + 1, 0, distance + 1);
				p2.subtract(distance, 0, distance);
				break;
			case SOUTH:
				p1.addVector(0, 0, distance);
				p2.addVector(0, 0, distance + 1);
				break;
			case NORTH:
			default:
				p1.subtract(0, 0, distance - 1);
				p2.subtract(0, 0, distance);
				break;
		}

		switch (orientation) {
			case EAST:
			case WEST:
				
				p1.addVector(0, distance + 1, distance + 1);
				p2.subtract(0, distance, distance);
				break;
			case UP:
				p1.addVector(0, distance + 1, 0);
				p2.addVector(0, distance, 0);
				break;
			case DOWN:
				p1.subtract(0, distance - 1, 0);
				p2.subtract(0, distance, 0);
				break;
			case SOUTH:
			case NORTH:
			default:
				
				p1.addVector(distance + 1, distance + 1, 0);
				p2.subtract(distance, distance, 0);
				break;
		}

		Vec3 min = Utils.min(p1, p2);
		Vec3 max = Utils.max(p1, p2);

		return AxisAlignedBB.fromBounds(min.xCoord, min.yCoord, min.zCoord, max.xCoord, max.yCoord, max.zCoord);
	}

	@Override
	public void updateEntity ()
	{
		super.updateEntity();
		
		//empty the fluid buffer, if it exists
		if(fluidInItem != null)
		{
			EnumFacing fillOrientation = getOpenOrientation();
			if(fillOrientation != null)
			{
				fillOrientation = fillOrientation.getOpposite();
			}
			else
			{
				fillOrientation = EnumFacing.DOWN; // If we are connectged on multiple sides, it's not clear which side to fill from.  //May as well just pick one.
			}
			
			fluidInItem.amount -= transport.fill(fillOrientation, fluidInItem, true);
			
			if(fluidInItem.amount <= 0)
			{
				TravelingItem travelingItem = TravelingItem.make(new Vec3(container.getPos()), currentItem);
				travelingItem.setContainer(container);
				dropItem(travelingItem);
				
				fluidInItem = null;
				currentItem = null;
			}
		}
		else
		{
			//suck in a new item
			if (battery.getEnergyStored() > 0) {
				for (int j = 1; j < 5; ++j) {
					if (suckItem(j)) {
						return;
					}
				}

			}
			
			battery.useEnergy(0, 5, false);

		}
	}

	private boolean suckItem(int distance)
	{
		AxisAlignedBB box = getSuckingBox(getOpenOrientation(), distance);

		if (box == null) {
			return false;
		}

		List<EntityItem> discoveredEntities = container.getWorld().getEntitiesWithinAABB(EntityItem.class, box);

		for (EntityItem entity : discoveredEntities) 
		{
			if (canSuck(entity, distance))
			{
				pullItemIntoPipe(entity, distance);
				return true;
			}
		}

		return false;
	}

	public void pullItemIntoPipe(EntityItem entity, int distance) 
	{
		if (container.getWorld().isRemote) {
			return;
		}

		container.getWorld().playSoundAtEntity(entity, "random.pop", 0.2F, ((container.getWorld().rand.nextFloat() - container.getWorld().rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);

		ItemStack stack = null;

		double speed = 0.01F;

		ItemStack contained = entity.getEntityItem();

		if (contained == null) {
			return;
		}
		
		TransportProxy.proxy.obsidianPipePickup(container.getWorld(), entity, this.container);

		int energyUsed = Math.min(10 * contained.stackSize * distance, battery.getEnergyStored());

		if (distance == 0 || energyUsed / distance / 10 == contained.stackSize) {
			stack = contained;
			CoreProxy.proxy.removeEntity(entity);
		} else {
			stack = contained.splitStack(energyUsed / distance / 10);
		}

		speed = Math.sqrt(entity.motionX * entity.motionX + entity.motionY * entity.motionY + entity.motionZ * entity.motionZ);
		speed = speed / 2F - 0.05;

		if (speed < 0.01) {
			speed = 0.01;
		}

		if (stack == null) {
			return;
		}
		
		storeAndDrainItem(contained);
		
	}
	
	/**
	 * Extract fluid from the item and put it in the pipe's buffer.
	 * Then, store the empty fluid container item.
	 * @param stack
	 */
	private void storeAndDrainItem(ItemStack stack)
	{		
		FluidStack drainedLiquid;
		
		//annoyingly, buckets don't use the standard Forge fluid API, so we need to use the separate FluidContainerRegistry	
	
		if(FluidContainerRegistry.isFilledContainer(stack))
		{
			drainedLiquid = FluidContainerRegistry.getFluidForFilledItem(stack);
			
			currentItem = FluidContainerRegistry.drainFluidContainer(stack);
		}
		else
		{
			IFluidContainerItem fluidContainerItem = ((IFluidContainerItem)stack.getItem());
			
			drainedLiquid = fluidContainerItem.drain(stack, fluidContainerItem.getCapacity(stack), true);
			
			currentItem = stack;			
		}
		
		if(drainedLiquid == null || drainedLiquid.amount <= 0)
		{
			//drain() returned a different result than 
			//spit it back out
			TravelingItem travelingItem = TravelingItem.make(new Vec3(container.getPos()), currentItem);
			travelingItem.setContainer(container);
			
			dropItem(travelingItem);
		}
		else
		{
		
			Log.debug("Storing " + drainedLiquid.amount + "MB of fluid in buffer.");
	
			
			//add liquid to buffer
			if(fluidInItem != null && fluidInItem.isFluidEqual(drainedLiquid))
			{
				fluidInItem.amount += drainedLiquid.amount;
			}
			else
			{
				fluidInItem = drainedLiquid;
			}
		}
	}
	
	//copy of PipeTransportItems.dropItem()
	//this pipe uses a fluid transport, so I have to copy-paste it.
	private void dropItem(TravelingItem item) 
	{
        if (container.getWorld().isRemote) {
            return;
        }

        PipeEventItem.DropItem event = new PipeEventItem.DropItem(container.pipe, item, item.toEntityItem());
        container.pipe.eventBus.handleEvent(event);

        if (event.entity == null) {
            return;
        }

        final EntityItem entity = event.entity;
        
        EnumFacing direction = getOpenOrientation();
        if(direction != null)
        {
        	direction = direction.getOpposite();
        }
        else
        {
        	direction = EnumFacing.UP;
        }
        entity.setPosition(entity.posX + direction.getFrontOffsetX() * 0.5d, entity.posY + direction.getFrontOffsetY() * 0.5d, entity.posZ + direction
                .getFrontOffsetZ() * 0.5d);

        entity.motionX = direction.getFrontOffsetX() * item.getSpeed() * 5 + getWorld().rand.nextGaussian() * 0.1d;
        entity.motionY = direction.getFrontOffsetY() * item.getSpeed() * 5 + getWorld().rand.nextGaussian() * 0.1d;
        entity.motionZ = direction.getFrontOffsetZ() * item.getSpeed() * 5 + getWorld().rand.nextGaussian() * 0.1d;

        container.getWorld().spawnEntityInWorld(entity);
	}

	public void eventHandler(PipeEventItem.DropItem event)
	{
		if (entitiesDroppedIndex + 1 >= entitiesDropped.length)
		{
			entitiesDroppedIndex = 0;
		}
		else 
		{
			entitiesDroppedIndex++;
		}
		entitiesDropped[entitiesDroppedIndex] = event.entity.getEntityId();
	}

	public boolean canSuck(EntityItem item, int distance) 
	{
		if(currentItem != null)
		{
			return false;
		}
		
		
		//glitched item
		if (item.getEntityItem().stackSize <= 0) {
			return false;
		}
		
		//not enough energy
		if(battery.getEnergyStored() < distance * 10)
		{
			return false;
		}
		
		//-------------------------------------------------------------------------------
		//check that we can suck up fluid from the item
		
		Item fluidItem = item.getEntityItem().getItem();
		
		Fluid fluid = null;
		
		if(fluidItem instanceof IFluidContainerItem)
		{	
			FluidStack containedFluid = ((IFluidContainerItem)fluidItem).getFluid(item.getEntityItem());
			if(containedFluid != null)
			{
				fluid = containedFluid.getFluid();
			}
		}
		else if(FluidContainerRegistry.isFilledContainer(item.getEntityItem()))
		{
			fluid = FluidContainerRegistry.getFluidForFilledItem(item.getEntityItem()).getFluid();
		}
		
		if(fluid == null || (transport.fluidType != null && !fluid.equals(transport.fluidType.getFluid())))
		{
			return false;
		}
		
		//-------------------------------------------------------------------------------

		//check that the item was not one we already dropped
		for (int element : entitiesDropped) {
			if(item.getEntityId() == element) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from)
	{
		return true;
	}


	@Override
	public int getEnergyStored(EnumFacing from) {
		return battery.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return battery.getMaxEnergyStored();
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);

		if(fluidInItem != null)
		{
			//store buffer fluidstack
			NBTTagCompound fluidInItemTag = new NBTTagCompound();
			fluidInItem.writeToNBT(fluidInItemTag);
			nbt.setTag("fluidInItemTag", fluidInItemTag);
			
			//store the current item
			NBTTagCompound currentItemTag = new NBTTagCompound();
			currentItem.writeToNBT(currentItemTag);
			nbt.setTag("currentItemTag", currentItemTag);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		if(nbt.hasKey("fluidInItemTag"))
		{
			fluidInItem = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("fluidInItemTag"));
			currentItem = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("currentItemTag"));
		}
	}

}
