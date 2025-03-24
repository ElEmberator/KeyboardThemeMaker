package com.ElEmberator.themebuilder.model;

import android.graphics.Color;

public class ThemeConfig {
    private int solidColor = Color.GRAY;
    private int strokeColor = Color.BLACK;
    private float strokeWidthDp = 1.0f;
    private float cornerRadiusDp = 4.0f;
    private boolean useXmlForKeys = true;
    private boolean useGradient = false;
    private int gradientStart = Color.BLUE;
    private int gradientEnd = Color.CYAN;
    private int gradientAngle = 0;
    private boolean useImageBackground = false;
    private boolean useTransparentKeys = false;
    private int shadowColor = Color.argb(100, 0, 0, 0);
    private float shadowDx = 0.0f;
    private float shadowDy = 2.0f;
    private float shadowRadius = 4.0f;

    // Getters and Setters
    public int getSolidColor() { return solidColor; }
    public void setSolidColor(int solidColor) { this.solidColor = solidColor; }
    public int getStrokeColor() { return strokeColor; }
    public void setStrokeColor(int strokeColor) { this.strokeColor = strokeColor; }
    public float getStrokeWidthDp() { return strokeWidthDp; }
    public void setStrokeWidthDp(float strokeWidthDp) { this.strokeWidthDp = strokeWidthDp; }
    public float getCornerRadiusDp() { return cornerRadiusDp; }
    public void setCornerRadiusDp(float cornerRadiusDp) { this.cornerRadiusDp = cornerRadiusDp; }
    public boolean isUseXmlForKeys() { return useXmlForKeys; }
    public void setUseXmlForKeys(boolean useXmlForKeys) { this.useXmlForKeys = useXmlForKeys; }
    public boolean isUseGradient() { return useGradient; }
    public void setUseGradient(boolean useGradient) { this.useGradient = useGradient; }
    public int getGradientStart() { return gradientStart; }
    public void setGradientStart(int gradientStart) { this.gradientStart = gradientStart; }
    public int getGradientEnd() { return gradientEnd; }
    public void setGradientEnd(int gradientEnd) { this.gradientEnd = gradientEnd; }
    public int getGradientAngle() { return gradientAngle; }
    public void setGradientAngle(int gradientAngle) { this.gradientAngle = gradientAngle; }
    public boolean isUseImageBackground() { return useImageBackground; }
    public void setUseImageBackground(boolean useImageBackground) { this.useImageBackground = useImageBackground; }
    public boolean isUseTransparentKeys() { return useTransparentKeys; }
    public void setUseTransparentKeys(boolean useTransparentKeys) { this.useTransparentKeys = useTransparentKeys; }
    public int getShadowColor() { return shadowColor; }
    public void setShadowColor(int shadowColor) { this.shadowColor = shadowColor; }
    public float getShadowDx() { return shadowDx; }
    public void setShadowDx(float shadowDx) { this.shadowDx = shadowDx; }
    public float getShadowDy() { return shadowDy; }
    public void setShadowDy(float shadowDy) { this.shadowDy = shadowDy; }
    public float getShadowRadius() { return shadowRadius; }
    public void setShadowRadius(float shadowRadius) { this.shadowRadius = shadowRadius; }
}
