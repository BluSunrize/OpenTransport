package xyz.brassgoggledcoders.opentransport.modules.vanilla.guiinterfaces;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiCommandBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntityCommandBlock;
import xyz.brassgoggledcoders.opentransport.api.blockwrappers.IBlockWrapper;
import xyz.brassgoggledcoders.opentransport.api.blockwrappers.IGuiInterface;
import xyz.brassgoggledcoders.opentransport.api.entities.IHolderEntity;

public class CommandGuiInterface implements IGuiInterface {
    @Override
    public Gui getGUI(EntityPlayer entityPlayer, IHolderEntity holderEntity, IBlockWrapper blockWrapper) {
        return new GuiCommandBlock((TileEntityCommandBlock) holderEntity.getBlockWrapper().getTileEntity());
    }

    @Override
    public Container getContainer(EntityPlayer entityPlayer, IHolderEntity holderEntity, IBlockWrapper blockWrapper) {
        return null;
    }
}
