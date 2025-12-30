package tr.mhrs.view;

import tr.mhrs.data.DoktorDAO;
import tr.mhrs.model.Bolum;
import tr.mhrs.model.Hastane;
import tr.mhrs.service.KullaniciService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class DoktorKayitEkrani extends JFrame {

    private final KullaniciService kullaniciService = new KullaniciService();
    private final DoktorDAO doktorDAO = new DoktorDAO();
    private final Long DOKTOR_ROL_ID = 3L; 

    private JTextField txtAd, txtSoyad, txtTcKimlikNo, txtEposta; 
    private JPasswordField txtSifre, txtSifreTekrar; 
    private JComboBox<Bolum> cmbBolum; 
    private JComboBox<Hastane> cmbHastane; 
    private JButton btnKayitOl, btnGirisEkraninaGit;
    
    public DoktorKayitEkrani() {
        super("MHRS | Doktor Kaydı");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 750); 
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel contentCard = new JPanel();
        contentCard.setLayout(new BoxLayout(contentCard, BoxLayout.Y_AXIS));
        contentCard.setBorder(new EmptyBorder(30, 30, 30, 30));
        contentCard.setBackground(Color.WHITE);
        
        JLabel headerLabel = new JLabel("Doktor Kayıt Formu", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtAd = createStyledTextField("Adınız");
        txtSoyad = createStyledTextField("Soyadınız");
        txtTcKimlikNo = createStyledTextField("T.C. Kimlik No"); 
        txtEposta = createStyledTextField("E-posta");
        
        cmbBolum = new JComboBox<>();
        cmbHastane = new JComboBox<>();
        styleComponent(cmbBolum, "Uzmanlık Alanı");
        styleComponent(cmbHastane, "Çalıştığı Hastane");
        
        loadVeritabaniVerileri();

        txtSifre = createStyledPasswordField("Şifre");
        txtSifreTekrar = createStyledPasswordField("Şifre Tekrarı"); 

        contentCard.add(headerLabel);
        contentCard.add(Box.createVerticalStrut(15));
        contentCard.add(txtAd); contentCard.add(Box.createVerticalStrut(10));
        contentCard.add(txtSoyad); contentCard.add(Box.createVerticalStrut(10));
        contentCard.add(txtTcKimlikNo); contentCard.add(Box.createVerticalStrut(10)); 
        contentCard.add(txtEposta); contentCard.add(Box.createVerticalStrut(10));
        contentCard.add(cmbBolum); contentCard.add(Box.createVerticalStrut(10));
        contentCard.add(cmbHastane); contentCard.add(Box.createVerticalStrut(10));
        contentCard.add(txtSifre); contentCard.add(Box.createVerticalStrut(10));
        contentCard.add(txtSifreTekrar); contentCard.add(Box.createVerticalStrut(20));
        
        btnKayitOl = new JButton("Hesap Oluştur");
        btnGirisEkraninaGit = new JButton("Giriş Ekranına Dön");

        btnKayitOl.addActionListener(e -> kayitOlIslemi());
        btnGirisEkraninaGit.addActionListener(e -> {
            new DoktorGirisPanel(new GirisEkrani()).setVisible(true);
            this.dispose();
        });

        contentCard.add(btnKayitOl);
        contentCard.add(Box.createVerticalStrut(10));
        contentCard.add(btnGirisEkraninaGit);

        add(contentCard);
    }

    private void loadVeritabaniVerileri() {
        try {
            List<Hastane> hastaneler = doktorDAO.tumHastaneleriGetir();
            for (Hastane h : hastaneler) cmbHastane.addItem(h);

            List<Bolum> bolumler = doktorDAO.tumBolumleriGetir();
            for (Bolum b : bolumler) cmbBolum.addItem(b);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Veriler yüklenirken hata oluştu!");
        }
    }

    private void kayitOlIslemi() {
        String ad = txtAd.getText();
        String soyad = txtSoyad.getText();
        String tc = txtTcKimlikNo.getText();
        String eposta = txtEposta.getText();
        String sifre = new String(txtSifre.getPassword());
        
        Hastane seciliHastane = (Hastane) cmbHastane.getSelectedItem();
        Bolum seciliBolum = (Bolum) cmbBolum.getSelectedItem();

        if (ad.isEmpty() || tc.isEmpty() || seciliHastane == null || seciliBolum == null) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun.");
            return;
        }

        boolean basarili = kullaniciService.doktorKayitOl(tc, sifre, eposta, ad, soyad, 
                                        seciliHastane.getHastaneId(), seciliBolum.getBolumId());

        if (basarili) {
            JOptionPane.showMessageDialog(this, "Doktor kaydı başarılı! Giriş yapabilirsiniz.");
            new DoktorGirisPanel(new GirisEkrani()).setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Kayıt sırasında hata oluştu. TC No zaten kullanımda olabilir.");
        }
    }

    private void styleComponent(JComponent c, String title) {
        c.setBorder(BorderFactory.createTitledBorder(title));
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
    }

    private JTextField createStyledTextField(String t) {
        JTextField f = new JTextField();
        styleComponent(f, t);
        return f;
    }

    private JPasswordField createStyledPasswordField(String t) {
        JPasswordField f = new JPasswordField();
        styleComponent(f, t);
        return f;
    }
}