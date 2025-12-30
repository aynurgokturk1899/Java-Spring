package tr.mhrs.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class RandevuAramaMobil extends JFrame {
    private final Long aktifHastaId;
    private final JFrame callingFrame;
    private final String PLACEHOLDER = "Poliklinik, Hastane veya Hekim Ara...";
    
    private String aktifAramaKapsami = "KLİNİK"; 

    public RandevuAramaMobil(Long aktifHastaId, JFrame callingFrame) {
        super("Arama");
        this.aktifHastaId = aktifHastaId;
        this.callingFrame = callingFrame;

        setSize(450, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        add(createTopPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                callingFrame.setVisible(true);
            }
        });
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        JButton btnBack = new JButton("<");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 22));
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.addActionListener(e -> {
            callingFrame.setVisible(true);
            dispose();
        });

        JLabel lblTitle = new JLabel("Arama", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        header.add(btnBack, BorderLayout.WEST);
        header.add(lblTitle, BorderLayout.CENTER);

        JPanel searchBoxWrapper = new JPanel(new BorderLayout());
        searchBoxWrapper.setBackground(Color.WHITE);
        searchBoxWrapper.setBorder(new EmptyBorder(5, 15, 10, 15));

        JPanel searchBox = new JPanel(new BorderLayout(10, 0));
        searchBox.setBackground(Color.WHITE);
        searchBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        JTextField txtSearch = new JTextField(PLACEHOLDER);
        txtSearch.setBorder(null);
        txtSearch.setForeground(Color.GRAY);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        txtSearch.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtSearch.getText().equals(PLACEHOLDER)) {
                    txtSearch.setText("");
                    txtSearch.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (txtSearch.getText().isEmpty()) {
                    txtSearch.setForeground(Color.GRAY);
                    txtSearch.setText(PLACEHOLDER);
                }
            }
        });

        txtSearch.addActionListener(e -> {
            String kriter = txtSearch.getText().trim();
            if (kriter.length() >= 3 && !kriter.equals(PLACEHOLDER)) {
                new RandevuAramaSonucEkrani(kriter, aktifHastaId, RandevuAramaMobil.this).setVisible(true);
                setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen arama için en az 3 karakter giriniz.", "Uyarı", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        JLabel lblSearchIcon = new JLabel("🔍");
        searchBox.add(lblSearchIcon, BorderLayout.WEST);
        searchBox.add(txtSearch, BorderLayout.CENTER);
        searchBoxWrapper.add(searchBox);

        JPanel tabContainer = new JPanel(new GridLayout(1, 3));
        tabContainer.setBackground(Color.WHITE);
        tabContainer.setPreferredSize(new Dimension(450, 45));
        
        JButton btnKlinik = createTabButton("KLİNİK", true);
        JButton btnHastane = createTabButton("HASTANE", false);
        JButton btnHekim = createTabButton("HEKİM", false);

        btnKlinik.addActionListener(e -> {
            aktifAramaKapsami = "KLİNİK";
            updateTabStyles(btnKlinik, btnHastane, btnHekim);
        });

        btnHastane.addActionListener(e -> {
            aktifAramaKapsami = "HASTANE";
            updateTabStyles(btnHastane, btnKlinik, btnHekim);
        });

        btnHekim.addActionListener(e -> {
            aktifAramaKapsami = "HEKİM";
            updateTabStyles(btnHekim, btnKlinik, btnHastane);
        });

        tabContainer.add(btnKlinik);
        tabContainer.add(btnHastane);
        tabContainer.add(btnHekim);

        panel.add(header);
        panel.add(searchBoxWrapper);
        panel.add(tabContainer);
        return panel;
    }

    private void updateTabStyles(JButton selected, JButton other1, JButton other2) {
        selected.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, Color.decode("#20B2AA")));
        selected.setForeground(Color.BLACK);

        other1.setBorder(BorderFactory.createEmptyBorder());
        other1.setForeground(Color.GRAY);
        other2.setBorder(BorderFactory.createEmptyBorder());
        other2.setForeground(Color.GRAY);
    }

    private JButton createTabButton(String text, boolean isActive) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        if (isActive) {
            btn.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, Color.decode("#20B2AA")));
            btn.setForeground(Color.BLACK);
        } else {
            btn.setBorder(BorderFactory.createEmptyBorder());
            btn.setForeground(Color.GRAY);
        }
        return btn;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.decode("#F8F9FA"));
        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setOpaque(false);
        JLabel lblIcon = new JLabel("📅", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 50));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lblMsg = new JLabel("<html><center>Arama yapmak için arama kutusuna yazabilirsiniz</center></html>");
        lblMsg.setForeground(Color.GRAY);
        lblMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
        inner.add(lblIcon);
        inner.add(Box.createVerticalStrut(20));
        inner.add(lblMsg);
        panel.add(inner);
        return panel;
    }
}