package com.lumia;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private JPanel currentPanel;

    public MainFrame() {
        setTitle("Main Frame");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initializing the main frame content
        currentPanel = new JPanel();
        currentPanel.add(new JLabel("Display mainframe !!!!!!"));
        add(currentPanel);
    }

    // Method to check and handle login state
    public void checkAndHandleLoginState() {
        // Check the login state (you already have the getLoginState method)
        if (!TokenManager.verifyToken()) {
            System.out.println("User not logged in. Showing login window instead.");
            replaceContentWithLogin();
        } else {
            System.out.println("User is logged in with valid token.");
        }
    }

    // Method to replace the current content with the LoginFrame
    public void replaceContentWithLogin() {
        // Remove current content from MainFrame
        getContentPane().removeAll();

        // Add the LoginFrame content
        LoginFrame loginFrame = new LoginFrame(this); // pass reference of MainFrame to LoginFrame
        add(loginFrame.currentPanel);

        // Revalidate and repaint to refresh the frame
        revalidate();
        repaint();
    }

    // Method to replace current content with the previous MainFrame content after login
    public void restoreMainFrame() {
        // Remove the login content and replace with MainFrame content
        getContentPane().removeAll();

        // Restore MainFrame content
        currentPanel = new JPanel();
        currentPanel.add(new JLabel("Welcome back to the MainFrame!"));
        add(currentPanel);

        // Revalidate and repaint to refresh the frame
        revalidate();
        repaint();
    }
}
