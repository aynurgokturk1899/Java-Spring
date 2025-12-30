package tr.mhrs.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public abstract class AbstractGirisEkrani extends JFrame {
    protected JTextField txtTc;
    protected JPasswordField txtSifre;
    protected JButton btnGiris;
    
    protected final Color MHRS_BLUE_DARK = new Color(0, 77, 153);
    protected final Color MHRS_GREEN = new Color(40, 167, 69);
    protected final Color LIGHT_GRAY_BG = Color.decode("#E0E0E0");

    public AbstractGirisEkrani(String baslik) {
        setTitle("MHRS | " + baslik);
        setSize(450, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel backgroundPanel = new JPanel(new BorderLayout());
        backgroundPanel.setBackground(LIGHT_GRAY_BG);

        JPanel contentCard = new JPanel();
        contentCard.setLayout(new BoxLayout(contentCard, BoxLayout.Y_AXIS));
        contentCard.setBorder(new EmptyBorder(50, 40, 40, 40));
        contentCard.setBackground(Color.WHITE);
        contentCard.setOpaque(true);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        contentCard.setMaximumSize(new Dimension(370, 650));
        centerWrapper.add(contentCard, gbc);
        backgroundPanel.add(centerWrapper, BorderLayout.CENTER);

        JLabel lblTitle = new JLabel(baslik, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(baslik.contains("Hasta") ? MHRS_GREEN.darker() : MHRS_BLUE_DARK);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setBorder(new EmptyBorder(10, 0, 30, 0));

        txtTc = createStyledField("T.C. Kimlik No", false);
        txtSifre = (JPasswordField) createStyledField("Şifre", true);

        btnGiris = new JButton("Giriş Yap");
        btnGiris.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnGiris.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        styleButton(btnGiris, baslik.contains("Hasta") ? MHRS_GREEN : MHRS_BLUE_DARK);

        JLabel lblKayit = new JLabel("Hesabın yok mu? Kayıt Ol", SwingConstants.CENTER);
        lblKayit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblKayit.setForeground(MHRS_BLUE_DARK);
        lblKayit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblKayit.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblKayit.setBorder(new EmptyBorder(20, 0, 0, 0));

        btnGiris.addActionListener(e -> girisYapMantigi());
        lblKayit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) { kayitEkraninaGit(); }
        });

        contentCard.add(lblTitle);
        contentCard.add(Box.createVerticalStrut(20));
        contentCard.add(txtTc);
        contentCard.add(Box.createVerticalStrut(20));
        contentCard.add(txtSifre);
        contentCard.add(Box.createVerticalStrut(30));
        contentCard.add(btnGiris);
        contentCard.add(Box.createVerticalStrut(20));
        contentCard.add(lblKayit);
        contentCard.add(Box.createVerticalGlue());

        add(backgroundPanel, BorderLayout.CENTER);
    }

    private JTextField createStyledField(String title, boolean isPassword) {
        JTextField field = isPassword ? new JPasswordField() : new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), title));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        return field;
    }

    private void styleButton(JButton btn, Color baseColor) {
        btn.setBackground(baseColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(baseColor.darker(), 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
    }

    protected abstract void girisYapMantigi();
    protected abstract void kayitEkraninaGit();
}