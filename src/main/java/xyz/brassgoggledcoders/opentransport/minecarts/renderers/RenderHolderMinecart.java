package xyz.brassgoggledcoders.opentransport.minecarts.renderers;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderMinecart;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import xyz.brassgoggledcoders.opentransport.minecarts.entities.EntityMinecartHolder;
import xyz.brassgoggledcoders.opentransport.renderers.RenderBlock;

import javax.annotation.Nonnull;

public class RenderHolderMinecart extends RenderMinecart<EntityMinecartHolder> {
    private RenderBlock renderBlock;

    public RenderHolderMinecart(RenderManager renderManager) {
        super(renderManager);
        renderBlock = new RenderBlock();
    }

    @Override
    public void doRender(@Nonnull EntityMinecartHolder entity, double x, double y, double z, float entityYaw,
                         float partialTicks) {
        GlStateManager.pushMatrix();
        this.bindEntityTexture(entity);
        long i = (long) entity.getEntityId() * 493286711L;
        i = i * i * 4392167121L + i * 98761L;
        float f = (((float) (i >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float f1 = (((float) (i >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float f2 = (((float) (i >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        GlStateManager.translate(f, f1, f2);
        double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks;
        double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks;
        double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks;
        double d3 = 0.30000001192092896D;
        Vec3d vec3d = entity.getPos(d0, d1, d2);
        float f3 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;

        if (vec3d != null) {
            Vec3d vec3d1 = entity.getPosOffset(d0, d1, d2, d3);
            Vec3d vec3d2 = entity.getPosOffset(d0, d1, d2, -d3);

            if (vec3d1 == null) {
                vec3d1 = vec3d;
            }

            if (vec3d2 == null) {
                vec3d2 = vec3d;
            }

            x += vec3d.xCoord - d0;
            y += (vec3d1.yCoord + vec3d2.yCoord) / 2.0D - d1;
            z += vec3d.zCoord - d2;
            Vec3d vec3d3 = vec3d2.addVector(-vec3d1.xCoord, -vec3d1.yCoord, -vec3d1.zCoord);

            if (vec3d3.lengthVector() != 0.0D) {
                vec3d3 = vec3d3.normalize();
                entityYaw = (float) (Math.atan2(vec3d3.zCoord, vec3d3.xCoord) * 180.0D / Math.PI);
                f3 = (float) (Math.atan(vec3d3.yCoord) * 73.0D);
            }
        }

        GlStateManager.translate((float) x, (float) y + 0.375F, (float) z);
        GlStateManager.rotate(180.0F - entityYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-f3, 0.0F, 0.0F, 1.0F);
        float f5 = (float) entity.getRollingAmplitude() - partialTicks;
        float f6 = entity.getDamage() - partialTicks;

        if (f6 < 0.0F) {
            f6 = 0.0F;
        }

        if (f5 > 0.0F) {
            GlStateManager
                    .rotate(MathHelper.sin(f5) * f5 * f6 / 10.0F * (float) entity.getRollingDirection(), 1.0F, 0.0F,
                            0.0F);
        }

        int j = entity.getDisplayTileOffset();

        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        GlStateManager.pushMatrix();
        float f4 = 0.75F;
        GlStateManager.scale(f4, f4, f4);
        GlStateManager.translate(-0.5F, (float) (j - 8) / 16.0F, 0.5F);
        renderBlock.renderEntity(entity, entity.getBlockContainer(), partialTicks);
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.bindEntityTexture(entity);

        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        this.modelMinecart.render(entity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        GlStateManager.popMatrix();

        if (this.renderOutlines) {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        if (!this.renderOutlines) {
            this.renderName(entity, x, y, z);
        }
    }

}
