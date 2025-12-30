package tr.mhrs.view;

import tr.mhrs.model.Kullanici;
import tr.mhrs.service.KullaniciService;
import tr.mhrs.factory.HastaMenuFactory;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class HastaGirisPanel extends AbstractGirisEkrani {

    private final JFrame callingFrame;
    private final KullaniciService kullaniciService;

    public HastaGirisPanel(JFrame callingFrame) {
        super("Hasta Girişi");
        
        this.callingFrame = callingFrame;
        this.kullaniciService = new KullaniciService();

        addWindowListener(new WindowAdapter() {
             @Override
             public void windowClosing(WindowEvent windowEvent) {
                 if (callingFrame != null) {
                     callingFrame.setVisible(true);
                 }
             }
        });
    }

    @Override
    protected void girisYapMantigi() {
        String tcKimlikNo = txtTc.getText();
        String sifre = new String(txtSifre.getPassword());

        if (tcKimlikNo.isEmpty() || sifre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "T.C. Kimlik No ve şifre boş bırakılamaz.", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Kullanici aktifKullanici = kullaniciService.girisYap(tcKimlikNo, sifre);

        if (aktifKullanici != null) {
            if (aktifKullanici.getRolId() == 2L) { 
                JFrame menu = new HastaMenuFactory().createMenu(aktifKullanici);
                menu.setVisible(true);
                JOptionPane.showMessageDialog(this, "Hasta girişi başarılı!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Bu panelden sadece hastalar giriş yapabilir.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Giriş başarısız. T.C. veya şifre hatalı.", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    protected void kayitEkraninaGit() {
        SwingUtilities.invokeLater(() -> {
            new KayitEkrani().setVisible(true);
        });
        dispose();
    }
}