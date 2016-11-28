package xyz.brassgoggledcoders.opentransport.proxies;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import xyz.brassgoggledcoders.opentransport.OpenTransport;
import xyz.brassgoggledcoders.opentransport.api.blockwrappers.IBlockWrapper;
import xyz.brassgoggledcoders.opentransport.api.entities.IHolderEntity;
import xyz.brassgoggledcoders.opentransport.api.transporttypes.ITransportType;
import xyz.brassgoggledcoders.opentransport.boats.entities.EntityBoatHolder;
import xyz.brassgoggledcoders.opentransport.boats.renderers.RenderHolderBoat;
import xyz.brassgoggledcoders.opentransport.minecarts.entities.EntityMinecartHolder;
import xyz.brassgoggledcoders.opentransport.minecarts.renderers.RenderHolderMinecart;
import xyz.brassgoggledcoders.opentransport.minecarts.renderers.RenderItemHolderMinecart;
import xyz.brassgoggledcoders.opentransport.registries.BlockWrapperRegistry;
import xyz.brassgoggledcoders.opentransport.wrappers.player.EntityPlayerSPWrapper;

import java.util.Map.Entry;

public class ClientProxy extends CommonProxy {
    @Override
    public EntityPlayer getEntityPlayerWrapper(EntityPlayer entityPlayer, IHolderEntity containerHolder) {
        if (entityPlayer instanceof EntityPlayerSP) {
            return new EntityPlayerSPWrapper((EntityPlayerSP) entityPlayer, containerHolder);
        }
        return super.getEntityPlayerWrapper(entityPlayer, containerHolder);
    }

    @Override
    public World getWorld(MessageContext ctx) {
        return Minecraft.getMinecraft().theWorld;
    }

    @Override
    public IThreadListener getIThreadListener(MessageContext messageContext) {
        return Minecraft.getMinecraft();
    }

    @Override
    public void registerEntityRenders() {
        RenderingRegistry.registerEntityRenderingHandler(EntityBoatHolder.class, RenderHolderBoat::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityMinecartHolder.class, RenderHolderMinecart::new);
       //Register for events
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onModelBakeEvent(ModelBakeEvent event) {

        //Iterate through them all
        for(Entry<String, IBlockWrapper> entry : BlockWrapperRegistry.getAllBlockWrappers().entrySet()) {
            IBlockWrapper blockWrapper = entry.getValue();
            //create full registry names
            String minecart = "minecart.holder." + blockWrapper.getUnlocalizedName();
            String boat = "boat.holder." + blockWrapper.getUnlocalizedName();

            //Create model location
            //This is funky for you. You seem to have routed them all to "opentransport:minecart" somehow, rather than having separate jsons
            //I couldn't figure out where you do that routign, but you will have to undo it probably to make this part work
            ModelResourceLocation mLoc = new ModelResourceLocation(new ResourceLocation("opentransport", minecart), "inventory");
            //Replace normal model with the new one, parsing in the blockwrapper
            event.getModelRegistry().putObject(mLoc, new RenderItemHolderMinecart(blockWrapper));

        }
    }
}
