package yonetici_app.view;

import yonetici_app.data.YoneticiDAO;
import yonetici_app.model.DoktorMusaitlik;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoktorRandevuEkrani extends JFrame {

    private final YoneticiDAO yoneticiDAO = new YoneticiDAO();

    private final Long doktorId;
    private final String doktorBilgi;
    private final LocalDate tarih;

    private DoktorMusaitlik musaitlik;
    private List<String[]> randevular;

    private JPanel grid;
    private JLabel lblSaat, lblDurum, lblHasta, lblTc, lblEmail;
    private JButton btnIptal;

    private Long seciliRandevuId = null;

    private Point mouseOffset;

    public DoktorRandevuEkrani(Long doktorId, String doktorBilgi, LocalDate tarih)
            throws SQLException {

        setUndecorated(true);

        this.doktorId = doktorId;
        this.doktorBilgi = doktorBilgi;
        this.tarih = tarih;

        this.musaitlik = yoneticiDAO.getMesaiBilgisi(doktorId, tarih);
        this.randevular = yoneticiDAO.getDoktorGunlukPlan(doktorId, tarih);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        BackgroundPanel bg =
                new BackgroundPanel("/resources/images/arka_plan_hastane.png");
        bg.setLayout(new GridBagLayout());

        JPanel kart = new JPanel(new BorderLayout(20, 20)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(0, 0, 0, 60));
                g2.fillRoundRect(8, 8, getWidth() - 16, getHeight() - 16, 30, 30);

                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 16, getHeight() - 16, 30, 30);
            }
        };
        kart.setOpaque(false);
        kart.setPreferredSize(new Dimension(1050, 680));
        kart.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel baslik = new JLabel(
                "RANDEVU ÇİZELGESİ - " + doktorBilgi + " | " + tarih
        );
        baslik.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JButton btnClose = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(230, 230, 230));
                g2.fillOval(0, 0, getWidth(), getHeight());

                g2.setColor(Color.DARK_GRAY);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth("✕")) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 3;
                g2.drawString("✕", x, y);
            }
        };

        btnClose.setPreferredSize(new Dimension(32, 32));
        btnClose.setBorderPainted(false);
        btnClose.setContentAreaFilled(false);
        btnClose.setFocusPainted(false);
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());

        header.add(baslik, BorderLayout.WEST);
        header.add(btnClose, BorderLayout.EAST);

        header.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mouseOffset = e.getPoint();
            }
        });
        header.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point p = e.getLocationOnScreen();
                setLocation(p.x - mouseOffset.x, p.y - mouseOffset.y);
            }
        });

        kart.add(header, BorderLayout.NORTH);

        grid = new JPanel(new GridLayout(0, 5, 15, 15));
        grid.setOpaque(false);
        kart.add(grid, BorderLayout.CENTER);

        kart.add(detayPanel(), BorderLayout.SOUTH);

        bg.add(kart);
        setContentPane(bg);

        slotlariYukle();
        setVisible(true);
    }

    private JPanel detayPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setOpaque(false);

        JPanel bilgi = new JPanel(new GridLayout(0, 1, 5, 5));
        bilgi.setBackground(new Color(245, 247, 250));
        bilgi.setBorder(new EmptyBorder(15, 15, 15, 15));

        lblSaat = new JLabel("Saat: -");
        lblDurum = new JLabel("Durum: -");
        lblHasta = new JLabel("Hasta: -");
        lblTc = new JLabel("TC: -");
        lblEmail = new JLabel("E-posta: -");

        bilgi.add(lblSaat);
        bilgi.add(lblDurum);
        bilgi.add(lblHasta);
        bilgi.add(lblTc);
        bilgi.add(lblEmail);

        btnIptal = new JButton("Randevuyu İptal Et");
        btnIptal.setBackground(new Color(231, 76, 60));
        btnIptal.setForeground(Color.WHITE);
        btnIptal.setFocusPainted(false);
        btnIptal.setEnabled(false);
        btnIptal.addActionListener(e -> randevuIptal());

        panel.add(bilgi, BorderLayout.CENTER);
        panel.add(btnIptal, BorderLayout.EAST);
        return panel;
    }

    private void slotlariYukle() {
        grid.removeAll();
        seciliRandevuId = null;
        btnIptal.setEnabled(false);

        if (musaitlik == null) {
            grid.add(new JLabel("Bu gün için mesai tanımı yok"));
            return;
        }

        Map<LocalTime, String[]> map = new HashMap<>();
        for (String[] r : randevular) {
            LocalTime t = LocalTime.parse(r[1].length() == 5 ? r[1] + ":00" : r[1]);
            map.put(t, r);
        }

        for (LocalTime t = musaitlik.getBaslangicSaati();
             t.isBefore(musaitlik.getBitisSaati());
             t = t.plusMinutes(musaitlik.getRandevuSuresiDk())) {

            String[] r = map.get(t);
            JButton b;

            if (r == null) {
                b = createSlotButton(t.toString().substring(0, 5), false);
                b.setEnabled(false);
            } else {
                b = createSlotButton(t.toString().substring(0, 5), true);
                b.addActionListener(e -> randevuSec(r));
            }
            grid.add(b);
        }

        grid.revalidate();
        grid.repaint();
    }

    private JButton createSlotButton(String saat, boolean dolu) {
        JButton btn = new JButton(
                "<html><center><b>" + saat + "</b><br>" +
                        (dolu ? "DOLU" : "BOŞ") + "</center></html>"
        );

        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setPreferredSize(new Dimension(150, 90));

        if (dolu) {
            btn.setBackground(new Color(231, 76, 60));
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(new Color(236, 240, 241));
            btn.setForeground(Color.DARK_GRAY);
        }

        btn.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        return btn;
    }

    private void randevuSec(String[] r) {
        seciliRandevuId = Long.parseLong(r[0]);
        lblSaat.setText("Saat: " + r[1].substring(0, 5));
        lblDurum.setText("Durum: " + r[2]);
        lblHasta.setText("Hasta: " + r[3]);
        lblTc.setText("TC: " + r[4]);
        lblEmail.setText("E-posta: " + r[5]);
        btnIptal.setEnabled(!"iptal".equalsIgnoreCase(r[2]));
    }

    private void randevuIptal() {
        if (seciliRandevuId == null) return;
        try {
            yoneticiDAO.iptalEtRandevu(seciliRandevuId);
            musaitlik = yoneticiDAO.getMesaiBilgisi(doktorId, tarih);
            randevular = yoneticiDAO.getDoktorGunlukPlan(doktorId, tarih);
            slotlariYukle();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}
