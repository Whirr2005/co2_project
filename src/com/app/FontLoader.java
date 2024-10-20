package com.app;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class FontLoader {

    private static Font satoshiFont;

    static {
        try {
            //get font from ttf file
            satoshiFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/fonts/Satoshi-Variable.ttf"));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(satoshiFont);  // create (register) font
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    //function to set font at a set size
    public static Font getSatoshiFont(float size) {
        return satoshiFont != null ? satoshiFont.deriveFont(size) : new Font("Arial", Font.PLAIN, (int) size);
    }
}
