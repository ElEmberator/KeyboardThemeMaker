package com.ElEmberator.themebuilder.builder;

import android.content.Context;
import android.graphics.Color;
import com.ElEmberator.themebuilder.model.ThemeConfig;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ThemeGenerator {
    private static final String[] REQUIRED_FILES = {
        "btn_keyboard_key_normal", "btn_keyboard_key_normal_off", "btn_keyboard_key_normal_on",
        "btn_keyboard_key_pressed", "btn_keyboard_key_pressed_off", "btn_keyboard_key_pressed_on",
        "btn_keyboard_key_alt_normal", "btn_keyboard_key_alt_normal_off", "btn_keyboard_key_alt_normal_on",
        "btn_keyboard_key_alt_pressed", "btn_keyboard_key_alt_pressed_off", "btn_keyboard_key_alt_pressed_on",
        "keyboard_background", "keyboard_key_feedback_background", "keyboard_key_feedback_more_background"
    };

    private final Context context;
    private final ThemeConfig config;

    public ThemeGenerator(Context context, ThemeConfig config) {
        this.context = context;
        this.config = config;
    }

    public void generateThemeFiles(File outputDir) throws IOException {
        if (config.isUseXmlForKeys()) {
            if (config.isUseTransparentKeys()) {
                generateTransparentXmlKeys(outputDir);
            } else {
                generateXmlKeys(outputDir);
            }
        }
        generateBackgroundXml(outputDir);
        generateSelectorFiles(outputDir);
    }

    private void generateXmlKeys(File outputDir) throws IOException {
        for (String fileName : REQUIRED_FILES) {
            if (fileName.startsWith("keyboard_")) continue;
            
            String xmlContent = generateKeyShapeXml(
                fileName.contains("pressed"),
                fileName.contains("_alt"),
                fileName.contains("_on"),
                fileName.contains("_off")
            );
            
            writeXmlFile(outputDir, fileName, xmlContent);
        }
    }

    private String generateKeyShapeXml(boolean isPressed, boolean isAlt, boolean isOn, boolean isOff) {
        int fillColor = isPressed ? darkenColor(config.getSolidColor(), 0.2f) : config.getSolidColor();
        int strokeColor = isPressed ? darkenColor(config.getStrokeColor(), 0.2f) : config.getStrokeColor();
        
        if (isAlt) {
            fillColor = adjustHue(fillColor, 20);
            strokeColor = adjustHue(strokeColor, 20);
        }
        
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        
        if (config.getShadowRadius() > 0) {
            xml.append("<layer-list xmlns:android=\"http://schemas.android.com/apk/res/android\">\n");
            xml.append("    <item android:left=\"").append(Math.max(0, config.getShadowDx())).append("dp\"")
               .append(" android:top=\"").append(Math.max(0, config.getShadowDy())).append("dp\"")
               .append(" android:right=\"").append(Math.max(0, -config.getShadowDx())).append("dp\"")
               .append(" android:bottom=\"").append(Math.max(0, -config.getShadowDy())).append("dp\">\n");
            xml.append("        <shape android:shape=\"rectangle\">\n");
            xml.append("            <solid android:color=\"").append(formatColor(config.getShadowColor())).append("\" />\n");
            xml.append("            <corners android:radius=\"").append(config.getCornerRadiusDp()).append("dp\" />\n");
            xml.append("        </shape>\n");
            xml.append("    </item>\n");
            xml.append("    <item>\n");
        }
        
        xml.append("<shape xmlns:android=\"http://schemas.android.com/apk/res/android\"\n")
           .append("    android:shape=\"rectangle\">\n");
        
        if (config.isUseGradient()) {
            xml.append("    <gradient\n")
               .append("        android:startColor=\"").append(formatColor(config.getGradientStart())).append("\"\n")
               .append("        android:endColor=\"").append(formatColor(config.getGradientEnd())).append("\"\n")
               .append("        android:angle=\"").append((config.getGradientAngle() * 45) % 360).append("\"\n")
               .append("        android:type=\"linear\" />\n");
        } else {
            xml.append("    <solid android:color=\"").append(formatColor(fillColor)).append("\" />\n");
        }
        
        xml.append("    <stroke\n")
           .append("        android:width=\"").append(config.getStrokeWidthDp()).append("dp\"\n")
           .append("        android:color=\"").append(formatColor(strokeColor)).append("\" />\n");
        
        xml.append("    <corners android:radius=\"").append(config.getCornerRadiusDp()).append("dp\" />\n");
        xml.append("</shape>\n");
        
        if (config.getShadowRadius() > 0) {
            xml.append("    </item>\n");
            xml.append("</layer-list>\n");
        }
        
        return xml.toString();
    }

    private void writeXmlFile(File outputDir, String fileName, String content) throws IOException {
        File xmlFile = new File(outputDir, fileName + ".xml");
        try (FileOutputStream fos = new FileOutputStream(xmlFile)) {
            fos.write(content.getBytes());
        }
    }

    private String formatColor(int color) {
        return String.format("#%08X", color);
    }

    private int darkenColor(int color, float factor) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= (1 - factor);
        return Color.HSVToColor(Color.alpha(color), hsv);
    }

    private int adjustHue(int color, float degrees) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[0] = (hsv[0] + degrees) % 360;
        return Color.HSVToColor(Color.alpha(color), hsv);
    }

    private void generateTransparentXmlKeys(File outputDir) throws IOException {
        for (String fileName : REQUIRED_FILES) {
            if (fileName.startsWith("keyboard_")) continue;
            
            String xmlContent = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<shape xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                "    android:shape=\"rectangle\">\n" +
                "    <solid android:color=\"@android:color/transparent\" />\n";
            
            if (config.getStrokeWidthDp() > 0) {
                int strokeColor = fileName.contains("pressed") ? 
                    darkenColor(config.getStrokeColor(), 0.2f) : config.getStrokeColor();
                if (fileName.contains("_alt")) strokeColor = adjustHue(strokeColor, 20);
                
                xmlContent += "    <stroke\n" +
                    "        android:width=\"" + config.getStrokeWidthDp() + "dp\"\n" +
                    "        android:color=\"" + formatColor(strokeColor) + "\" />\n";
            }
            
            xmlContent += "    <corners android:radius=\"" + config.getCornerRadiusDp() + "dp\" />\n" +
                "</shape>\n";
            
            writeXmlFile(outputDir, fileName, xmlContent);
        }
    }

    private void generateBackgroundXml(File outputDir) throws IOException {
        String xmlContent;
        
        if (config.isUseGradient()) {
            xmlContent = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<shape xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                "    android:shape=\"rectangle\">\n" +
                "    <gradient\n" +
                "        android:startColor=\"" + formatColor(config.getGradientStart()) + "\"\n" +
                "        android:endColor=\"" + formatColor(config.getGradientEnd()) + "\"\n" +
                "        android:angle=\"" + (config.getGradientAngle() * 45) % 360 + "\"\n" +
                "        android:type=\"linear\" />\n" +
                "</shape>";
        } else {
            xmlContent = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<shape xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                "    android:shape=\"rectangle\">\n" +
                "    <solid android:color=\"" + formatColor(config.getSolidColor()) + "\" />\n" +
                "</shape>";
        }
        
        writeXmlFile(outputDir, "keyboard_background", xmlContent);
    }

    private void generateSelectorFiles(File outputDir) throws IOException {
        writeXmlFile(outputDir, "btn_keyboard_key", generateKeySelector(false));
        writeXmlFile(outputDir, "btn_keyboard_key_alt", generateKeySelector(true));
        writeXmlFile(outputDir, "keyboard_key_feedback", generateFeedbackSelector());
    }

    private String generateKeySelector(boolean isAlt) {
        String prefix = isAlt ? "btn_keyboard_key_alt" : "btn_keyboard_key";
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<selector xmlns:android=\"http://schemas.android.com/apk/res/android\">\n" +
            "    <item android:state_checkable=\"true\" android:state_checked=\"true\" android:state_pressed=\"true\" android:drawable=\"@" + prefix + "_pressed_on\" />\n" +
            "    <item android:state_checkable=\"true\" android:state_pressed=\"true\" android:drawable=\"@" + prefix + "_pressed_off\" />\n" +
            "    <item android:state_checkable=\"true\" android:state_checked=\"true\" android:drawable=\"@" + prefix + "_normal_on\" />\n" +
            "    <item android:state_checkable=\"true\" android:drawable=\"@" + prefix + "_normal_off\" />\n" +
            "    <item android:state_pressed=\"true\" android:drawable=\"@" + prefix + "_pressed\" />\n" +
            "    <item android:drawable=\"@" + prefix + "_normal\" />\n" +
            "</selector>";
    }

    private String generateFeedbackSelector() {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<selector xmlns:android=\"http://schemas.android.com/apk/res/android\">\n" +
            "    <item android:drawable=\"@keyboard_key_feedback_more_background\" android:state_long_pressable=\"true\" />\n" +
            "    <item android:drawable=\"@keyboard_key_feedback_background\" />\n" +
            "</selector>";
    }
}
