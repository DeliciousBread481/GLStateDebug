package com.deliciousbread481.glstatedebug.mixin;

import com.deliciousbread481.glstatedebug.GlStateDebugMod;
import com.deliciousbread481.glstatedebug.GlStateSnapshot;
import com.deliciousbread481.glstatedebug.ThrottledLogger;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderItem.class)
public abstract class RenderItemMixin {

    @Unique private final ThreadLocal<GlStateSnapshot> gsd$snapshot = new ThreadLocal<>();

    // renderItemAndEffectIntoGUI(EntityLivingBase, World, ItemStack, int, int)
    @Inject(method = "renderItemAndEffectIntoGUI(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;II)V",
            at = @At("HEAD"))
    private void gsd$headLiving(EntityLivingBase e, World w, ItemStack s, int x, int y, CallbackInfo ci) {
        if (GlStateDebugMod.probeRenderItem) gsd$snapshot.set(GlStateSnapshot.capture());
    }

    @Inject(method = "renderItemAndEffectIntoGUI(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;II)V",
            at = @At("RETURN"))
    private void gsd$returnLiving(EntityLivingBase e, World w, ItemStack stack, int x, int y, CallbackInfo ci) {
        gsd$check(stack, x, y);
    }

    // renderItemAndEffectIntoGUI(ItemStack, int, int)
    @Inject(method = "renderItemAndEffectIntoGUI(Lnet/minecraft/item/ItemStack;II)V",
            at = @At("HEAD"))
    private void gsd$headStack(ItemStack stack, int x, int y, CallbackInfo ci) {
        if (GlStateDebugMod.probeRenderItem) gsd$snapshot.set(GlStateSnapshot.capture());
    }

    @Inject(method = "renderItemAndEffectIntoGUI(Lnet/minecraft/item/ItemStack;II)V",
            at = @At("RETURN"))
    private void gsd$returnStack(ItemStack stack, int x, int y, CallbackInfo ci) {
        gsd$check(stack, x, y);
    }

    // renderItemModelIntoGUI 也做快照（覆盖内部 model 渲染路径，如 renderLitItem）
    @Inject(method = "renderItemModelIntoGUI", at = @At("HEAD"))
    private void gsd$headModel(ItemStack stack, int x, int y,
                               ItemCameraTransforms.TransformType type, CallbackInfo ci) {
        if (GlStateDebugMod.probeRenderItem) gsd$snapshot.set(GlStateSnapshot.capture());
    }

    @Inject(method = "renderItemModelIntoGUI", at = @At("RETURN"))
    private void gsd$returnModel(ItemStack stack, int x, int y,
                                 ItemCameraTransforms.TransformType type, CallbackInfo ci) {
        gsd$check(stack, x, y);
    }

    @Unique
    private void gsd$check(ItemStack stack, int x, int y) {
        if (!GlStateDebugMod.probeRenderItem) return;
        GlStateSnapshot before = gsd$snapshot.get();
        if (before == null) return;
        String diff = GlStateSnapshot.capture().diff(before);
        if (diff != null && ThrottledLogger.allow("renderitem:" + diff)) {
            GlStateDebugMod.LOGGER.warn(
                "[RenderItem] GL state leak after rendering {} at ({},{}): {}",
                stack.getItem().getRegistryName(), x, y, diff,
                new Throwable("stacktrace"));
        }
        gsd$snapshot.remove();
    }

    // renderItemOverlayIntoGUI（含冷却遮罩，patch 里新增了 enableBlend）
    @Inject(method = "renderItemOverlayIntoGUI", at = @At("HEAD"))
    private void gsd$headOverlay(net.minecraft.client.gui.FontRenderer fr,
                                 ItemStack stack, int x, int y, String text, CallbackInfo ci) {
        if (GlStateDebugMod.probeRenderItem) gsd$snapshot.set(GlStateSnapshot.capture());
    }

    @Inject(method = "renderItemOverlayIntoGUI", at = @At("RETURN"))
    private void gsd$returnOverlay(net.minecraft.client.gui.FontRenderer fr,
                                   ItemStack stack, int x, int y, String text, CallbackInfo ci) {
        gsd$check(stack, x, y);
    }
}