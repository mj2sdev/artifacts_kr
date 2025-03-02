package artifacts.client.mimic;

import artifacts.Artifacts;
import artifacts.client.mimic.model.MimicChestLayerModel;
import artifacts.client.mimic.model.MimicModel;
import artifacts.entity.MimicEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.architectury.platform.Platform;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MimicChestLayer extends RenderLayer<MimicEntity, MimicModel> {

    public static final ResourceLocation CHEST_ATLAS = new ResourceLocation("textures/atlas/chest.png");

    public static final List<String> QUARK_CHEST_TYPES = Arrays.asList(
            "oak",
            "spruce",
            "birch",
            "jungle",
            "acacia",
            "dark_oak",
            "warped",
            "crimson",
            "azalea",
            "blossom",
            "mangrove",
            "bamboo"
    );

    private final MimicChestLayerModel chestModel;
    public final Material vanillaChestMaterial;
    public final List<Material> chestMaterials;

    @SuppressWarnings("deprecation")
    public MimicChestLayer(RenderLayerParent<MimicEntity, MimicModel> entityRenderer, EntityModelSet modelSet) {
        super(entityRenderer);

        Calendar calendar = Calendar.getInstance();
        boolean isChristmas = calendar.get(Calendar.MONTH) == Calendar.DECEMBER && calendar.get(Calendar.DATE) >= 24 && calendar.get(Calendar.DATE) <= 26
                || calendar.get(Calendar.MONTH) == Calendar.APRIL && calendar.get(Calendar.DATE) == 1;

        chestModel = new MimicChestLayerModel(modelSet.bakeLayer(MimicChestLayerModel.LAYER_LOCATION));
        chestMaterials = new ArrayList<>();
        vanillaChestMaterial = isChristmas ? Sheets.CHEST_XMAS_LOCATION : Sheets.CHEST_LOCATION;

        if (isChristmas) {
            chestMaterials.add(vanillaChestMaterial);
            return;
        }

        if (Platform.isModLoaded("lootr")) {
            ResourceLocation chestLocation = new ResourceLocation("lootr", "chest");
            chestMaterials.add(new Material(TextureAtlas.LOCATION_BLOCKS, chestLocation));
        } else if (Platform.isModLoaded("myloot")) {
            ResourceLocation chestLocation = new ResourceLocation("myloot", "entity/chest/loot");
            chestMaterials.add(new Material(CHEST_ATLAS, chestLocation));
        } else {
            chestMaterials.add(vanillaChestMaterial);
            if (Platform.isModLoaded("quark")) {
                for (String chestType : QUARK_CHEST_TYPES) {
                    ResourceLocation chestLocation = new ResourceLocation("quark", String.format("model/chest/%s/normal", chestType));
                    chestMaterials.add(new Material(CHEST_ATLAS, chestLocation));
                }
            }
        }
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource buffer, int packedLight, MimicEntity mimic, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!mimic.isInvisible()) {
            matrixStack.pushPose();

            matrixStack.mulPose(Axis.XP.rotationDegrees(180));
            matrixStack.translate(-0.5, -1.5, -0.5);

            getParentModel().copyPropertiesTo(chestModel);
            chestModel.prepareMobModel(mimic, limbSwing, limbSwingAmount, partialTicks);
            chestModel.setupAnim(mimic, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            VertexConsumer builder = getChestMaterial(mimic).buffer(buffer, RenderType::entityCutout);
            chestModel.renderToBuffer(matrixStack, builder, packedLight, LivingEntityRenderer.getOverlayCoords(mimic, 0), 1, 1, 1, 1);

            matrixStack.popPose();
        }
    }

    private Material getChestMaterial(MimicEntity mimic) {
        if (!Artifacts.CONFIG.client.useModdedMimicTextures) {
            return vanillaChestMaterial;
        }
        if (chestMaterials.size() == 1) {
            return chestMaterials.get(0);
        }
        return chestMaterials.get((int) (Math.abs(mimic.getUUID().getMostSignificantBits()) % chestMaterials.size()));
    }
}
