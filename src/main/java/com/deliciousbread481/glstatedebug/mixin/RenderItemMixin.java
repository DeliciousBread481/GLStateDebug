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
  
    @Inject(method = {  
        "renderItemAndEffectIntoGUI",  
        "renderItemModelIntoGUI",  
        "renderItemOverlayIntoGUI"  
    }, at = @At("HEAD"))  
    private void gsd$head(CallbackInfo ci) {  
        if (GlStateDebugMod.probeRenderItem) gsd$snapshot.set(GlStateSnapshot.capture());  
    }  
  
    @Inject(method = {  
        "renderItemAndEffectIntoGUI",  
        "renderItemModelIntoGUI",  
        "renderItemOverlayIntoGUI"  
    }, at = @At("RETURN"))  
    private void gsd$return(CallbackInfo ci) {  
        if (!GlStateDebugMod.probeRenderItem) return;  
        GlStateSnapshot before = gsd$snapshot.get();  
        gsd$snapshot.remove();  
        if (before == null) return;  
        String diff = GlStateSnapshot.capture().diff(before);  
        if (diff != null && ThrottledLogger.allow("renderitem:" + diff)) {  
            GlStateDebugMod.LOGGER.warn("[RenderItem] GL state leak: {}", diff,  
                new Throwable("stacktrace"));  
        }  
    }  
}