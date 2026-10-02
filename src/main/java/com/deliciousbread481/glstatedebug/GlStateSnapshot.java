package com.deliciousbread481.glstatedebug;

import org.lwjgl.opengl.GL11;

import java.util.Locale;

public final class GlStateSnapshot {

    public final float r, g, b, a;
    public final boolean blend, lighting, depthTest, texture2d;

    private GlStateSnapshot() {
        float[] color = currentColor();
        this.r = color[0];
        this.g = color[1];
        this.b = color[2];
        this.a = color[3];
        this.blend     = GL11.glIsEnabled(GL11.GL_BLEND);
        this.lighting  = GL11.glIsEnabled(GL11.GL_LIGHTING);
        this.depthTest = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        this.texture2d = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
    }

    public static GlStateSnapshot capture() {
        return new GlStateSnapshot();
    }

    private static float[] currentColor() {
        float[] out = new float[4];
        try {
            java.nio.FloatBuffer buf = org.lwjgl.BufferUtils.createFloatBuffer(16);
            GL11.glGetFloat(GL11.GL_CURRENT_COLOR, buf);
            for (int i = 0; i < 4; i++) out[i] = buf.get(i);
        } catch (Throwable t) {
            java.nio.FloatBuffer buf = java.nio.ByteBuffer
                .allocateDirect(16 * 4)
                .order(java.nio.ByteOrder.nativeOrder())
                .asFloatBuffer();
            GL11.glGetFloat(GL11.GL_CURRENT_COLOR, buf);
            for (int i = 0; i < 4; i++) out[i] = buf.get(i);
        }
        return out;
    }

    public String diff(GlStateSnapshot before) {
        StringBuilder sb = new StringBuilder();
        if (Math.abs(before.a - a) > 0.001f
                || Math.abs(before.r - r) > 0.001f
                || Math.abs(before.g - g) > 0.001f
                || Math.abs(before.b - b) > 0.001f) {
            sb.append(String.format(Locale.ROOT,
                "color (%.3f,%.3f,%.3f,%.3f) -> (%.3f,%.3f,%.3f,%.3f) | ",
                before.r, before.g, before.b, before.a, r, g, b, a));
        }
        if (before.blend != blend)         sb.append("blend ").append(before.blend).append("->").append(blend).append(" | ");
        if (before.lighting != lighting)   sb.append("lighting ").append(before.lighting).append("->").append(lighting).append(" | ");
        if (before.depthTest != depthTest) sb.append("depthTest ").append(before.depthTest).append("->").append(depthTest).append(" | ");
        if (before.texture2d != texture2d) sb.append("texture2d ").append(before.texture2d).append("->").append(texture2d).append(" | ");
        return sb.length() == 0 ? null : sb.toString();
    }
}