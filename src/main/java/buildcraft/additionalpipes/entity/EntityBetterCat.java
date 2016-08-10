package buildcraft.additionalpipes.entity;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.world.World;

public class EntityBetterCat extends EntityOcelot
{

	public EntityBetterCat(World world)
	{
		super(world);
        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<EntityCreeper>(this, EntityCreeper.class, true));
        System.out.println(this.getEntityAttribute(SharedMonsterAttributes.followRange).getAttributeValue());
	}
	
	@Override
	public void onLivingUpdate()
	{
		super.onLivingUpdate();
		if(this.getHealth() < this.getMaxHealth() && this.ticksExisted % 40 * 12 == 0)
        {
            this.heal(1.0F);
        }
	}

}
