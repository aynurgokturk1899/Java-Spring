package tr.mhrs.view;

import tr.mhrs.model.Kullanici;
import tr.mhrs.service.KullaniciService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class KayitEkrani extends JFrame {

    private final KullaniciService kullaniciService = new KullaniciService();
    private final Long HASTA_ROL_ID = 2L; 

    private JTextField txtAd, txtSoyad, txtTcKimlikNo, txtEposta; 
    private JPasswordField txtSifre, txtSifreTekrar; 
    private JButton btnKayitOl, btnGirisEkraninaGit;
    
    private final Color MHRS_BLUE_DARK = new Color(0, 77, 153);
    private final Color MHRS_GREEN = new Color(40, 167, 69);
    private final Color LIGHT_GRAY_BG = Color.decode("#E0E0E0");
    
    private final int MOBILE_WIDTH = 450;
    private final int MOBILE_HEIGHT = 750;

    public KayitEkrani() {
        super("MHRS | Yeni Kullanıcı Kaydı");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(MOBILE_WIDTH, MOBILE_HEIGHT); 
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel backgroundPanel = new JPanel(new BorderLayout()); 
        backgroundPanel.setBackground(LIGHT_GRAY_BG);

        JPanel contentCard = new JPanel();
        contentCard.setLayout(new BoxLayout(contentCard, BoxLayout.Y_AXIS));
        contentCard.setBorder(new EmptyBorder(30, 30, 30, 30));
        contentCard.setBackground(Color.WHITE);
        
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        contentCard.setMaximumSize(new Dimension(MOBILE_WIDTH - 60, MOBILE_HEIGHT - 60)); 
        centerWrapper.add(contentCard, gbc);
        backgroundPanel.add(centerWrapper, BorderLayout.CENTER);


        JLabel headerLabel = new JLabel("Hasta Kayıt Formu", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(MHRS_GREEN);
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerLabel.setBorder(new EmptyBorder(0, 0, 10, 0));


        txtAd = createStyledTextField("Adınız");
        txtSoyad = createStyledTextField("Soyadınız");
        txtTcKimlikNo = createStyledTextField("T.C. Kimlik No"); 
        txtEposta = createStyledTextField("E-posta");
        txtSifre = createStyledPasswordField("Şifre (Min. 6 Karakter)");
        txtSifreTekrar = createStyledPasswordField("Şifre Tekrarı");

        
        contentCard.add(headerLabel);
        contentCard.add(Box.createVerticalStrut(15));
        contentCard.add(txtAd); contentCard.add(Box.createVerticalStrut(10));
        contentCard.add(txtSoyad); contentCard.add(Box.createVerticalStrut(10));
        contentCard.add(txtTcKimlikNo); contentCard.add(Box.createVerticalStrut(10)); 
        contentCard.add(txtEposta); contentCard.add(Box.createVerticalStrut(10));
        contentCard.add(txtSifre); contentCard.add(Box.createVerticalStrut(10));
        contentCard.add(txtSifreTekrar); contentCard.add(Box.createVerticalStrut(20)); 
        
        
        btnKayitOl = new JButton("Hesap Oluştur");
        btnGirisEkraninaGit = new JButton("Giriş Ekranına Dön");

        btnKayitOl.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnGirisEkraninaGit.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnKayitOl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        
        styleModernButton(btnKayitOl, MHRS_GREEN, 18);
        styleLinkButton(btnGirisEkraninaGit, MHRS_BLUE_DARK);

        contentCard.add(btnKayitOl);
        contentCard.add(Box.createVerticalStrut(15));
        contentCard.add(btnGirisEkraninaGit);
        contentCard.add(Box.createVerticalGlue());

        add(backgroundPanel, BorderLayout.CENTER);

        btnKayitOl.addActionListener(e -> kayitOlIslemi());
        
        btnGirisEkraninaGit.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new GirisEkrani().setVisible(true));
            this.dispose(); 
        });

        setVisible(true);
    }
    
    
    private JTextField createStyledTextField(String title) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), 
            title
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        return field;
    }
    
    private JPasswordField createStyledPasswordField(String title) {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), 
            title
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        return field;
    }

    private void styleModernButton(JButton button, Color baseColor, int fontSize) {
        button.setBackground(baseColor); 
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(baseColor.darker(), 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
    }
    
    private void styleLinkButton(JButton button, Color color) {
        button.setBackground(Color.WHITE);
        button.setForeground(color);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }


    private void kayitOlIslemi() {
        String ad = txtAd.getText();
        String soyad = txtSoyad.getText();
        
        String tcKimlikNo = txtTcKimlikNo.getText(); 
        String eposta = txtEposta.getText();
        String sifre = new String(txtSifre.getPassword());
        String sifreTekrar = new String(txtSifreTekrar.getPassword()); 

        if (tcKimlikNo.length() != 11) {
        JOptionPane.showMessageDialog(this, "T.C. Kimlik No 11 haneli olmalıdır.", "Uyarı", JOptionPane.WARNING_MESSAGE);
        return;
    }
        if (!tcKimlikNo.matches("\\d+")) {
        JOptionPane.showMessageDialog(this, "T.C. Kimlik No sadece rakamlardan oluşabilir.", "Uyarı", JOptionPane.WARNING_MESSAGE);
        return;
    }
        if (ad.isEmpty() || soyad.isEmpty() || tcKimlikNo.isEmpty() || eposta.isEmpty() || sifre.isEmpty() || sifreTekrar.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tüm alanları doldurmanız gerekmektedir.", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!sifre.equals(sifreTekrar)) {
            JOptionPane.showMessageDialog(this, "Şifreler uyuşmuyor. Lütfen tekrar kontrol edin.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
      
        boolean basarili = kullaniciService.kayitOl(tcKimlikNo, sifre, eposta, ad, soyad, HASTA_ROL_ID); 

        if (basarili) {
            JOptionPane.showMessageDialog(this, "Kayıt başarılı! Giriş yapabilirsiniz.", "Başarı", JOptionPane.INFORMATION_MESSAGE);
            
            SwingUtilities.invokeLater(() -> new GirisEkrani().setVisible(true));
            this.dispose(); 
        } else {
            JOptionPane.showMessageDialog(this, "Kayıt başarısız oldu. T.C. Kimlik No mevcut veya bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
}