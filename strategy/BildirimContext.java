package yonetici_app.strategy;

import java.awt.Component;
import javax.swing.JOptionPane;

public class BildirimContext {
    private Component parent;

    public BildirimContext(Component parent) {
        this.parent = parent;
    }

    public String bildirimiGonderSorarak(String ad, String iletisim, String konu, String icerik) {
        Object[] options = {"E-Posta", "SMS", "Vazgeç"};
        int secim = JOptionPane.showOptionDialog(parent,
                ad + " kullanıcısına bildirim nasıl gitsin?",
                "Gönderim Yöntemi Seçin",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        BildirimStrategy strategy;
        String tur;

        if (secim == JOptionPane.YES_OPTION) { 
            strategy = new EmailBildirim(parent);
            tur = "email";
        } else if (secim == JOptionPane.NO_OPTION) { 
            strategy = new SMSBildirim(parent);
            tur = "sms";
        } else {
            return null;
        }

        strategy.bildirimGonder(ad, iletisim, konu, icerik);
        
        JOptionPane.showMessageDialog(parent, "Sistem: Bildirim " + tur.toUpperCase() + " ile gönderildi.");
        
        return tur; 
    }
}