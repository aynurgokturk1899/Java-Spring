package tr.mhrs.view;

import tr.mhrs.model.Kullanici;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ProfilEkrani extends JFrame {
    private final Kullanici aktifKullanici;
    private final JFrame callingFrame;
    private final Color BG_GRAY = new Color(240, 242, 245);
    private final Color TEXT_DARK = new Color(51, 51, 51);

    public ProfilEkrani(Kullanici kullanici, JFrame callingFrame) {
        super("Profil");
        this.aktifKullanici = kullanici;
        this.callingFrame = callingFrame;

        setSize(450, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_GRAY);

        add(createHeaderBar(), BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        content.add(createProfileButton("👤", "Kimlik Bilgileri", "Ad, Soyad, T.C. Kimlik No vb.", e -> showKimlikDetay()));
        content.add(Box.createVerticalStrut(15));

        content.add(createProfileButton("📞", "İletişim Bilgileri", "E-posta Adresi", e -> showIletisimDetay()));

        add(content, BorderLayout.CENTER);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                callingFrame.setVisible(true);
            }
        });
    }

    private JPanel createHeaderBar() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(450, 65));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        JButton btnBack = new JButton(" < ");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 22));
        btnBack.setForeground(new Color(0, 122, 255));
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.addActionListener(e -> {
            callingFrame.setVisible(true);
            dispose();
        });

        JLabel lblTitle = new JLabel("Profil", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTitle.setForeground(TEXT_DARK);

        header.add(btnBack, BorderLayout.WEST);
        header.add(lblTitle, BorderLayout.CENTER);
        header.add(Box.createRigidArea(new Dimension(50, 0)), BorderLayout.EAST);
        return header;
    }

    private JButton createProfileButton(String icon, String title, String subTitle, java.awt.event.ActionListener action) {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout(15, 0));
        btn.setBackground(Color.WHITE);
        btn.setMaximumSize(new Dimension(410, 80));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(235, 235, 235), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        
        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        JLabel lblSub = new JLabel(subTitle);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(Color.GRAY);
        textPanel.add(lblTitle);
        textPanel.add(lblSub);

        JLabel lblArrow = new JLabel(">");
        lblArrow.setForeground(Color.LIGHT_GRAY);
        lblArrow.setFont(new Font("Segoe UI", Font.BOLD, 18));

        btn.add(lblIcon, BorderLayout.WEST);
        btn.add(textPanel, BorderLayout.CENTER);
        btn.add(lblArrow, BorderLayout.EAST);
        
        btn.addActionListener(action);
        return btn;
    }


    private void showKimlikDetay() {
        new KimlikBilgileriEkrani(aktifKullanici, this).setVisible(true);
        this.setVisible(false); 
    }

    private void showIletisimDetay() {
        new IletisimBilgileriEkrani(aktifKullanici, this).setVisible(true);
        this.setVisible(false);
    }
}