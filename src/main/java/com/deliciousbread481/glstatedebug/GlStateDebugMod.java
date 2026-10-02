package com.deliciousbread481.glstatedebug;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = GlStateDebugMod.MODID, name = GlStateDebugMod.NAME,
     version = GlStateDebugMod.VERSION, clientSideOnly = true)
public class GlStateDebugMod {

    public static final String MODID = "glstatedebug";
    public static final String NAME = "GL State Debug";
    public static final String VERSION = "1.0.0";

    public static final Logger LOGGER = LogManager.getLogger(NAME);

    public static boolean probeRenderItem   = boolProp("item", true);
    public static boolean probeColor        = boolProp("color", true);
    public static boolean probeLightmap     = boolProp("lightmap", true);
    public static boolean probeRenderHelper = boolProp("lighting", true);

    private static boolean boolProp(String key, boolean def) {
        return Boolean.parseBoolean(
            System.getProperty("glstatedebug." + key, Boolean.toString(def)));
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("GL State Debug loaded. probes: item={}, color={}, lightmap={}, lighting={}",
            probeRenderItem, probeColor, probeLightmap, probeRenderHelper);
    }
}