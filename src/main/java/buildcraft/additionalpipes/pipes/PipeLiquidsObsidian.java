package buildcraft.additionalpipes.pipes;

import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import buildcraft.BuildCraftTransport;
import buildcraft.api.core.IIconProvider;
import buildcraft.api.core.Position;
import buildcraft.core.lib.RFBattery;
import buildcraft.core.proxy.CoreProxy;
import buildcraft.transport.PipeTransportFluids;
import buildcraft.transport.TransportProxy;
import buildcraft.transport.TravelingItem;
import buildcraft.transport.pipes.events.PipeEventItem;
import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PipeLiquidsObsidian extends APPipe<PipeTransportFluids> implements IEnergyHandler 
{
	private static final int ICON = 24;
	
	AxisAlignedBB searchBox;
	private RFBattery battery = new RFBattery(2560, 640, 0);

	//rolling queue of entity IDs to avoid picking up
	private int[] entitiesDropped;
	private int entitiesDroppedIndex = 0;

	public PipeLiquidsObsidian(Item item)
	{
		super(new PipeTransportFluids(), item);
		
		//load the fluid capacities set in mod init
		transport.initFromPipe(getClass());
		
		entitiesDropped = new int[32];
		Arrays.fill(entitiesDropped, -1);
	}

	@Override
	public int getIconIndex(ForgeDirection direction)
	{
		return ICON;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIconProvider getIconProvider() {
		return BuildCraftTransport.instance.pipeIconProvider;
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

	private AxisAlignedBB getSuckingBox(ForgeDirection orientation, int distance) {
		if (orientation == ForgeDirection.UNKNOWN) {
			return null;
		}
		Position p1 = new Position(container.xCoord, container.yCoord, container.zCoord, orientation);
		Position p2 = new Position(container.xCoord, container.yCoord, container.zCoord, orientation);

		switch (orientation) {
			case EAST:
				p1.x += distance;
				p2.x += 1 + distance;
				break;
			case WEST:
			p1.x -= distance - 1;
				p2.x -= distance;
				break;
			case UP:
			case DOWN:
				p1.x += distance + 1;
				p2.x -= distance;
				p1.z += distance + 1;
				p2.z -= distance;
				break;
			case SOUTH:
				p1.z += distance;
				p2.z += distance + 1;
				break;
			case NORTH:
			default:
			p1.z -= distance - 1;
				p2.z -= distance;
				break;
		}

		switch (orientation) {
			case EAST:
			case WEST:
				p1.y += distance + 1;
				p2.y -= distance;
				p1.z += distance + 1;
				p2.z -= distance;
				break;
			case UP:
				p1.y += distance + 1;
				p2.y += distance;
				break;
			case DOWN:
			p1.y -= distance - 1;
				p2.y -= distance;
				break;
			case SOUTH:
			case NORTH:
			default:
				p1.y += distance + 1;
				p2.y -= distance;
				p1.x += distance + 1;
				p2.x -= distance;
				break;
		}

		Position min = p1.min(p2);
		Position max = p1.max(p2);

		return AxisAlignedBB.getBoundingBox(min.x, min.y, min.z, max.x, max.y, max.z);
	}

	@Override
	public void updateEntity ()
	{
		super.updateEntity();

		if (battery.getEnergyStored() > 0) {
			for (int j = 1; j < 5; ++j) {
				if (suckItem(j)) {
					return;
				}
			}

			battery.useEnergy(0, 5, false);
		}
	}

	@SuppressWarnings("unchecked")
	private boolean suckItem(int distance)
	{
		AxisAlignedBB box = getSuckingBox(getOpenOrientation(), distance);

		if (box == null) {
			return false;
		}

		List<EntityItem> discoveredEntities = container.getWorldObj().getEntitiesWithinAABB(EntityItem.class, box);

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

	public void pullItemIntoPipe(EntityItem entity, int distance) {
		if (container.getWorldObj().isRemote) {
			return;
		}

		ForgeDirection orientation = getOpenOrientation().getOpposite();

		if (orientation != ForgeDirection.UNKNOWN) {
			container.getWorldObj().playSoundAtEntity(entity, "random.pop", 0.2F, ((container.getWorldObj().rand.nextFloat() - container.getWorldObj().rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);

			ItemStack stack = null;

			double speed = 0.01F;

			ItemStack contained = entity.getEntityItem();

			if (contained == null) {
				return;
			}
			
			TransportProxy.proxy.obsidianPipePickup(container.getWorldObj(), entity, this.container);

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
			
			processAndExpelItem(contained);
		}
	}
	
	/**
	 * Extract fluid from the item and put it in the pipe.
	 * Then, drop the empty fluid container item.
	 * @param stack
	 */
	private void processAndExpelItem(ItemStack stack)
	{
		//annoyingly, buckets don't use the standard Forge fluid API, so we need to use the separate FluidContainerRegistry
		
		ItemStack stackToDrop = null;
		
		if(FluidContainerRegistry.isFilledContainer(stack))
		{
			FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(stack);
		    //no take-backs.  If any liquid was drained, the bucket is emptied.
		    if(transport.fill(ForgeDirection.UNKNOWN, liquid, true) != 0)
		    {
		    	stackToDrop = FluidContainerRegistry.drainFluidContainer(stack);
		    }
		    else
		    {
		    	stackToDrop = stack;
		    }
		}
		else
		{
			IFluidContainerItem fluidContainerItem = ((IFluidContainerItem)stack.getItem());
			
			//first, figure out how much liquid we can add
			int amountFillable = transport.fill(ForgeDirection.UNKNOWN, fluidContainerItem.drain(stack, fluidContainerItem.getCapacity(stack), false), false);
			
			//now, do the actual fill
			transport.fill(ForgeDirection.UNKNOWN, fluidContainerItem.drain(stack, amountFillable, true), true);
			
			stackToDrop = stack;
			
		}
		
		TravelingItem travelingItem = TravelingItem.make(container.x(), container.y(), container.z(), stackToDrop);
		travelingItem.setContainer(container);
		dropItem(travelingItem);
	}
	
	//copy of PipeTransportItems.dropItem()
	//this pipe uses a fluid transport, so I have to copy-paste it.
	private void dropItem(TravelingItem item) 
	{
		if (container.getWorldObj().isRemote) {
			return;
		}
		
		PipeEventItem.DropItem event = new PipeEventItem.DropItem(container.pipe, item, item.toEntityItem());
		container.pipe.eventBus.handleEvent(PipeEventItem.DropItem.class, event);
		
		if(event.entity == null)
		{
			return;
		}
		
		final EntityItem entity = event.entity;

		ForgeDirection direction = item.input;
		entity.setPosition(entity.posX + direction.offsetX * 0.5d,
				entity.posY + direction.offsetY * 0.5d,
				entity.posZ + direction.offsetZ * 0.5d);

		entity.motionX = direction.offsetX * item.getSpeed() * 5
				+ getWorld().rand.nextGaussian() * 0.1d;
		entity.motionY = direction.offsetY * item.getSpeed() * 5
				+ getWorld().rand.nextGaussian() * 0.1d;
		entity.motionZ = direction.offsetZ * item.getSpeed() * 5
				+ getWorld().rand.nextGaussian() * 0.1d;

		container.getWorldObj().spawnEntityInWorld(entity);
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
		if (item.getEntityItem().stackSize <= 0) {
			return false;
		}
		
		if(!(item.getEntityItem().getItem() instanceof IFluidContainerItem || FluidContainerRegistry.isFilledContainer(item.getEntityItem())))
		{
			return false;
		}

		for (int element : entitiesDropped) {
			if(item.getEntityId() == element) {
				return false;
			}
		}

		return battery.getEnergyStored() >= distance * 10;
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from)
	{
		return true;
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive,
			boolean simulate) {
		return battery.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract,
			boolean simulate) {
		return 0;
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return battery.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return battery.getMaxEnergyStored();
	}

}
