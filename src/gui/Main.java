package gui;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            // visual
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            // Abre o menu principal ao iniciar o sistema
            MenuPrincipalGUI menu = new MenuPrincipalGUI();
            menu.setVisible(true);
        });
    }
}
