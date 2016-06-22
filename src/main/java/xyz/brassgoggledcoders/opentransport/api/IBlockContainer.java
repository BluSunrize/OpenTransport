package xyz.brassgoggledcoders.opentransport.api;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import xyz.brassgoggledcoders.opentransport.renderers.RenderType;

public interface IBlockContainer
{
	Block getBlock();

	IBlockState getBlockState();

	String getUnlocalizedName();

	RenderType getRenderType();

	IInteraction getClickInteraction();

	boolean onInteract(EntityPlayer entityPlayer, IHolderEntity entity);

	void tick();

	void markDirty();

	void setWorld(World world);

	boolean hasTileEntity();

	TileEntity getTileEntity();

	NBTTagCompound writeToNBT(NBTTagCompound tagCompound);

	void readFromNBT(NBTTagCompound tagCompound);

	IBlockContainer copy();
}