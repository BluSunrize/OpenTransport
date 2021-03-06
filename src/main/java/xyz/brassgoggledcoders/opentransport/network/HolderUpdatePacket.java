package xyz.brassgoggledcoders.opentransport.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import xyz.brassgoggledcoders.opentransport.OpenTransport;
import xyz.brassgoggledcoders.opentransport.api.entities.IHolderEntity;

public class HolderUpdatePacket implements IMessage {
    public int entityID;
    public IHolderEntity holderEntity;
    public NBTTagCompound nbtTagCompound;

    public HolderUpdatePacket() {

    }

    public HolderUpdatePacket(IHolderEntity holderEntity) {
        this.entityID = holderEntity.getEntity().getEntityId();
        this.holderEntity = holderEntity;
        this.nbtTagCompound = holderEntity.getBlockWrapper().writeToNBT(new NBTTagCompound());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityID = buf.readInt();
        this.nbtTagCompound = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entityID);
        ByteBufUtils.writeTag(buf, this.nbtTagCompound);
    }

    public IHolderEntity getHolderEntityFromMessage(MessageContext messageContext) {
        World world = OpenTransport.proxy.getWorld(messageContext);
        if (world != null) {
            Entity entity = world.getEntityByID(this.entityID);
            if (entity instanceof IHolderEntity) {
                return (IHolderEntity) entity;
            }
        } else {
            OpenTransport.instance.getLogger().devInfo("The world was null");
        }

        return null;
    }

    public static class Handler implements IMessageHandler<HolderUpdatePacket, IMessage> {
        @Override
        public IMessage onMessage(final HolderUpdatePacket message, final MessageContext ctx) {
            IThreadListener mainThread = OpenTransport.proxy.getIThreadListener(ctx);
            mainThread.addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    IHolderEntity holderEntity = message.getHolderEntityFromMessage(ctx);

                    if (holderEntity != null) {
                        holderEntity.getBlockWrapper().readFromNBT(message.nbtTagCompound);
                    }
                }
            });
            return null;
        }
    }
}
