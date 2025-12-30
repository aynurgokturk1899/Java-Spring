package yonetici_app.strategy;

import java.awt.Component;
import javax.swing.JOptionPane;

public class EmailBildirim implements BildirimStrategy {
    private final Component parent;

    public EmailBildirim(Component parent) {
        this.parent = parent;
    }

    @Override
    public void bildirimGonder(String aliciAd, String aliciEmail, String konu, String icerik) {
        String message = String.format("--- E-POSTA BİLDİRİMİ GÖNDERİLİYOR ---\nAlıcı: %s <%s>\nKonu: %s\nİçerik: %s)",
                                      aliciAd, aliciEmail, konu, icerik);
        JOptionPane.showMessageDialog(parent, message, "Bildirim Başarılı", JOptionPane.INFORMATION_MESSAGE);
    }
}