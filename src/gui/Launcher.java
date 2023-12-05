package gui;

import javax.swing.SwingUtilities;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubDarkIJTheme;

public class Launcher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                FlatGitHubDarkIJTheme.setup();
                MainWindow main = new MainWindow();
                FlatGitHubDarkIJTheme.setup();
                main.show();
            }
        });
    }
}
