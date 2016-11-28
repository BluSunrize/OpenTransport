package xyz.brassgoggledcoders.opentransport.minecarts.renderers;

import blusunrize.immersiveengineering.common.util.chickenbones.Matrix4;
import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelMinecart;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import org.apache.commons.lang3.tuple.Pair;
import xyz.brassgoggledcoders.opentransport.api.blockwrappers.IBlockWrapper;
import xyz.brassgoggledcoders.opentransport.minecarts.entities.EntityMinecartHolder;
import xyz.brassgoggledcoders.opentransport.renderers.RenderBlock;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.*;

public class RenderItemHolderMinecart implements IBakedModel, IPerspectiveAwareModel {

    //Basically, I made this an IBakedModel, since that is how you do ItemModels now.
    //Ideally, you would have a "base" version of this, that handles all the block wrapper part and caching and shit
    //And you'd make "Minecart" and "Boat" two subclasses that simply override a method like "getContainerQuads", which returns a list of quads that look like a cart/boat

    Set<BakedQuad> bakedQuads;//This caches the baked quads, it's initialized during the first bake. If you had meta/nbt differences, you'd make this a map or smth and use the OverrideLists to handle it all
    static List<BakedQuad> emptyQuads = Lists.newArrayList();//Empty quads list so you don't constantly initialize new lists

    IBlockWrapper blockWrapper;//The blockwrapper contained

    public RenderItemHolderMinecart(IBlockWrapper blockWrapper) {
        this.blockWrapper = blockWrapper;
    }

    public RenderItemHolderMinecart() {
        this(null);
//        renderBlock = new RenderBlock();
//        modelMinecart = new ModelMinecart();
    }



    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType)
    {
        Matrix4 matrix = new Matrix4();//currently usign IE/CodeChicken matrices here, since they have sensible translation methods.
        //I would have switched it to vanilal Matrices later, once I had the values figured out
matrix.translate(4,0,0);
        return Pair.of(this, matrix.toMatrix4f());
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand)
    {
        if(bakedQuads == null)//if they have already been baked, you can save the trouble. I frequently comment this out when testing stuff, so that it rebakes and updates
        {
            try{
                bakedQuads = Collections.synchronizedSet(new LinkedHashSet<BakedQuad>());

                Minecraft mc = Minecraft.getMinecraft();
                //This is where the "getContainerQuads()" would sit, that adds either a cart or a boat to the list of quads.
                //It will of course need a texture stitched into the sheet, but you can probably take hte vanilla cart texture and cut it down+optimize it to fit in a 32x32
                //You wouldn't see a lot of the insides so you can get lazy with it.
                TextureAtlasSprite cartTexture = mc.getTextureMapBlocks().getAtlasSprite("minecraft:blocks/diamond_ore");



                //Get quads for the contained Block
                IBakedModel blockModel = mc.getBlockRendererDispatcher().getBlockModelShapes().getModelForState(blockWrapper.getBlockState());
                HashSet<BakedQuad> set = new HashSet();
                //From experience and also what AtomicBlom had to do for chiselled sheep. You have to check quads for all facings and null.
                //I used a set in the hopes of quads hashing well and possibly avoiding duplicates with this.
                for(EnumFacing f : EnumFacing.values())
                    set.addAll(blockModel.getQuads(blockWrapper.getBlockState(), f, 0));
                //I added this if check as an experiment. Might work, might not. Might have to always query null as well, not sure.
                if(set.isEmpty())
                    set.addAll(blockModel.getQuads(blockWrapper.getBlockState(), null, 0));

                bakedQuads.addAll(set);

            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        if(bakedQuads!=null && !bakedQuads.isEmpty())
        {
            //Never return the original list, always make a synchronized copy.
            //Or at least that is what everyone else does, so I stuck with what I assume is convention.
            List<BakedQuad> quadList = Collections.synchronizedList(Lists.newArrayList(bakedQuads));
            return quadList;
        }
        return emptyQuads;
    }

    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }
    @Override
    public boolean isGui3d() {
        return true;
    }
    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }
    @Override
    public TextureAtlasSprite getParticleTexture() {
        return null;
    }
    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    //This would be used if you have to differ items by meta or nbt data, since the normal GetQuads doesn't feature it.
    //IE's ModelCoresample is a good example for how that can be handled.
    public ItemOverrideList getOverrides() {
        return null;
    }


/*TODO: RENDERING CODE
    @Override
	public void render(World world, Item item, ItemStack itemStack, TransformType type) {
		if(minecartHolder == null) {
			minecartHolder = new EntityMinecartHolder(Minecraft.getMinecraft().theWorld);
		}
		if(minecartHolder.getEntity().worldObj != null && item instanceof ItemMinecartHolder) {
			ItemMinecartHolder itemMinecartHolder = (ItemMinecartHolder) item;
			IBlockWrapper blockWrapper = itemMinecartHolder.getBlockWrapper(itemStack);
			blockWrapper.setHolder(minecartHolder);
			minecartHolder.setBlockWrapper(blockWrapper);

			GlStateManager.pushMatrix();
			GlStateManager.translate(.5, .55, .5);
			switch(type) {
				case GROUND:
					GlStateManager.scale(.15, .15, .15);
					break;
				case GUI:
					GlStateManager.scale(.25, .25, .25);
					GlStateManager.rotate(45, 1, -1, 0);
					break;
				case FIRST_PERSON_LEFT_HAND:
					GlStateManager.scale(.2, .2, .2);
					GlStateManager.rotate(45, 1, 0, 0);
					break;
				case FIRST_PERSON_RIGHT_HAND:
					GlStateManager.scale(.2, .2, .2);
					GlStateManager.rotate(45, 1, 0, 0);
					break;
				case THIRD_PERSON_LEFT_HAND:
					GlStateManager.scale(.15, .15, .15);
					GlStateManager.rotate(45, 1, 0, 0);
					break;
				case THIRD_PERSON_RIGHT_HAND:
					GlStateManager.scale(.15, .15, .15);
					GlStateManager.rotate(45, 1, 0, 0);
					break;
				default:
					break;
			}
			renderMinecart();
			GlStateManager.rotate(90, 0, 1, 0);
			GlStateManager.scale(1.5, 1.5, 1.5);
			GlStateManager.translate(-.5, -0.25, .5);
			renderBlockWrapper(blockWrapper);
			GlStateManager.popMatrix();
		}
	}

	protected void renderMinecart() {
		GlStateManager.pushMatrix();
		GlStateManager.rotate(180, 0, 0, 1);
		Minecraft.getMinecraft().renderEngine.bindTexture(minecartTexture);
		modelMinecart.render(ClientHelper.player(), 0, 0, 0, 0, 0, 0.1F);
		GlStateManager.popMatrix();
	}

	protected void renderBlockWrapper(IBlockWrapper blockWrapper) {
		renderBlock.renderEntity(ClientHelper.player(), blockWrapper, 0);
	}*/
}
