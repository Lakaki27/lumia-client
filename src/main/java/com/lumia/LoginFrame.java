package com.lumia;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {

    private MainFrame mainFrame;
    JPanel currentPanel = new JPanel();

    JLabel emailLabel = new JLabel("Email");
    JTextField emailField = new JTextField();
    JLabel passwordLabel = new JLabel("Mot de passe");
    JPasswordField passwordField = new JPasswordField();
    JCheckBox showPasswordButton = new JCheckBox("Afficher");
    JLabel errorLabel = new JLabel("");
    JButton loginButton = new JButton("LOGIN");

    LoginFrame(MainFrame mainframe) {
        this.mainFrame = mainframe;

        setTitle("Login Form");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        setSize(400, 250);

        showPasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                passwordField.setEchoChar(showPasswordButton.isSelected() ? (char) 0 : (Character) UIManager.get("PasswordField.echoChar") );
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Logic for login
                String email = emailField.getText();
                char[] password = passwordField.getPassword();

                if (email.isEmpty() || password.length == 0) {
                    errorLabel.setText("Veuillez saisir vos identifiants.");
                    return;
                }

                ApiRequest req = new ApiRequest();
                LoginResponse response = req.makeLoginRequest(email, new String(password));

                if (response.isSuccess()) {
                    TokenManager.saveToken(response.getMessage());
                    mainFrame.restoreMainFrame();
                } else {
                    errorLabel.setText(response.getMessage());
                }
            }
        });

        errorLabel.setForeground(Color.RED);

        gbc.insets = new Insets(10, 10, 10, 10);  // add padding between components
        gbc.gridx = 0;
        gbc.gridy = 0;
        currentPanel.add(emailLabel, gbc);

        gbc.gridy = 1;
        currentPanel.add(emailField, gbc);

        gbc.gridy = 2;
        currentPanel.add(passwordLabel, gbc);

        gbc.gridy = 3;
        currentPanel.add(passwordField, gbc);

        gbc.gridy = 4;
        currentPanel.add(showPasswordButton, gbc);

        gbc.gridy = 5;
        currentPanel.add(loginButton, gbc);

        gbc.gridy = 6;
        currentPanel.add(errorLabel, gbc);
    }
}