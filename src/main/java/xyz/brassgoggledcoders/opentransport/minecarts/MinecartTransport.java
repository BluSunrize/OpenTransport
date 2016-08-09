package xyz.brassgoggledcoders.opentransport.minecarts;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import xyz.brassgoggledcoders.boilerplate.BaseCreativeTab;
import xyz.brassgoggledcoders.opentransport.OpenTransport;
import xyz.brassgoggledcoders.opentransport.api.blockcontainers.IBlockContainer;
import xyz.brassgoggledcoders.opentransport.api.transporttypes.ITransportType;
import xyz.brassgoggledcoders.opentransport.api.transporttypes.TransportType;
import xyz.brassgoggledcoders.opentransport.minecarts.entities.EntityMinecartHolder;
import xyz.brassgoggledcoders.opentransport.minecarts.items.ItemMinecartHolder;

import javax.annotation.Nonnull;
import java.util.Map;

@TransportType
public class MinecartTransport implements ITransportType<EntityMinecart> {
	private CreativeTabs cartsTab = new MinecartsCreativeTab();
	private boolean isActive = true;

	@Nonnull
	@Override
	public String getName() {
		return "Minecarts";
	}

	@Nonnull
	@Override
	public Class<EntityMinecart> getBaseEntity() {
		return EntityMinecart.class;
	}

	@Nonnull
	@Override
	public CreativeTabs getCreativeTab() {
		return cartsTab;
	}

	@Override
	public void registerItems(Map<String, IBlockContainer> blockContainers) {
		blockContainers.forEach((name, blockContainer) -> {
			ItemMinecartHolder holder = new ItemMinecartHolder(blockContainer, this.getCreativeTab());
			OpenTransport.INSTANCE.getRegistryHolder().getItemRegistry().registerItem(holder);
		});
	}

	@Override
	public void registerEntities() {
		OpenTransport.INSTANCE.getRegistryHolder().getEntityRegistry().registerEntity(EntityMinecartHolder.class);
	}

	@Override
	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public boolean getIsActive() {
		return this.isActive;
	}

	private static class MinecartsCreativeTab extends BaseCreativeTab {
		public MinecartsCreativeTab() {
			super("minecarts");
		}

		@Override
		public Item getTabIconItem() {
			return Items.MINECART;
		}
	}
}