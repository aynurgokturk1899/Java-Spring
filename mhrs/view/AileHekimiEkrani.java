package tr.mhrs.view;

import tr.mhrs.model.AileHekimiBilgisi;
import tr.mhrs.service.RandevuService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AileHekimiEkrani extends JFrame {
    private final RandevuService randevuService = new RandevuService();
    private final Color BG_GRAY = new Color(245, 247, 249); 
    private final Color TEXT_COLOR = new Color(51, 51, 51);

    public AileHekimiEkrani(Long hastaId, JFrame callingFrame) {
        super("Aile Hekimi Bilgileri");
        
        setSize(450, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_GRAY);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(450, 65));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        JButton btnBack = new JButton(" < "); 
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 22));
        btnBack.setForeground(new Color(0, 122, 255)); 
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> {
            callingFrame.setVisible(true);
            dispose();
        });

        JLabel lblTitle = new JLabel("Aile Hekimi Bilgileri", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTitle.setForeground(TEXT_COLOR);

        header.add(btnBack, BorderLayout.WEST);
        header.add(lblTitle, BorderLayout.CENTER);
        header.add(Box.createRigidArea(new Dimension(50, 0)), BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(40, 30, 40, 30));

        AileHekimiBilgisi info = randevuService.getAileHekimiBilgisi(hastaId);

        if (info != null) {
            content.add(createInfoCard("👤", "HEKİM ADI SOYADI", info.getDoktorAdSoyad()));
            content.add(Box.createVerticalStrut(25));
            content.add(createInfoCard("🏥", "AİLE SAĞLIĞI MERKEZİ", info.getBirimAd()));
            content.add(Box.createVerticalStrut(25));
            content.add(createInfoCard("📍", "ADRES BİLGİSİ", 
                info.getAdres() != null ? info.getAdres() : "ADRES KAYDI BULUNMAMAKTADIR"));
            content.add(Box.createVerticalStrut(25));
            content.add(createInfoCard("📞", "İLETİŞİM NUMARASI", 
                info.getTelefon() != null ? info.getTelefon() : "NUMARA KAYDI YOK"));
        } else {
            content.add(new JLabel("<html><center>Kayıtlı aile hekimi bilgisi bulunamadı.<br>Lütfen sistem yöneticisi ile iletişime geçiniz.</center></html>"));
        }

        add(content, BorderLayout.CENTER);
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                callingFrame.setVisible(true);
            }
        });
    }

  
    private JPanel createInfoCard(String icon, String label, String value) {
        JPanel card = new JPanel(new BorderLayout(20, 0));
        card.setOpaque(false);
        card.setMaximumSize(new Dimension(400, 80));

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 32));
        lblIcon.setForeground(new Color(100, 100, 100));
        lblIcon.setPreferredSize(new Dimension(50, 50));
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel textGroup = new JPanel();
        textGroup.setLayout(new BoxLayout(textGroup, BoxLayout.Y_AXIS));
        textGroup.setOpaque(false);

        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblLabel.setForeground(new Color(150, 150, 150));

        JLabel lblValue = new JLabel("<html><body style='width: 240px;'>" + value.toUpperCase() + "</body></html>");
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblValue.setForeground(TEXT_COLOR);

        textGroup.add(lblLabel);
        textGroup.add(Box.createVerticalStrut(4));
        textGroup.add(lblValue);

        card.add(lblIcon, BorderLayout.WEST);
        card.add(textGroup, BorderLayout.CENTER);
        return card;
    }
}