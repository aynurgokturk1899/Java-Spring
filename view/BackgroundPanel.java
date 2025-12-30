package yonetici_app.view;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class BackgroundPanel extends JPanel {

    private Image background;

    public BackgroundPanel(String resourcePath) {
        URL url = getClass().getResource(resourcePath);

        if (url != null) {
            background = new ImageIcon(url).getImage();
            System.out.println("Arka plan yüklendi: " + url);
        } else {
            background = null;
            System.out.println("RESOURCE BULUNAMADI: " + resourcePath);
        }

        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
