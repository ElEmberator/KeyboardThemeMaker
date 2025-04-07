package com.keyboard.theme.memo;

import android.content.Context;
import android.graphics.Color;

public class KeyboardTheme {
    private Context context;
    
    // Theme colors
    private int keyColor = 0xFF808080;
    private int keyTextColor = 0xFF000000;
    private int keyStrokeColor = 0xFF000000;
    private int keyShadowColor = 0xFF000000;
    
    public KeyboardTheme(Context context) {
        this.context = context;
    }
    
    public int getKeyColor() {
        return keyColor;
    }
    
    public int getKeyTextColor() {
        return keyTextColor;
    }
    
    public int getKeyStrokeColor() {
        return keyStrokeColor;
    }
    
    public int getKeyShadowColor() {
        return keyShadowColor;
    }
}