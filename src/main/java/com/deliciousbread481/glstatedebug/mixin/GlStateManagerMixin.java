package com.deliciousbread481.glstatedebug.mixin;

import com.deliciousbread481.glstatedebug.GlStateDebugMod;
import com.deliciousbread481.glstatedebug.ThrottledLogger;
import net.minecraft.client.renderer.GlStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GlStateManager.class)
public abstract class GlStateManagerMixin {
    @Inject(method = "color(FFFF)V", at = @At("HEAD"))
    private static void gsd$onColor(float r, float g, float b, float a, CallbackInfo ci) {
        if (!GlStateDebugMod.probeColor || a >= 0.99f) return;
        StackTraceElement[] st = Thread.currentThread().getStackTrace();
        String caller = st.length > 3 ? st[3].toString() : "unknown";
        if (ThrottledLogger.allow("color:" + caller)) {
            GlStateDebugMod.LOGGER.warn(
                "[GlStateManager] color() with alpha={} from {}",
                String.format(java.util.Locale.ROOT, "%.3f", a), caller,
                new Throwable("stacktrace"));
        }
    }
}