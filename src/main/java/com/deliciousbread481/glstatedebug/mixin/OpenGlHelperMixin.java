package com.deliciousbread481.glstatedebug.mixin;

import com.deliciousbread481.glstatedebug.GlStateDebugMod;
import com.deliciousbread481.glstatedebug.ThrottledLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OpenGlHelper.class)
public abstract class OpenGlHelperMixin {
    @Inject(method = "setLightmapTextureCoords", at = @At("HEAD"))
    private static void gsd$onLightmap(int target, float u, float v, CallbackInfo ci) {
        if (!GlStateDebugMod.probeLightmap) return;
        if (Minecraft.getMinecraft().currentScreen == null) return;
        StackTraceElement[] st = Thread.currentThread().getStackTrace();
        String caller = st.length > 3 ? st[3].toString() : "unknown";
        if (ThrottledLogger.allow("lightmap:" + caller)) {
            GlStateDebugMod.LOGGER.warn(
                "[OpenGlHelper] setLightmapTextureCoords(target={}, u={}, v={}) while GUI open, from {}",
                target, u, v, caller, new Throwable("stacktrace"));
        }
    }
}