package com.lumia;

import com.formdev.flatlaf.icons.FlatCheckBoxIcon;
import com.formdev.flatlaf.icons.FlatRadioButtonIcon;
import com.formdev.flatlaf.ui.FlatButtonUI;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.metal.MetalCheckBoxIcon;
import javax.swing.plaf.metal.MetalRadioButtonUI;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class MainFrame extends JFrame {
    private JPanel currentPanel = new JPanel();

    JScrollPane scrollPane;
    JTable articleList;
    JTextField barcodeField = new JTextField();
    JLabel priceLabel = new JLabel();
    JCheckBox isAcquiring = new JCheckBox();

    private boolean wasTicketPrinted = false;

    public MainFrame() {
        setTitle("Client LUMIA");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        currentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        currentPanel.setLayout(new BoxLayout(currentPanel, BoxLayout.Y_AXIS));
        int height = currentPanel.getHeight();
        int width = currentPanel.getWidth();


        ///////////////////////////
        // Product listing panel //
        ///////////////////////////

        String[] columnNames = {"Produit", "Code-barre", "Qté.", "Prix un."};

        DefaultTableModel model = new DefaultTableModel(null, columnNames);

        articleList = new JTable(model) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        articleList.setRowHeight(60);
        TableColumnModel columnModel = articleList.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(1200);
        columnModel.getColumn(0).setMaxWidth(1200);
        columnModel.getColumn(1).setPreferredWidth(600);
        columnModel.getColumn(1).setMaxWidth(600);
        columnModel.getColumn(2).setPreferredWidth(200);
        columnModel.getColumn(2).setMaxWidth(200);
        columnModel.getColumn(3).setPreferredWidth(300);
        columnModel.getColumn(3).setMaxWidth(300);

        articleList.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                double totalPrice = computeTotalPrice();
                String price = Double.toString(totalPrice).concat(" €");
                priceLabel.setText(price);
            }
        });

        articleList.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        articleList.setFocusable(false);
        scrollPane = new JScrollPane(articleList);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setFocusable(false);

        scrollPane.setSize(new Dimension(currentPanel.getWidth(), (int) (height * 0.8)));
        currentPanel.add(scrollPane);


        /////////////////////////////////
        // Product add & removal panel //
        /////////////////////////////////


        JPanel buttonsContainerPanel = new JPanel(new GridLayout(2, 1));
        buttonsContainerPanel.setSize(new Dimension((int) (currentPanel.getWidth()), (int) (height * 0.4)));

        JButton addButton = getAddButton(barcodeField, model);

        JPanel barcodePanel = new JPanel();
        barcodePanel.setLayout(new GridBagLayout());


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        barcodePanel.add(barcodeField, gbc);

        barcodePanel.setSize(new Dimension((int) (currentPanel.getWidth() * 0.6), (int) (height * 0.2)));

        barcodeField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    addButton.doClick();
                }
            }
        });

        JButton removeButton = new JButton("Supprimer");
        removeButton.setBackground(new Color(0xD42525));
        removeButton.setForeground(Color.WHITE);
        removeButton.setOpaque(true);
        removeButton.setBorderPainted(false);
        removeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        removeButton.setUI(new FlatButtonUI(false) {
            @Override
            protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect, Rectangle textRect, Rectangle iconRect) {
                if (b.hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    try {
                        g2.setColor(Color.BLUE);
                        g2.setStroke(new BasicStroke(3));
                        g2.draw(viewRect);
                    } finally {
                        g2.dispose();
                    }
                }
            }
        });
        removeButton.addActionListener(e -> {
            int selectedRow = articleList.getSelectedRow();
            if (selectedRow != -1) {
                model.removeRow(selectedRow);
            } else {
                if (model.getRowCount() > 0) {
                    model.removeRow(model.getRowCount() - 1);
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        buttonPanel.add(barcodePanel);
        buttonPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        buttonPanel.add(addButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(removeButton);
        buttonPanel.setSize(new Dimension(currentPanel.getWidth(), (int) (height * 0.2)));

        buttonsContainerPanel.add(buttonPanel);


        ///////////////////
        // Selling panel //
        ///////////////////

        JPanel sellPanel = new JPanel(new GridLayout(1, 2));
        sellPanel.setSize(new Dimension(currentPanel.getWidth(), (int) (height * 0.2)));

        // Left side for radios
        JPanel isSoldPanel = new JPanel();
        isSoldPanel.setLayout(new GridBagLayout());

        GridBagConstraints isSoldGbc = new GridBagConstraints();
        isSoldGbc.gridx = 0;
        isSoldGbc.gridy = 0;
        isSoldGbc.anchor = GridBagConstraints.CENTER;

        JCheckBox isAcquiring = getIsAcquiringCheckbox();
        isAcquiring.setAlignmentX(Component.CENTER_ALIGNMENT);
        isAcquiring.setAlignmentY(Component.CENTER_ALIGNMENT);

        isSoldPanel.add(isAcquiring, isSoldGbc);
        isSoldPanel.setSize(new Dimension(currentPanel.getWidth() / 2, (int) (height * 0.2)));

        // Right side for validation and ticket printing
        JPanel validatePanel = new JPanel(new GridLayout(2, 1));

        JPanel pricePanel = new JPanel(new GridLayout(1, 2));
        JLabel totalLabel = new JLabel("Total:");
        pricePanel.add(totalLabel);
        pricePanel.add(priceLabel);

        GridLayout validateButtonsPanelLayout = new GridLayout(1, 2);
        validateButtonsPanelLayout.setHgap(10);
        JPanel validateButtonsPanel = new JPanel(validateButtonsPanelLayout);

        JButton validateButton = getValidateButton();
        JButton ticketButton = getTicketButton();

        validateButtonsPanel.add(ticketButton);
        validateButtonsPanel.add(validateButton);

        validatePanel.add(pricePanel);
        validatePanel.add(validateButtonsPanel);

        sellPanel.add(isSoldPanel);
        sellPanel.add(validatePanel);

        buttonsContainerPanel.add(sellPanel);

        currentPanel.add(buttonsContainerPanel);

        add(currentPanel);
    }

    private double computeTotalPrice() {
        double total = 0;
        TableModel model = articleList.getModel();
        int rowCount = model.getRowCount();

        for (int i = 0; i < rowCount; i++) {
            double secondColumnValue = ((Number) model.getValueAt(i, 2)).doubleValue();
            double thirdColumnValue = ((Number) model.getValueAt(i, 3)).doubleValue();
            total += secondColumnValue * (thirdColumnValue * 100);
        }

        double roundOff = (double) Math.round(total * 100) / 100;

        return roundOff / 100;
    }

    public JTextField getBarcodeField() {
        return barcodeField;
    }

    private JCheckBox getIsAcquiringCheckbox() {
        isAcquiring.setText("Entrée en stock ?");
        isAcquiring.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        isAcquiring.setFocusable(true);  // Allow the checkbox to gain focus
        isAcquiring.setFocusPainted(false);  // Disable the default focus painting

        isAcquiring.setIcon(new FlatCheckBoxIcon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                int width = 30;  // Size of the checkbox
                int height = 30;

                // Set the preferred size of the icon
                setPreferredSize(new Dimension(width, height));

                // Draw the background of the checkbox (a simple rectangle)
                g.setColor(Color.WHITE);
                g.fillRect(x, y, width, height);  // Empty space inside the checkbox
                g.setColor(Color.BLACK);  // Outline color
                g.drawRect(x, y, width, height);  // Draw the border of the checkbox

                Graphics2D g2d = (Graphics2D) g;

                // If the checkbox is selected, draw a checkmark
                if (isAcquiring.isSelected()) {
                    // Use Graphics2D for smoother drawing
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setStroke(new BasicStroke(3)); // Set the thickness of the checkmark line

                    // Draw the checkmark path (a more elegant approach)
                    int startX = x + 5;
                    int startY = y + 15;
                    int middleX = x + 12;
                    int middleY = y + 22;
                    int endX = x + 25;
                    int endY = y + 8;

                    // Draw the checkmark in a more "path-like" way
                    g2d.drawLine(startX, startY, middleX, middleY);
                    g2d.drawLine(middleX, middleY, endX, endY);
                }

                // Draw a blue border when the checkbox has focus
                if (isAcquiring.hasFocus()) {
                    g2d.setColor(Color.BLUE);
                    g2d.setStroke(new BasicStroke(5));  // Blue border thickness
                    g2d.drawRect(x - 5, y - 5, width + 10, height + 10);  // Outer blue border
                }
            }

            // Override the getIconWidth and getIconHeight to match the preferred size
            @Override
            public int getIconWidth() {
                return 30;  // Width of the icon
            }

            @Override
            public int getIconHeight() {
                return 30;  // Height of the icon
            }
        });

        return isAcquiring;
    }

    private JButton getTicketButton() {
        JButton ticketButton = new JButton("Impr. reçu");
        ticketButton.setBackground(new Color(0x542292));
        ticketButton.setForeground(Color.WHITE);
        ticketButton.setOpaque(true);
        ticketButton.setBorderPainted(false);
        ticketButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        ticketButton.setUI(new FlatButtonUI(false) {
            @Override
            protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect, Rectangle textRect, Rectangle iconRect) {
                if (b.hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    try {
                        g2.setColor(Color.BLUE);
                        g2.setStroke(new BasicStroke(3));
                        g2.draw(viewRect);
                    } finally {
                        g2.dispose();
                    }
                }
            }
        });
        ticketButton.addActionListener(e -> {
            if (isAcquiring.isSelected()) {
                int response = JOptionPane.showConfirmDialog(
                        null,
                        "Cette transaction est marquée comme une entrée en stock.\nVoulez-vous vraiment imprimer un ticket ?",
                        "Attention",
                        JOptionPane.YES_NO_OPTION
                );

                if (response == JOptionPane.NO_OPTION || response == JOptionPane.CLOSED_OPTION) {
                    return;
                }
            }

            DefaultTableModel dm = (DefaultTableModel) articleList.getModel();

            int rowCount = dm.getRowCount();
            List<Article> articles = new ArrayList<>();

            for (int i = 0; i < rowCount; i++) {
                int amount = (Integer) dm.getValueAt(i, 2);  // "amount" column (assuming it's a double)
                String name = String.valueOf(dm.getValueAt(i, 0));  // "name" column
                double unitPrice = (Double) dm.getValueAt(i, 3);  // "price" column (assuming it's a double)

                Article article = new Article(amount, name, unitPrice);
                articles.add(article);
            }

            printTicket(articles);
        });

        return ticketButton;
    }

    private JButton getAddButton(JTextField barcodeField, DefaultTableModel model) {
        JButton addButton = new JButton("Ajouter");
        addButton.setBackground(new Color(0x542292));
        addButton.setForeground(Color.WHITE);
        addButton.setOpaque(true);
        addButton.setBorderPainted(false);
        addButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addButton.setUI(new FlatButtonUI(false) {
            @Override
            protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect, Rectangle textRect, Rectangle iconRect) {
                if (b.hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    try {
                        g2.setColor(Color.BLUE);
                        g2.setStroke(new BasicStroke(3));
                        g2.draw(viewRect);
                    } finally {
                        g2.dispose();
                    }
                }
            }
        });
        addButton.addActionListener(e -> {
            String barcode = barcodeField.getText();

            if (barcodeField.getText().length() != 13) {
                JOptionPane.showMessageDialog(null, "Code-barre invalide !");
                barcodeField.setText("");
                return;
            }

            if (barcode.length() == 0) {
                return;
            }

            boolean exists = false;

            for (int row = 0; row < model.getRowCount(); row++) {
                String existingBarcode = (String) model.getValueAt(row, 1);

                if (existingBarcode.equals(barcode)) {
                    int currentCount = (int) model.getValueAt(row, 2);
                    model.setValueAt(currentCount + 1, row, 2);
                    exists = true;
                    barcodeField.setText(null);
                    break;
                }
            }

            if (!exists) {
                ApiRequest req = new ApiRequest();
                ProductResponse response = req.makeGetProductRequest(barcode);

                if (response.isSuccess()) {
                    model.addRow(new Object[]{response.getProductName(), response.getProductBarcode(), 1, response.getProductPrice()});
                    barcodeField.setText("");
                } else {
                    JOptionPane.showMessageDialog(null, "Code-barre invalide !");
                }
            }
        });
        return addButton;
    }

    private JButton getValidateButton() {
        JButton validateButton = new JButton("Valider");
        validateButton.setBackground(new Color(0xBAAB5C));
        validateButton.setForeground(Color.WHITE);
        validateButton.setOpaque(true);
        validateButton.setBorderPainted(false);
        validateButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        validateButton.setUI(new FlatButtonUI(false) {
            @Override
            protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect, Rectangle textRect, Rectangle iconRect) {
                if (b.hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    try {
                        g2.setColor(Color.BLUE);
                        g2.setStroke(new BasicStroke(3));
                        g2.draw(viewRect);
                    } finally {
                        g2.dispose();
                    }
                }
            }
        });
        validateButton.addActionListener(e -> {
            if (!wasTicketPrinted && !isAcquiring.isSelected()) {
                int response = JOptionPane.showConfirmDialog(
                        null,
                        "Aucun ticket n'a été imprimé. Valider sans ticket ?",
                        "Attention",
                        JOptionPane.YES_NO_OPTION
                );

                if (response == JOptionPane.NO_OPTION) {
                    return;
                }
            }

            DefaultTableModel dm = (DefaultTableModel) articleList.getModel();
            ApiRequest req = new ApiRequest();

            boolean isAcquired = isAcquiring.isSelected();

            int rowCount = dm.getRowCount();
            List<Map<String, String>> products = new ArrayList<>();

            for (int i = 0; i < rowCount; i++) {
                Map<String, String> product = new HashMap<>();
                product.put("barcode", String.valueOf(dm.getValueAt(i, 1)));  // "barcode" column
                product.put("amount", String.valueOf(dm.getValueAt(i, 2)));   // "amount" column
                products.add(product);
            }

            boolean success = req.confirmBasket(products, isAcquired);

            if (!success) {
                JOptionPane.showMessageDialog(null, "Erreur dans la confirmation de l'achat !");
                return;
            }

            barcodeField.setText("");

            while (dm.getRowCount() > 0) {
                dm.removeRow(0);
            }

            wasTicketPrinted = false;
            barcodeField.requestFocusInWindow();
        });

        return validateButton;
    }

    private void printTicket(List<Article> articles) {
        double totalPrice = computeTotalPrice();

        Ticket ticket = new Ticket(articles, totalPrice);

        boolean success = ticket.print();

        if (!success) {
            JOptionPane.showMessageDialog(null, "Erreur d'impression du ticket !");
            return;
        }

        wasTicketPrinted = true;
    }

    public void checkAndHandleLoginState() {
        if (!TokenManager.verifyToken()) {
            replaceContentWithLogin();
        }
    }

    public void replaceContentWithLogin() {
        getContentPane().removeAll();

        LoginFrame loginFrame = new LoginFrame(this);
        add(loginFrame.currentPanel);

        // Revalidate and repaint to refresh the frame
        revalidate();
        repaint();
    }

    public void restoreMainFrame() {
        getContentPane().removeAll();

        MainFrame mainFrame = new MainFrame();
        add(mainFrame.currentPanel);

        // Revalidate and repaint to refresh the frame
        revalidate();
        repaint();
    }
}