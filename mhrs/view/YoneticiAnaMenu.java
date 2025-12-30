package tr.mhrs.view;

import tr.mhrs.model.Kullanici;
import tr.mhrs.model.RandevuDetay;
import tr.mhrs.service.RandevuService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class YoneticiAnaMenu extends JFrame {
    private final Kullanici yonetici;
    private final RandevuService randevuService = new RandevuService();
    private JPanel listPanel;

    public YoneticiAnaMenu(Kullanici yonetici) throws SQLException {
        super("MHRS | Yönetici Paneli");
        this.yonetici = yonetici;
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(220, 53, 69)); 
        header.setPreferredSize(new Dimension(500, 80));
        JLabel lblTitle = new JLabel("Yönetim Paneli", SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.add(lblTitle, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);


        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        
        JScrollPane scrollPane = new JScrollPane(listPanel);
        add(scrollPane, BorderLayout.CENTER);
       
        verileriYukle(1L);        
        JButton btnCikis = new JButton("Çıkış Yap");
        btnCikis.addActionListener(e -> {
            new GirisEkrani().setVisible(true);
            dispose();
        });
        add(btnCikis, BorderLayout.SOUTH);
    }

    private void verileriYukle(Long demoHastaId) throws SQLException {
        listPanel.removeAll();
        List<RandevuDetay> randevular = randevuService.getYaklasanRandevular(demoHastaId);
        
        if (randevular.isEmpty()) {
            listPanel.add(new JLabel("Aktif randevu bulunamadı."));
        } else {
            for (RandevuDetay r : randevular) {
                listPanel.add(createYoneticiRandevuKarti(r));
                listPanel.add(Box.createVerticalStrut(10));
            }
        }
        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel createYoneticiRandevuKarti(RandevuDetay r) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(450, 100));
        
        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setOpaque(false);
        info.add(new JLabel("Dr. " + r.getDoktorAdSoyad() + " - " + r.getBolumAd()));
        info.add(new JLabel("Hasta ID: " + r.getHastaId() + " | Tarih: " + r.getRandevuSaati()));
        
        JButton btnIptal = new JButton("İPTAL ET");
        btnIptal.setBackground(Color.RED);
        btnIptal.setForeground(Color.WHITE);
        
        btnIptal.addActionListener(e -> {
            String sebep = JOptionPane.showInputDialog(this, "İptal Sebebi Giriniz:");
            if (sebep != null && !sebep.trim().isEmpty()) {
                boolean sonuc = randevuService.yoneticiRandevuIptal(r.getRandevuId(), sebep);
                if (sonuc) {
                    JOptionPane.showMessageDialog(this, "Randevu iptal edildi ve taraflara bildirim gönderildi.");
                    try {
                        verileriYukle(r.getHastaId()); 
                    } catch (SQLException ex) {
                        Logger.getLogger(YoneticiAnaMenu.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        
        card.add(info, BorderLayout.CENTER);
        card.add(btnIptal, BorderLayout.EAST);
        return card;
    }
}