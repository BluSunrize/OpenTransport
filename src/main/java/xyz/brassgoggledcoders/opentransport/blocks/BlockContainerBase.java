package xyz.brassgoggledcoders.opentransport.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import xyz.brassgoggledcoders.opentransport.OpenTransport;
import xyz.brassgoggledcoders.opentransport.api.blockcontainers.IBlockContainer;
import xyz.brassgoggledcoders.opentransport.api.blockcontainers.IGuiInterface;
import xyz.brassgoggledcoders.opentransport.api.blockcontainers.IInteraction;
import xyz.brassgoggledcoders.opentransport.api.entities.IHolderEntity;
import xyz.brassgoggledcoders.opentransport.interactions.BaseInteraction;
import xyz.brassgoggledcoders.opentransport.interfaces.BaseInterface;
import xyz.brassgoggledcoders.opentransport.network.HolderUpdatePacket;
import xyz.brassgoggledcoders.opentransport.renderers.RenderType;
import xyz.brassgoggledcoders.opentransport.wrappers.world.WorldWrapper;

import javax.annotation.Nonnull;

public class BlockContainerBase implements IBlockContainer {
	Block block;
	IBlockState blockState;
	TileEntity tileEntity;
	World world;
	boolean hasTileEntity;
	String unlocalizedName;
	IInteraction clickInteraction = new BaseInteraction();
	IGuiInterface guiInterface = new BaseInterface();
	RenderType renderType = RenderType.VMC;
	IHolderEntity holderEntity;
	boolean isDirty;

	public BlockContainerBase(Block block) {
		this.block = block;
		this.blockState = block.getDefaultState();
		this.unlocalizedName = block.getUnlocalizedName().replaceFirst("tile.", "");
	}

	public BlockContainerBase setBlock(Block block) {
		this.block = block;
		this.blockState = block.getDefaultState();
		return this;
	}

	public BlockContainerBase setBlockState(IBlockState blockState) {
		this.block = blockState.getBlock();
		this.blockState = blockState;
		return this;
	}

	public BlockContainerBase setUnlocalizedName(String name) {
		this.unlocalizedName = name.replaceFirst("tile.", "");
		return this;
	}

	public BlockContainerBase setClickInteraction(IInteraction interaction) {
		this.clickInteraction = interaction;
		return this;
	}

	public BlockContainerBase setGuiInterface(IGuiInterface guiInterface) {
		this.guiInterface = guiInterface;
		return this;
	}

	public BlockContainerBase setRenderType(RenderType renderType) {
		this.renderType = renderType;
		return this;
	}

	@Override
	@Nonnull
	public Block getBlock() {
		return block;
	}

	@Override
	@Nonnull
	public IBlockState getBlockState() {
		return blockState;
	}

	@Override
	@Nonnull
	public String getUnlocalizedName() {
		return unlocalizedName;
	}

	@Override
	@Nonnull
	public RenderType getRenderType() {
		return renderType;
	}

	@Override
	@Nonnull
	public IInteraction getClickInteraction() {
		return clickInteraction;
	}

	@Override
	@Nonnull
	public IGuiInterface getInterface() {
		return guiInterface;
	}

	@Override
	public boolean onInteract(EntityPlayer entityPlayer, EnumHand hand, ItemStack itemStack) {
		boolean result;
		this.updateBlockContainer();
		EntityPlayer entityPlayerWrapper = OpenTransport.PROXY.getEntityPlayerWrapper(entityPlayer, this.holderEntity);
		result = this.getClickInteraction().interact(entityPlayerWrapper, hand, itemStack, this.holderEntity, this);
		OpenTransport.PROXY.resetPlayer(entityPlayer);
		this.updateBlockContainer();
		return result;
	}

	@Override
	public void tick() {
		if(this.getTileEntity() instanceof ITickable) {
			((ITickable) this.getTileEntity()).update();
		}
	}

	@Override
	public void markDirty() {
		isDirty = true;
	}

	@Override
	public void setHolder(IHolderEntity holderEntity) {
		this.holderEntity = holderEntity;
		this.world = new WorldWrapper(holderEntity);
	}

	@Override
	public boolean hasTileEntity() {
		return hasTileEntity;
	}

	@Override
	public TileEntity getTileEntity() {
		if(this.tileEntity == null) {
			this.tileEntity = this.getBlock().createTileEntity(this.world, this.getBlockState());
			this.tileEntity.setWorldObj(this.world);
		}
		return this.tileEntity;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
		if(world != null && this.getTileEntity() != null) {
			tagCompound.setTag("TILE_DATA", this.getTileEntity().writeToNBT(new NBTTagCompound()));
		}
		return tagCompound;
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		if(world != null && this.getTileEntity() != null) {
			if(tagCompound.hasKey("TILE_DATA")) {
				this.getTileEntity().readFromNBT(tagCompound.getCompoundTag("TILE_DATA"));
			}
		}
	}

	@Override
	public IBlockContainer copy() {
		BlockContainerBase copyBlockContainer = new BlockContainerBase(this.getBlock());
		copyBlockContainer.setBlockState(this.getBlockState()).setClickInteraction(this.getClickInteraction())
				.setGuiInterface(this.guiInterface).setRenderType(this.getRenderType())
				.setUnlocalizedName(this.getUnlocalizedName());
		return copyBlockContainer;
	}

	private void updateBlockContainer() {
		OpenTransport.INSTANCE.getPacketHandler()
				.sendToAllAround(new HolderUpdatePacket(this.holderEntity), this.holderEntity.getEntity());
	}
}
