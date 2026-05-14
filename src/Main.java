import gui.MenuPrincipalGUI;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            // Mantém o visual elegante do Windows
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            // Abre o Menu Principal ao iniciar o sistema
            new MenuPrincipalGUI().setVisible(true);
        });
    }
}