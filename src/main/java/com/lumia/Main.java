package com.lumia;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Set the Look and Feel to FlatLaf or other theme
        try {
            FlatLaf.registerCustomDefaultsSource("com.lumia.themes");
            FlatLightLaf.setup();
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        // Create MainFrame and show it
        MainFrame mainFrame = new MainFrame();
        mainFrame.setVisible(true);

        // Check login state and replace content in MainFrame if necessary
        mainFrame.checkAndHandleLoginState();
    }
}

