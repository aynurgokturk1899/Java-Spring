package tr.mhrs.view;

import tr.mhrs.model.Kullanici;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class KimlikBilgileriEkrani extends JFrame {
    public KimlikBilgileriEkrani(Kullanici k, JFrame callingFrame) {
        setTitle("Kimlik Bilgileri");
        setSize(450, 750);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(240, 242, 245));
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JButton btnBack = new JButton("<");
        btnBack.addActionListener(e -> { callingFrame.setVisible(true); dispose(); });
        header.add(btnBack, BorderLayout.WEST);
        JLabel lblTitle = new JLabel("Kimlik Bilgileri", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        header.add(lblTitle, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblSec = new JLabel("KİŞİSEL BİLGİLER");
        lblSec.setFont(new Font("Segoe UI", Font.BOLD, 16));
        card.add(lblSec);
        card.add(Box.createVerticalStrut(15));

        card.add(createRow("Ad", "SAKİNE")); 
        card.add(createRow("Soyad", "ALPAY"));
        card.add(createRow("Cinsiyet", "Kadın"));
        card.add(createRow("Doğum Tarihi", "01.01.1990"));

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrapper.setOpaque(false);
        wrapper.add(card);
        add(wrapper, BorderLayout.CENTER);
    }

    private JPanel createRow(String label, String value) {
        JPanel p = new JPanel(new BorderLayout());
        p.setPreferredSize(new Dimension(380, 45));
        p.setBackground(Color.WHITE);
        p.add(new JLabel("<html><font color='gray'>" + label + "</font></html>"), BorderLayout.WEST);
        JLabel lblVal = new JLabel(value);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        p.add(lblVal, BorderLayout.EAST);
        p.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        return p;
    }
}