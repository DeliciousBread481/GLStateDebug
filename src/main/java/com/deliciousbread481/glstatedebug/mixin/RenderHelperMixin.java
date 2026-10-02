package com.deliciousbread481.glstatedebug.mixin;

import com.deliciousbread481.glstatedebug.GlStateDebugMod;
import com.deliciousbread481.glstatedebug.ThrottledLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderHelper.class)
public abstract class RenderHelperMixin {

    private static int gsd$guiLightingDepth = 0;

    @Inject(method = "enableGUIStandardItemLighting", at = @At("HEAD"))
    private static void gsd$enableGui(CallbackInfo ci) {
        if (!GlStateDebugMod.probeRenderHelper) return;
        gsd$guiLightingDepth++;
        if (gsd$guiLightingDepth > 1 && ThrottledLogger.allow("guiLight:nested")) {
            GlStateDebugMod.LOGGER.warn(
                "[RenderHelper] nested enableGUIStandardItemLighting, depth={}",
                gsd$guiLightingDepth);
        }
    }

    @Inject(method = "disableStandardItemLighting", at = @At("HEAD"))
    private static void gsd$disable(CallbackInfo ci) {
        if (!GlStateDebugMod.probeRenderHelper) return;
        if (Minecraft.getMinecraft().currentScreen != null) {
            gsd$guiLightingDepth = Math.max(0, gsd$guiLightingDepth - 1);
        }
        if (gsd$guiLightingDepth == 0 && Minecraft.getMinecraft().currentScreen == null
                && ThrottledLogger.allow("guiLight:unbalanced")) {
        }
    }
}