package com.app;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class FontLoader {

    private static Font satoshiFont;

    static {
        try {
            // Load Satoshi font from the file once
            satoshiFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/fonts/Satoshi-Variable.ttf"));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(satoshiFont);  // Register the font
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    // Method to get the font with a specific size
    public static Font getSatoshiFont(float size) {
        return satoshiFont != null ? satoshiFont.deriveFont(size) : new Font("Arial", Font.PLAIN, (int) size);
    }
}
