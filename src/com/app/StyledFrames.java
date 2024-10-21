package com.app;

import javax.swing.*;
import java.awt.*;

import com.app.FontLoader;


//class for creating many similar popup windows
public class StyledFrames {
    static void newPopup(String sentContent, String title) {
        JFrame popupWindow = new JFrame(title);
        popupWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //close (dispose only does the current) window
        popupWindow.setSize(500, 300);
        popupWindow.setLocationRelativeTo(null);
        popupWindow.setResizable(false);
        popupWindow.setVisible(true);

        //create panel for content
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.decode("#24293e"));




        JTextArea content = new JTextArea(10,10);
        content.setEditable(false);
        content.setLineWrap(true);
        content.setText(sentContent);
        content.setBackground(Color.decode("#24293e"));
        content.setSize(450, 200);
        content.setFont(FontLoader.getSatoshiFont(18f));
        content.setForeground(Color.decode("#f4f5fc"));
        contentPanel.add(content);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.decode("#24293e"));
        buttonPanel.setSize(500, 100);
        JButton OKButton = new roundedButton("OK");


        OKButton.addActionListener(_ -> {
            popupWindow.dispose();
        });

        //add buttons to panel
        buttonPanel.add(OKButton);
        popupWindow.setLayout(new BorderLayout());
        popupWindow.add(contentPanel, BorderLayout.CENTER);
        popupWindow.add(buttonPanel, BorderLayout.SOUTH);

    }


}
