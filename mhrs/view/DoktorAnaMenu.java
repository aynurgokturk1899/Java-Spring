package tr.mhrs.view;

import tr.mhrs.model.Kullanici;
import tr.mhrs.model.RandevuDetay;
import tr.mhrs.model.Bildirim;
import tr.mhrs.service.RandevuService;
import tr.mhrs.service.BildirimService;
import tr.mhrs.data.DoktorDAO; 

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import tr.mhrs.template.AktifRandevuListeleme;
import tr.mhrs.template.IptalRandevuListeleme;
import tr.mhrs.template.RandevuListelemeTemplate;
import tr.mhrs.template.TamamlandiRandevuListeleme;
import tr.mhrs.template.TumuRandevuListeleme;

public class DoktorAnaMenu extends JFrame {

    private final Kullanici aktifDoktor;
    private final RandevuService randevuService = new RandevuService();
    private final BildirimService bildirimService = new BildirimService();
    private final DoktorDAO doktorDAO = new DoktorDAO(); 
    
    private JPanel mainContainer; 
    private JPanel appointmentsPanel; 
    private JPanel randevuListPanel; 
    private CardLayout cardLayout;
    
    private JButton btnBildirim;
    private Timer autoRefreshTimer;
    
    private String currentFilter = "TÜMÜ";
    private List<RandevuDetay> tumRandevular; 

    private final Color THEME_NAVY = new Color(30, 25, 100); 
    private final Color CARD_BORDER = new Color(0, 120, 215); 
    private final Color BTN_RED = new Color(230, 50, 50); 
    private final Color BTN_GREEN = new Color(40, 167, 69);
    private final Color BG_LIGHT = new Color(245, 245, 245);
    private final Color ACTIVE_TAB_COLOR = new Color(0, 120, 215);
    private final Color INACTIVE_TAB_COLOR = Color.GRAY;

    public DoktorAnaMenu(Kullanici aktifDoktor) {
        super("MHRS | Doktor Paneli");
        this.aktifDoktor = aktifDoktor;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 750); 
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createHeader(), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        mainContainer.setBackground(BG_LIGHT);

        createAppointmentsView();
        mainContainer.add(appointmentsPanel, "RANDEVULAR");

        mainContainer.add(new DoktorAyarlarEkrani(aktifDoktor), "AYARLAR");

        add(mainContainer, BorderLayout.CENTER);

        add(createBottomNav(), BorderLayout.SOUTH);

        fetchData();
        updateBildirimCount();
        
        startAutoRefresh();
        
        setVisible(true);
    }

    private void createAppointmentsView() {
        appointmentsPanel = new JPanel(new BorderLayout());
        appointmentsPanel.setBackground(BG_LIGHT);
        
        appointmentsPanel.add(createFilterPanel(), BorderLayout.NORTH);

        randevuListPanel = new JPanel();
        randevuListPanel.setLayout(new BoxLayout(randevuListPanel, BoxLayout.Y_AXIS));
        randevuListPanel.setBackground(Color.WHITE);
        randevuListPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(randevuListPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        appointmentsPanel.add(scrollPane, BorderLayout.CENTER);
    }
    
    private void startAutoRefresh() {
        autoRefreshTimer = new Timer(10000, e -> {
            if (!this.isActive()) return; 
            fetchData();
            updateBildirimCount();
        });
        autoRefreshTimer.start();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(THEME_NAVY);
        header.setPreferredSize(new Dimension(450, 80)); 
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        String doktorAdSoyad = doktorDAO.getDoktorAdSoyad(aktifDoktor.getKullaniciId());
        String ekranIsmi = (doktorAdSoyad != null) ? doktorAdSoyad : "Dr. " + aktifDoktor.getTcKimlikNo();

        JLabel lblWelcome = new JLabel("Uzm. Dr. " + ekranIsmi);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 18)); 
        lblWelcome.setForeground(Color.WHITE);

        JPanel rightIcons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightIcons.setOpaque(false);

        JButton btnRefresh = new JButton("↻");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 24));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setBorderPainted(false);
        btnRefresh.setContentAreaFilled(false);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.setToolTipText("Listeyi Yenile");
        btnRefresh.addActionListener(e -> {
            fetchData();
            updateBildirimCount();
            JOptionPane.showMessageDialog(this, "Veriler güncellendi.");
        });

        btnBildirim = new JButton("🔔");
        btnBildirim.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        btnBildirim.setForeground(Color.WHITE);
        btnBildirim.setBorderPainted(false);
        btnBildirim.setContentAreaFilled(false);
        btnBildirim.setFocusPainted(false);
        btnBildirim.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBildirim.addActionListener(e -> showBildirimEkrani());

        rightIcons.add(btnRefresh);
        rightIcons.add(btnBildirim);

        header.add(lblWelcome, BorderLayout.WEST);
        header.add(rightIcons, BorderLayout.EAST);
        return header;
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 5, 0));
        panel.setBackground(BG_LIGHT);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new Dimension(450, 60));

        JButton btnTumu = createFilterButton("Tümü", "TÜMÜ");
        JButton btnAktif = createFilterButton("Aktif", "AKTIF");
        JButton btnTamam = createFilterButton("Tamam", "TAMAMLANDI");
        JButton btnIptal = createFilterButton("İptal", "IPTAL");

        panel.add(btnTumu);
        panel.add(btnAktif);
        panel.add(btnTamam);
        panel.add(btnIptal);

        return panel;
    }

    private JButton createFilterButton(String text, String filterKey) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        btn.setForeground(INACTIVE_TAB_COLOR);
        btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        btn.addActionListener(e -> {
            Container parent = btn.getParent();
            for (Component c : parent.getComponents()) {
                if (c instanceof JButton) {
                    c.setForeground(INACTIVE_TAB_COLOR);
                    c.setBackground(Color.WHITE);
                }
            }
            btn.setForeground(Color.WHITE);
            btn.setBackground(ACTIVE_TAB_COLOR);
            currentFilter = filterKey;
            
            cardLayout.show(mainContainer, "RANDEVULAR");
            fetchData();
        });
        
        if (filterKey.equals("TÜMÜ")) {
            btn.setForeground(Color.WHITE);
            btn.setBackground(ACTIVE_TAB_COLOR);
        }
        return btn;
    }

    private void fetchData() {
        tumRandevular = randevuService.getDoktorYaklasanRandevular(aktifDoktor.getKullaniciId());
        renderList();
    }

    private void renderList() {
        RandevuListelemeTemplate template;
        switch (currentFilter) {
            case "AKTIF": template = new AktifRandevuListeleme(); break;
            case "IPTAL": template = new IptalRandevuListeleme(); break;
            case "TAMAMLANDI": template = new TamamlandiRandevuListeleme(); break;
            default: template = new TumuRandevuListeleme(); break;
        }
        template.listele(tumRandevular, randevuListPanel, this::createAppointmentCard);
    }

    private JPanel createAppointmentCard(RandevuDetay r) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(400, 150)); 
        card.setPreferredSize(new Dimension(380, 150));
        
        String durum = r.getDurum().toLowerCase();
        Color borderColor = CARD_BORDER;
        String statusText = "DURUM: AKTİF (PLANLANDI)";
        Color statusColor = BTN_GREEN;

        if (durum.equals("iptal")) {
            borderColor = BTN_RED;
            statusText = "DURUM: İPTAL EDİLDİ";
            statusColor = BTN_RED;
        } else if (durum.equals("tamamlandi")) {
            borderColor = Color.GRAY;
            statusText = "DURUM: TAMAMLANDI";
            statusColor = Color.GRAY;
        }
        
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 5, 0, 0, borderColor), 
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            )
        ));

        JPanel centerInfo = new JPanel(new GridLayout(3, 1)); 
        centerInfo.setOpaque(false);
        
        JLabel name = new JLabel("Hasta: " + r.getDoktorAdSoyad()); 
        name.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        JLabel time = new JLabel("Tarih: " + r.getRandevuSaati().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        time.setForeground(Color.DARK_GRAY);
        
        JLabel statusLabel = new JLabel(statusText);
        statusLabel.setForeground(statusColor);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        centerInfo.add(name);
        centerInfo.add(time);
        centerInfo.add(statusLabel);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        btnPanel.setOpaque(false);
        
        if (durum.equals("planlandi")) {
            JButton btnIptal = new JButton("İptal Et");
            styleActionBtn(btnIptal, BTN_RED);
            btnIptal.addActionListener(e -> {
                try {
                    handleIptal(r);
                } catch (SQLException ex) {
                    Logger.getLogger(DoktorAnaMenu.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            
            JButton btnTamamla = new JButton("Tamamla");
            styleActionBtn(btnTamamla, BTN_GREEN); 
            btnTamamla.addActionListener(e -> handleTamamla(r));

            btnPanel.add(btnIptal);
            btnPanel.add(btnTamamla);
        } 
        
        JButton btnTahlil = new JButton("Tahliller");
        styleActionBtn(btnTahlil, new Color(0, 123, 255)); 
        btnTahlil.addActionListener(e -> showHastaTahlilDialog(r.getHastaId(), "Hasta Tahlil Sonuçları"));
        btnPanel.add(btnTahlil);

        card.add(centerInfo, BorderLayout.CENTER);
        card.add(btnPanel, BorderLayout.SOUTH);
        return card;
    }

    private void handleTamamla(RandevuDetay r) {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Randevuyu tamamlandı olarak işaretlemek istiyor musunuz?", 
            "Muayene Tamamla", JOptionPane.YES_NO_OPTION);
            
        if(confirm == JOptionPane.YES_OPTION) {
            boolean success = randevuService.doktorRandevuTamamla(r.getRandevuId());
            if (success) {
                JOptionPane.showMessageDialog(this, "Muayene tamamlandı.");
                fetchData(); 
            } else {
                JOptionPane.showMessageDialog(this, "İşlem başarısız.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleIptal(RandevuDetay r) throws SQLException {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Randevuyu iptal etmek istiyor musunuz? (Hastaya bildirim gönderilecek)", 
            "İptal Onayı", JOptionPane.YES_NO_OPTION);
            
        if(confirm == JOptionPane.YES_OPTION) {
            boolean success = randevuService.doktorRandevuIptal(r.getRandevuId());
            if (success) {
                JOptionPane.showMessageDialog(this, "Randevu iptal edildi.");
                fetchData(); 
            } else {
                JOptionPane.showMessageDialog(this, "İşlem başarısız.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showHastaTahlilDialog(Long hastaId, String title) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setSize(450, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        TahlilSonuclariEkrani tahlilPanel = new TahlilSonuclariEkrani(hastaId);
        dialog.add(tahlilPanel, BorderLayout.CENTER);
        
        JButton btnKapat = new JButton("Kapat");
        btnKapat.addActionListener(e -> dialog.dispose());
        dialog.add(btnKapat, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }

    private void styleActionBtn(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void updateBildirimCount() {
        int count = bildirimService.getOkunmamisBildirimSayisi(aktifDoktor.getKullaniciId());
        if (count > 0) {
            btnBildirim.setText("🔔 (" + count + ")");
            btnBildirim.setForeground(Color.YELLOW);
        } else {
            btnBildirim.setText("🔔");
            btnBildirim.setForeground(Color.WHITE);
        }
    }

    private void showBildirimEkrani() {
        JDialog dialog = new JDialog(this, "Bildirimler", true);
        dialog.setSize(400, 600);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(Color.WHITE);

        bildirimService.bildirimleriOkunduYap(aktifDoktor.getKullaniciId());
        updateBildirimCount();

        JLabel header = new JLabel("Tüm Bildirimler", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setBorder(new EmptyBorder(10, 10, 10, 10));
        header.setForeground(Color.DARK_GRAY);
        dialog.add(header, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(new Color(245, 245, 245));

        List<Bildirim> bildirimler = bildirimService.getKullaniciBildirimleri(aktifDoktor.getKullaniciId(), false);

        if (bildirimler.isEmpty()) {
            JLabel emptyLbl = new JLabel("Görüntülenecek bildirim bulunmamaktadır.", SwingConstants.CENTER);
            emptyLbl.setBorder(new EmptyBorder(20,0,0,0));
            listPanel.add(emptyLbl);
        } else {
            for (Bildirim b : bildirimler) {
                listPanel.add(createBildirimKarti(b));
                listPanel.add(Box.createVerticalStrut(8));
            }
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        dialog.add(scrollPane, BorderLayout.CENTER);

        dialog.setVisible(true);
    }

    private JPanel createBildirimKarti(Bildirim bildirim) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        boolean isIptal = bildirim.getKonu().toLowerCase().contains("iptal") || 
                          bildirim.getIcerik().toLowerCase().contains("iptal");
        Color cardColor = bildirim.isOkundu() ? Color.WHITE : new Color(240, 248, 255);
        Color borderColor = isIptal ? new Color(220, 53, 69) : (bildirim.isOkundu() ? Color.LIGHT_GRAY : new Color(0, 102, 204));
        Color textColor = isIptal ? new Color(220, 53, 69) : Color.DARK_GRAY;

        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 1, 0, borderColor),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblIcon = new JLabel(isIptal ? "❌" : "📢");
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        lblIcon.setBorder(new EmptyBorder(0, 0, 0, 10));
        card.add(lblIcon, BorderLayout.WEST);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1));
        centerPanel.setBackground(cardColor);
        JLabel lblKonu = new JLabel(bildirim.getKonu());
        lblKonu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblKonu.setForeground(textColor);
        String kisaIcerik = bildirim.getIcerik().replace("\n", " ").trim();
        if(kisaIcerik.length() > 50) kisaIcerik = kisaIcerik.substring(0, 50) + "...";
        JLabel lblIcerik = new JLabel(kisaIcerik);
        lblIcerik.setForeground(Color.GRAY);
        lblIcerik.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        centerPanel.add(lblKonu);
        centerPanel.add(lblIcerik);
        card.add(centerPanel, BorderLayout.CENTER);

        String tarihSaat = bildirim.getGonderimTarihi().format(DateTimeFormatter.ofPattern("dd MMM HH:mm"));
        JLabel lblTarih = new JLabel(tarihSaat);
        lblTarih.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTarih.setForeground(Color.GRAY);
        lblTarih.setVerticalAlignment(SwingConstants.TOP);
        card.add(lblTarih, BorderLayout.EAST);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                 JOptionPane.showMessageDialog(card,
                     "<html><body style='width: 300px;'>" + bildirim.getIcerik().replace("\n", "<br>") + "</body></html>",
                     bildirim.getKonu(),
                     isIptal ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
            }
        });
        return card;
    }

    private JPanel createBottomNav() {
        JPanel nav = new JPanel(new GridLayout(1, 3));
        nav.setBackground(new Color(240, 240, 240));
        nav.setPreferredSize(new Dimension(450, 60));
        nav.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        String[] labels = {"Randevular", "Hasta Arama", "Ayarlar"};
        String[] icons = {"📅", "🔍", "⚙"};
        
        JButton btnRandevu = createNavButton(icons[0], labels[0], true);
        JButton btnArama = createNavButton(icons[1], labels[1], false);
        JButton btnAyarlar = createNavButton(icons[2], labels[2], false);

        btnRandevu.addActionListener(e -> { 
            cardLayout.show(mainContainer, "RANDEVULAR");
            updateNavStyles(btnRandevu, btnArama, btnAyarlar);
            currentFilter = "TÜMÜ";
            fetchData();
        });
        
        btnArama.addActionListener(e -> showHastaAramaDialog()); 
        
        btnAyarlar.addActionListener(e -> {
            cardLayout.show(mainContainer, "AYARLAR");
            updateNavStyles(btnAyarlar, btnRandevu, btnArama);
        });

        nav.add(btnRandevu);
        nav.add(btnArama);
        nav.add(btnAyarlar);
        return nav;
    }
    
    private void updateNavStyles(JButton active, JButton... others) {
        active.setForeground(CARD_BORDER);
        for(JButton btn : others) {
            btn.setForeground(Color.GRAY);
        }
    }
    
    private JButton createNavButton(String icon, String label, boolean isActive) {
        JButton b = new JButton("<html><center>" + icon + "<br>" + label + "</center></html>");
        b.setBorderPainted(false); 
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setForeground(isActive ? CARD_BORDER : Color.GRAY);
        return b;
    }

    private void showHastaAramaDialog() {
        JDialog dialog = new JDialog(this, "Hasta Arama", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BG_LIGHT);

        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setBackground(THEME_NAVY);
        searchPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JTextField txtSearch = new JTextField();
        txtSearch.putClientProperty("JTextField.placeholderText", "Hasta Adı veya Soyadı Giriniz...");
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JButton btnSearch = new JButton("Ara");
        btnSearch.setBackground(BTN_GREEN);
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        
        searchPanel.add(txtSearch, BorderLayout.CENTER);
        searchPanel.add(btnSearch, BorderLayout.EAST);
        dialog.add(searchPanel, BorderLayout.NORTH);

        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBackground(Color.WHITE);
        
        JScrollPane scroll = new JScrollPane(resultPanel);
        scroll.setBorder(null);
        dialog.add(scroll, BorderLayout.CENTER);

        Runnable aramaIslemi = new Runnable() {
            @Override
            public void run() {
                String kriter = txtSearch.getText().trim();
                if (kriter.length() < 2) {
                    JOptionPane.showMessageDialog(dialog, "Lütfen en az 2 karakter giriniz.");
                    return;
                }
                
                List<RandevuDetay> sonuclar = randevuService.hastaAra(aktifDoktor.getKullaniciId(), kriter);
                resultPanel.removeAll();
                
                if (sonuclar.isEmpty()) {
                    JLabel lbl = new JLabel("Eşleşen hasta bulunamadı.");
                    lbl.setBorder(new EmptyBorder(20, 0, 0, 0));
                    lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
                    resultPanel.add(lbl);
                } else {
                    for (RandevuDetay r : sonuclar) {
                        resultPanel.add(createHastaSonucKarti(r, this));
                        resultPanel.add(Box.createVerticalStrut(10));
                    }
                }
                resultPanel.revalidate();
                resultPanel.repaint();
            }
        };

        btnSearch.addActionListener(e -> aramaIslemi.run());
        dialog.setVisible(true);
    }

    private JPanel createHastaSonucKarti(RandevuDetay r, Runnable onRefresh) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(450, 100));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, CARD_BORDER),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            )
        ));

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setBackground(Color.WHITE);
        
        JLabel lblAd = new JLabel(r.getDoktorAdSoyad()); 
        lblAd.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblAd.setForeground(THEME_NAVY);
        
        JLabel lblBilgi = new JLabel("Son İşlem: " + r.getRandevuSaati().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        lblBilgi.setForeground(Color.GRAY);
        
        infoPanel.add(lblAd);
        infoPanel.add(lblBilgi);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        btnPanel.setBackground(Color.WHITE);
        
        if ("planlandi".equalsIgnoreCase(r.getDurum())) {
            JButton btnIptal = new JButton("İptal Et");
            styleActionBtn(btnIptal, BTN_RED);
            btnIptal.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(card, 
                    "Bu hastanın aktif randevusunu iptal etmek istiyor musunuz?", 
                    "Onay", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = false;
                    try {
                        success = randevuService.doktorRandevuIptal(r.getRandevuId());
                    } catch (SQLException ex) {
                        Logger.getLogger(DoktorAnaMenu.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (success) {
                        JOptionPane.showMessageDialog(card, "Randevu iptal edildi.");
                        if (onRefresh != null) onRefresh.run();
                        fetchData();
                    } else {
                        JOptionPane.showMessageDialog(card, "Hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            btnPanel.add(btnIptal);
        }
        
        JButton btnTahlil = new JButton("Tahliller");
        styleActionBtn(btnTahlil, new Color(0, 123, 255));
        btnTahlil.addActionListener(e -> showHastaTahlilDialog(r.getHastaId(), r.getDoktorAdSoyad() + " - Tahliller"));
        
        btnPanel.add(btnTahlil);
        
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(btnPanel, BorderLayout.EAST);
        
        return card;
    }
}