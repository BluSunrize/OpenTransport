package xyz.brassgoggledcoders.opentransport.api.blockwrappers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import xyz.brassgoggledcoders.opentransport.api.entities.IHolderEntity;

public interface IActionListener {
    boolean actionOccurred(ActionType actionType, EntityPlayer entityPlayer, EnumHand hand, ItemStack itemStack,
                           IHolderEntity holderEntity, IBlockWrapper blockWrapper);
}
