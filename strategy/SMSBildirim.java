package yonetici_app.strategy;

import java.awt.Component;
import javax.swing.JOptionPane;

public class SMSBildirim implements BildirimStrategy {
    private final Component parent;

    public SMSBildirim(Component parent) {
        this.parent = parent;
    }

    @Override
    public void bildirimGonder(String aliciAd, String aliciSMS, String konu, String icerik) {
        String message = String.format("--- SMS BİLDİRİMİ GÖNDERİLİYOR ---\nAlıcı: %s\nNo: %s\nMesaj: %s\n\n(Simülasyon: GSM Operatörü bağlantısı kurulmadı.)",
                                      aliciAd, aliciSMS, icerik);
        JOptionPane.showMessageDialog(parent, message, "SMS Gönderildi", JOptionPane.INFORMATION_MESSAGE);
    }
}