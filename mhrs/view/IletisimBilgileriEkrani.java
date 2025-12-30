package tr.mhrs.view;

import tr.mhrs.model.Kullanici;
import tr.mhrs.data.KullaniciDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class IletisimBilgileriEkrani extends JFrame {
    private final KullaniciDAO dao = new KullaniciDAO();
    private final Color BUTTON_COLOR = new Color(243, 226, 210); 
    private final Color BG_GRAY = new Color(240, 242, 245);

    public IletisimBilgileriEkrani(Kullanici k, JFrame callingFrame) {
        setTitle("İletişim Bilgileri");
        setSize(450, 750);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_GRAY);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_GRAY);
        header.setPreferredSize(new Dimension(450, 60));
        JButton btnBack = new JButton("<");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btnBack.addActionListener(e -> { 
            callingFrame.setVisible(true); 
            dispose(); 
        });
        header.add(btnBack, BorderLayout.WEST);
        
        JLabel lblTitle = new JLabel("İletişim Bilgileri", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        header.add(lblTitle, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(10, 15, 10, 15));

        JTextField fieldEposta = new JTextField(k.getEposta());
        mainContent.add(createCard("E-Posta Bilgileri", 
                new String[]{"E-Posta Adresi:", "E-Posta Tipi:", "Durum:"}, 
                new JComponent[]{fieldEposta, new JLabel("Kendi E-Postası"), new JLabel("Onaysız")}, 
                "E-Posta Bilgileri Düzenle", k));

        mainContent.add(Box.createVerticalStrut(20));

        JTextField fieldTel = new JTextField(k.getTcKimlikNo()); 
        mainContent.add(createCard("Telefon Bilgileri", 
                new String[]{"Telefon Numarası:", "Telefon Tipi:", "Durum:"}, 
                new JComponent[]{fieldTel, new JLabel("e-Devlet Cep Telefonu"), new JLabel("Onaylı")}, 
                "Telefon Bilgileri Düzenle", k));

        add(new JScrollPane(mainContent), BorderLayout.CENTER);
    }

    private JPanel createCard(String title, String[] labels, JComponent[] values, String btnText, Kullanici k) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        card.add(lblTitle);
        card.add(Box.createVerticalStrut(10));

        for (int i = 0; i < labels.length; i++) {
            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false);
            row.add(new JLabel(labels[i]), BorderLayout.WEST);
            values[i].setFont(new Font("Segoe UI", Font.PLAIN, 14));
            row.add(values[i], BorderLayout.EAST);
            card.add(row);
            card.add(new JSeparator());
            card.add(Box.createVerticalStrut(5));
        }

        JButton btn = new JButton(btnText);
        btn.setBackground(BUTTON_COLOR);
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setPreferredSize(new Dimension(380, 45));
        btn.setMaximumSize(new Dimension(380, 45));

        btn.addActionListener(e -> {
            try {
               
                String guncelEposta = k.getEposta();
                String guncelTel = k.getTcKimlikNo(); 

                if (title.contains("E-Posta")) {
                    guncelEposta = ((JTextField) values[0]).getText();
                } else if (title.contains("Telefon")) {
                    guncelTel = ((JTextField) values[0]).getText();
                }

                boolean basarili = dao.iletisimGuncelle(k.getKullaniciId(), guncelEposta, guncelTel);

                if (basarili) {
                    k.setEposta(guncelEposta);
                    
                    JOptionPane.showMessageDialog(this, title + " başarıyla güncellendi!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Güncelleme yapılamadı. ID kontrolü yapın.", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage(), "Veritabanı Hatası", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        card.add(Box.createVerticalStrut(10));
        card.add(btn);

        return card;
    }
}