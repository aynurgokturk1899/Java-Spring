package tr.mhrs.view;

import tr.mhrs.model.Kullanici;
import tr.mhrs.service.KullaniciService;
import tr.mhrs.factory.DoktorMenuFactory;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DoktorGirisPanel extends AbstractGirisEkrani {

    private final JFrame callingFrame;
    private final KullaniciService kullaniciService;

    public DoktorGirisPanel(JFrame callingFrame) {
        super("Doktor Girişi");
        
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
            JOptionPane.showMessageDialog(this, "T.C. Kimlik/Sicil No ve şifre boş bırakılamaz.", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Kullanici aktifKullanici = kullaniciService.girisYap(tcKimlikNo, sifre);

        if (aktifKullanici != null) {
            if (aktifKullanici.getRolId() == 3L || aktifKullanici.getRolId() == 1L) { 
                JFrame menu = new DoktorMenuFactory().createMenu(aktifKullanici);
                menu.setVisible(true);
                JOptionPane.showMessageDialog(this, "Doktor girişi başarılı!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Bu panelden sadece doktorlar giriş yapabilir.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Giriş başarısız. Bilgilerinizi kontrol edin.", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    protected void kayitEkraninaGit() {
        SwingUtilities.invokeLater(() -> {
            new DoktorKayitEkrani().setVisible(true);
        });
        dispose();
    }
}