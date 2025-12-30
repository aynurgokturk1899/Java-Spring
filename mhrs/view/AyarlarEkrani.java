package tr.mhrs.view;

import tr.mhrs.data.AyarlarDAO;
import tr.mhrs.model.Kullanici;
import tr.mhrs.model.KullaniciAyarlari;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AyarlarEkrani extends JPanel {
    private final Kullanici aktifKullanici;
    private final AyarlarDAO ayarlarDAO = new AyarlarDAO();
    private KullaniciAyarlari mevcutAyarlar;
    
    private JComboBox<String> cmbTema, cmbDil, cmbBildirim;
    
    private final Color PRIMARY_BLUE = Color.decode("#007BFF");
    private final Color DANGER_RED = Color.decode("#DC3545");
    private final Color BG_GRAY = Color.decode("#E0E0E0");

    public AyarlarEkrani(Kullanici kullanici) {
        this.aktifKullanici = kullanici;
        setLayout(new BorderLayout());
        setBackground(BG_GRAY);

        try {
            mevcutAyarlar = ayarlarDAO.ayarlariGetir(aktifKullanici.getKullaniciId());
        } catch (Exception e) {
            mevcutAyarlar = new KullaniciAyarlari(aktifKullanici.getKullaniciId(), "Açık", "Türkçe", "Hepsi");
        }

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(20, 20, 20, 20));
        content.setOpaque(false);

        addSectionHeader(content, "Uygulama Ayarları");
        cmbTema = createRow(content, "🌓 Tema Seçimi", new String[]{"Açık", "Koyu", "Sistem"}, mevcutAyarlar.getTema());
        cmbDil = createRow(content, "🌐 Uygulama Dili", new String[]{"Türkçe", "English", "Deutsch"}, mevcutAyarlar.getDil());
        
        content.add(Box.createVerticalStrut(20));

        addSectionHeader(content, "Bildirim Tercihleri");
        cmbBildirim = createRow(content, "🔔 Bildirimler", new String[]{"Hepsi", "Sadece Randevu", "Kapalı"}, mevcutAyarlar.getBildirimTercihi());

        content.add(Box.createVerticalStrut(40));

        JButton btnSave = createModernButton("Ayarları Kaydet", PRIMARY_BLUE);
        btnSave.addActionListener(e -> saveAction());
        
        JButton btnLogout = createModernButton("Güvenli Çıkış", DANGER_RED);
        btnLogout.addActionListener(e -> logoutAction());

        content.add(btnSave);
        content.add(Box.createVerticalStrut(15));
        content.add(btnLogout);
        
        add(new JScrollPane(content), BorderLayout.CENTER);
    }

    private void addSectionHeader(JPanel p, String title) {
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(Color.DARK_GRAY);
        lbl.setBorder(new EmptyBorder(10, 5, 10, 0));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(lbl);
    }

    private JComboBox<String> createRow(JPanel p, String title, String[] opts, String current) {
        JPanel row = new JPanel(new BorderLayout());
        row.setMaximumSize(new Dimension(450, 55));
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JComboBox<String> cb = new JComboBox<>(opts);
        cb.setSelectedItem(current);
        cb.setPreferredSize(new Dimension(150, 30));

        row.add(lblTitle, BorderLayout.WEST);
        row.add(cb, BorderLayout.EAST);
        
        p.add(row);
        p.add(Box.createVerticalStrut(5));
        return cb;
    }

    private JButton createModernButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(450, 50));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return btn;
    }

    private void logoutAction() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Oturumu kapatmak istediğinize emin misiniz?", 
            "Çıkış Onayı", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            Window ancestor = SwingUtilities.getWindowAncestor(this);
            if (ancestor != null) {
                ancestor.dispose();
                new GirisEkrani().setVisible(true);
            }
        }
    }

    private void saveAction() {
        mevcutAyarlar.setTema((String) cmbTema.getSelectedItem());
        mevcutAyarlar.setDil((String) cmbDil.getSelectedItem());
        mevcutAyarlar.setBildirimTercihi((String) cmbBildirim.getSelectedItem());

        try {
            if (ayarlarDAO.ayarlariKaydet(mevcutAyarlar)) {
                JOptionPane.showMessageDialog(this, "Ayarlar Başarıyla Kaydedildi.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Hata: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
}