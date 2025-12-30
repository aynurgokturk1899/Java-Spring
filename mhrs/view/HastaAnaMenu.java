package tr.mhrs.view;

import tr.mhrs.model.Kullanici;
import tr.mhrs.model.RandevuDetay;
import tr.mhrs.model.AileHekimiBilgisi;
import tr.mhrs.model.Bildirim;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Collections;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import tr.mhrs.service.BildirimService;
import tr.mhrs.service.RandevuService;


public class HastaAnaMenu extends JFrame {

    private final Kullanici aktifKullanici;
    private final RandevuService randevuService = new RandevuService();
    private final BildirimService bildirimService = new BildirimService();
    private final JPanel mainContentContainer;

    private JButton btnBildirim;
    private JPanel randevuListPanel;

    private String activeTab = "ANASAYFA";
    private String activeAppointmentList = "YAKLASAN";

    private final Color PRIMARY_BLUE = Color.decode("#007BFF");
    private final Color PRIMARY_BLUE_DARK = Color.decode("#0056B3");
    private final Color PRIMARY_GREEN = Color.decode("#28A745");
    private final Color BG_GRAY = Color.decode("#E0E0E0");
    private final Color CARD_WHITE = Color.WHITE;
    private final Color TEXT_DARK = Color.decode("#333333");
    private final Color TEXT_GRAY = Color.decode("#A9A9A9");
    private final Color TEXT_BLACK = Color.BLACK;

    private final int MOBILE_WIDTH = 450;
    private final int MOBILE_HEIGHT = 750;


    public HastaAnaMenu(Kullanici aktifKullanici) throws SQLException {
        super("Merkezi Hekim Randevu Sistemi");
        this.aktifKullanici = aktifKullanici;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(MOBILE_WIDTH, MOBILE_HEIGHT);
        setResizable(false);
        setLayout(new BorderLayout());
        this.getContentPane().setBackground(BG_GRAY);

        mainContentContainer = new JPanel(new BorderLayout());
        mainContentContainer.setBackground(BG_GRAY);

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(mainContentContainer, BorderLayout.CENTER);
        add(createBottomNav(), BorderLayout.SOUTH);

        switchContent("ANASAYFA");

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void updateBildirimCount() {
        int count = bildirimService.getOkunmamisBildirimSayisi(aktifKullanici.getKullaniciId());
        if (count > 0) {
            btnBildirim.setText("🔔(" + count + ")");
            btnBildirim.setForeground(Color.RED.darker());
        } else {
            btnBildirim.setText("🔔");
            btnBildirim.setForeground(TEXT_GRAY);
        }
    }
private void switchContent(String target) throws SQLException {
    mainContentContainer.removeAll();
    activeTab = target;

    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setBorder(null);

    JPanel viewPanel = null;

    if (target.equals("ANASAYFA")) {
        viewPanel = createHomeContent();
    } else if (target.equals("RANDEVULARIM")) {
        viewPanel = createRandevularimTabs();
    } else if (target.equals("AYARLAR")) {
        viewPanel = new AyarlarEkrani(aktifKullanici);
    } else if (target.equals("HASTANELER")) {
        viewPanel = new TahlilSonuclariEkrani(aktifKullanici.getKullaniciId());
    }

    if (viewPanel == null) {
        viewPanel = new JPanel();
        viewPanel.setBackground(BG_GRAY);
    }

    scrollPane.setViewportView(viewPanel);
    mainContentContainer.add(scrollPane, BorderLayout.CENTER);
    
    mainContentContainer.revalidate();
    mainContentContainer.repaint();

    updateBildirimCount();
}


    private JPanel createHomeContent() throws SQLException {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_GRAY);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 10, 15)); 

        JPanel topCardContainer = new JPanel(new GridLayout(1, 2, 10, 0)); 
        topCardContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        topCardContainer.setBackground(BG_GRAY);

        topCardContainer.add(createAileHekimiKartiGorsel());

        topCardContainer.add(createRandevuAlKartiGorsel());
        
        contentPanel.add(Box.createVerticalStrut(10)); 
        contentPanel.add(topCardContainer);
        contentPanel.add(Box.createVerticalStrut(30));


        JLabel lblRandevuHeader = new JLabel("Yaklaşan Randevularınız");
        lblRandevuHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblRandevuHeader.setForeground(TEXT_DARK);
        lblRandevuHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(lblRandevuHeader);
        contentPanel.add(Box.createVerticalStrut(10));

        randevuListPanel = new JPanel();
        randevuListPanel.setLayout(new BoxLayout(randevuListPanel, BoxLayout.Y_AXIS));
        randevuListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        randevuListPanel.setBackground(BG_GRAY);

        loadRandevular(randevuListPanel, "YAKLASAN");

        contentPanel.add(randevuListPanel);
        contentPanel.add(Box.createVerticalGlue());


        return contentPanel;
    }
    
   private JPanel createAileHekimiKartiGorsel() {
    JPanel card = new JPanel(new BorderLayout());
    card.setBackground(PRIMARY_GREEN); 
    card.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
    card.setCursor(new Cursor(Cursor.HAND_CURSOR));
  
    JLabel lblIcon = new JLabel("🏠", SwingConstants.CENTER); 
    lblIcon.setFont(new Font("Segoe UI", Font.BOLD, 24));
    lblIcon.setForeground(CARD_WHITE);
    card.add(lblIcon, BorderLayout.NORTH);

    JLabel lblTitle = new JLabel("Aile Hekimi Bilgileri", SwingConstants.CENTER);
    lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
    lblTitle.setForeground(CARD_WHITE);
    card.add(lblTitle, BorderLayout.SOUTH);
       card.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            new AileHekimiEkrani(aktifKullanici.getKullaniciId(), HastaAnaMenu.this).setVisible(true);
            
            HastaAnaMenu.this.setVisible(false);
        }
    });
        
        return card;
    }
    
    private JPanel createRandevuAlKartiGorsel() {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(PRIMARY_BLUE);
        card.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblIcon = new JLabel("➕", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblIcon.setForeground(CARD_WHITE);
        card.add(lblIcon, BorderLayout.NORTH);

        JLabel lblTitle = new JLabel("Hastane Randevusu Al", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(CARD_WHITE);
        card.add(lblTitle, BorderLayout.SOUTH);
        
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showRandevuSecimEkrani();
            }
        });

        return card;
    }


    private JPanel createRandevularimTabs() throws SQLException {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_GRAY);
        
        JPanel tabHeader = new JPanel(new GridLayout(1, 2));
        tabHeader.setPreferredSize(new Dimension(getWidth(), 50));
        
        JButton btnYaklasan = new JButton("RANDEVULARIM");
        JButton btnGecmis = new JButton("GEÇMİŞ RANDEVULAR");
        
        JPanel listContainer = new JPanel(new BorderLayout());
        listContainer.setBackground(BG_GRAY);
        listContainer.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        randevuListPanel = new JPanel();
        randevuListPanel.setLayout(new BoxLayout(randevuListPanel, BoxLayout.Y_AXIS));
        randevuListPanel.setBackground(BG_GRAY);
        listContainer.add(randevuListPanel, BorderLayout.NORTH);

        ActionListener tabListener = e -> {
            JButton source = (JButton) e.getSource();
            if (source == btnYaklasan) {
                activeAppointmentList = "YAKLASAN";
            } else {
                activeAppointmentList = "GEÇMİŞ";
            }
            styleTabButton(btnYaklasan, activeAppointmentList.equals("YAKLASAN"));
            styleTabButton(btnGecmis, activeAppointmentList.equals("GEÇMİŞ"));
            try {
                loadRandevular(randevuListPanel, activeAppointmentList);
            } catch (SQLException ex) {
                Logger.getLogger(HastaAnaMenu.class.getName()).log(Level.SEVERE, null, ex);
            }
        };
        
        styleTabButton(btnYaklasan, true);
        styleTabButton(btnGecmis, false);
        btnYaklasan.addActionListener(tabListener);
        btnGecmis.addActionListener(tabListener);
        
        tabHeader.add(btnYaklasan);
        tabHeader.add(btnGecmis);
        
        panel.add(tabHeader, BorderLayout.NORTH);
        panel.add(listContainer, BorderLayout.CENTER);
        
        loadRandevular(randevuListPanel, activeAppointmentList);

        return panel;
    }

    private void styleTabButton(JButton button, boolean isActive) {
        if (isActive) {
            button.setBackground(CARD_WHITE);
            button.setForeground(PRIMARY_BLUE_DARK);
            button.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, PRIMARY_BLUE));
            button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        } else {
            button.setBackground(CARD_WHITE);
            button.setForeground(TEXT_GRAY);
            button.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0));
            button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }
        button.setFocusPainted(false);
    }
    
    public void loadRandevular(JPanel targetPanel, String listType) throws SQLException {
    targetPanel.removeAll();
    List<RandevuDetay> randevular;

    if (listType.equals("YAKLASAN") || listType.equals("ANASAYFA")) {
        randevular = randevuService.getYaklasanRandevular(aktifKullanici.getKullaniciId()); 
    } else if (listType.equals("GEÇMİŞ")) {
        randevular = randevuService.getGecmisRandevular(aktifKullanici.getKullaniciId());
    } else {
        randevular = Collections.emptyList();
    }

    if (randevular == null || randevular.isEmpty()) { 
        JPanel noRandevuCard = new JPanel(new BorderLayout());
        noRandevuCard.setBackground(CARD_WHITE);
        noRandevuCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(30, 15, 30, 15)
        ));

        JLabel lblNoRandevu = new JLabel("Bu kategoride randevunuz bulunmamaktadır.", SwingConstants.CENTER);
        lblNoRandevu.setForeground(TEXT_GRAY);
        lblNoRandevu.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        noRandevuCard.add(lblNoRandevu, BorderLayout.CENTER);
        targetPanel.add(noRandevuCard);
    } else {
        if (listType.equals("ANASAYFA")) {
            targetPanel.add(createRandevuKarti(randevular.get(0), listType));
        } else {
            for (RandevuDetay r : randevular) {
    JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    wrapper.setOpaque(false); 
    
    wrapper.add(createRandevuKarti(r, listType));
    
    targetPanel.add(wrapper);
    targetPanel.add(Box.createVerticalStrut(15));
}
        }
    }
    
    targetPanel.revalidate();
    targetPanel.repaint();
}
    

private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(CARD_WHITE);

        JLabel lblTitle = new JLabel("MHRS +", SwingConstants.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(PRIMARY_BLUE);

        JLabel lblMenuIcon = new JLabel("☰");
        lblMenuIcon.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblMenuIcon.setForeground(TEXT_DARK);

        btnBildirim = new JButton("🔔");
        btnBildirim.setBorderPainted(false);
        btnBildirim.setContentAreaFilled(false);
        btnBildirim.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        btnBildirim.setForeground(TEXT_GRAY);
        btnBildirim.addActionListener(e -> showBildirimEkrani());

        JLabel lblMoreIcon = new JLabel("⋮");
        lblMoreIcon.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblMoreIcon.setForeground(TEXT_DARK);
        lblMoreIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblMoreIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new ProfilEkrani(aktifKullanici, HastaAnaMenu.this).setVisible(true);
                setVisible(false);
            }
        });

        JPanel rightIcons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightIcons.setBackground(CARD_WHITE);
        rightIcons.add(btnBildirim);
        rightIcons.add(lblMoreIcon);

        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(CARD_WHITE);
        titleBar.add(lblTitle, BorderLayout.WEST);
        titleBar.add(rightIcons, BorderLayout.EAST);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(CARD_WHITE);
        topBar.add(lblMenuIcon, BorderLayout.WEST);
        topBar.add(titleBar, BorderLayout.CENTER); 

        panel.add(topBar, BorderLayout.NORTH);

        String karsilamaMetni;
        if (aktifKullanici.getAd() != null && aktifKullanici.getSoyad() != null) {
            karsilamaMetni = aktifKullanici.getAd().toUpperCase() + " " + aktifKullanici.getSoyad().toUpperCase();
        } else {
            karsilamaMetni = aktifKullanici.getTcKimlikNo();
        }

        JLabel lblUser = new JLabel("Merhaba, " + karsilamaMetni, SwingConstants.LEFT);
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUser.setForeground(TEXT_DARK);
        lblUser.setBorder(new EmptyBorder(5, 0, 10, 0));

        JPanel userBar = new JPanel(new BorderLayout());
        userBar.setBackground(CARD_WHITE);
        userBar.add(lblUser, BorderLayout.NORTH);
        userBar.add(createSearchBarGorsel(), BorderLayout.CENTER); 

        panel.add(userBar, BorderLayout.CENTER);

        return panel;
    }    
    private JPanel createSearchBarGorsel() {
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#E0E0E0"), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchPanel.setBackground(CARD_WHITE);
        
        JLabel lblSearchIcon = new JLabel("🔍");
        lblSearchIcon.setForeground(TEXT_GRAY);
        lblSearchIcon.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchPanel.add(lblSearchIcon, BorderLayout.WEST);

        JLabel lblPlaceholder = new JLabel("Poliklinik, Hastane veya Hekim Ara...", SwingConstants.LEFT);
        lblPlaceholder.setForeground(TEXT_GRAY);
        lblPlaceholder.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchPanel.add(lblPlaceholder, BorderLayout.CENTER);
        
        JLabel lblDropdown = new JLabel("⌄");
        lblDropdown.setForeground(TEXT_GRAY);
        lblDropdown.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        searchPanel.add(lblDropdown, BorderLayout.EAST);
        
        searchPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchPanel.addMouseListener(new MouseAdapter() {
             @Override
             public void mouseClicked(MouseEvent e) {
                 new RandevuAramaMobil(aktifKullanici.getKullaniciId(), HastaAnaMenu.this).setVisible(true);
                 HastaAnaMenu.this.setVisible(false);
             }
        });

        searchPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
        searchPanel.setMaximumSize(new Dimension(400, 40)); 
        searchPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return searchPanel;
    }
    


    private JPanel createBottomNav() {
    JPanel navPanel = new JPanel(new GridLayout(1, 4, 1, 0));
    navPanel.setBackground(CARD_WHITE); 
    
    JButton btnAnaSayfa = createNavButton("🏠", "Ana Sayfa", "ANASAYFA");
    JButton btnRandevularim = createNavButton("🗓", "Randevularım", "RANDEVULARIM");
    JButton btnSonuclarim = createNavButton("🏥", "Sonuçlarım", "HASTANELER");
    JButton btnAyarlar = createNavButton("⚙", "Ayarlar", "AYARLAR");

    styleNavButton(btnAnaSayfa, true);

    ActionListener navListener = e -> {
        JButton source = (JButton) e.getSource();
        String target = (String) source.getClientProperty("navTarget");

        if (target.equals("ANASAYFA") || target.equals("RANDEVULARIM") || target.equals("HASTANELER")|| target.equals("AYARLAR")) {
             styleNavButton(btnAnaSayfa, target.equals("ANASAYFA"));
             styleNavButton(btnRandevularim, target.equals("RANDEVULARIM"));
             styleNavButton(btnSonuclarim, target.equals("HASTANELER"));
             styleNavButton(btnAyarlar, target.equals("AYARLAR"));
             styleNavButton(btnAyarlar, false);
            try {
                switchContent(target);
            } catch (SQLException ex) {
                Logger.getLogger(HastaAnaMenu.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
             JOptionPane.showMessageDialog(this, "Bu ekran henüz geliştirilmedi.", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
             styleNavButton(btnAnaSayfa, activeTab.equals("ANASAYFA"));
             styleNavButton(btnRandevularim, activeTab.equals("RANDEVULARIM"));
             styleNavButton(btnSonuclarim, activeTab.equals("HASTANELER"));
             styleNavButton(btnAyarlar, false);
        }
    };

    btnAnaSayfa.addActionListener(navListener);
    btnRandevularim.addActionListener(navListener);
    btnSonuclarim.addActionListener(navListener);
    btnAyarlar.addActionListener(navListener);

    navPanel.add(btnAnaSayfa);
    navPanel.add(btnRandevularim);
    navPanel.add(btnSonuclarim);
    navPanel.add(btnAyarlar);

    return navPanel;
}
    private JButton createNavButton(String icon, String text, String target) {
    JButton button = new JButton("<html><center>" + icon + "<br/><span style='font-size:8px;'>" + text + "</span></center></html>");
    button.setMargin(new Insets(2, 2, 2, 2));
    button.putClientProperty("navTarget", target);
    return button;
}

    private void styleNavButton(JButton button, boolean isActive) {
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); 
        button.setFont(new Font("Segoe UI", Font.BOLD, 10)); 

        if (isActive) {
            button.setBackground(CARD_WHITE);
            button.setForeground(PRIMARY_BLUE); 
        } else {
            button.setBackground(CARD_WHITE);
            button.setForeground(TEXT_GRAY); 
        }
    }
    

    public void loadYaklasanRandevular() throws SQLException {
        switchContent("ANASAYFA");
    }

    private void startSearchFlow(String tip) {
         String kriter = JOptionPane.showInputDialog(this,
             "Lütfen aramak istediğiniz " + tip + " adını giriniz:",
             tip + " Arama",
             JOptionPane.PLAIN_MESSAGE);

         if (kriter != null && !kriter.trim().isEmpty() && kriter.length() >= 3) {
             JOptionPane.showMessageDialog(this, "Arama motoru işlevi henüz tamamlanmadı.", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
         } else if (kriter != null) {
             JOptionPane.showMessageDialog(this, "Arama kriteri en az 3 karakter olmalıdır.", "Uyarı", JOptionPane.WARNING_MESSAGE);
         }
    }


    private void showRandevuSecimEkrani() {
        new RandevuAramaEkrani(aktifKullanici.getKullaniciId(), this).setVisible(true);
    this.setVisible(false);
    }

    private JButton createOptionButton(String text, Color foreground, Color background) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(foreground);
        button.setBackground(background);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(300, 50));
        button.setBorder(BorderFactory.createLineBorder(foreground.darker(), 1));
        return button;
    }

private JPanel createRandevuKarti(RandevuDetay r, String listType) {
    JPanel card = new JPanel(new BorderLayout(10, 0)); 
    card.setBackground(CARD_WHITE);
    
    Dimension cardSize = new Dimension(400, 135);
    card.setMinimumSize(cardSize);
    card.setPreferredSize(cardSize);
    card.setMaximumSize(cardSize);
    
    card.setAlignmentX(Component.CENTER_ALIGNMENT); 

    card.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
        BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));

    JPanel datePanel = new JPanel(new BorderLayout()); 
    datePanel.setPreferredSize(new Dimension(80, 80)); 
    datePanel.setBackground(Color.decode("#F2F2F2"));
    datePanel.setBorder(BorderFactory.createLineBorder(PRIMARY_BLUE, 1));

    Locale trLocale = new Locale("tr", "TR");
    String dayOfMonth = r.getRandevuSaati().toLocalDate().format(DateTimeFormatter.ofPattern("d"));
    String month = r.getRandevuSaati().toLocalDate().format(DateTimeFormatter.ofPattern("MMMM", trLocale));
    String dayOfWeek = r.getRandevuSaati().getDayOfWeek().getDisplayName(TextStyle.FULL, trLocale);

    JLabel lblTime = new JLabel(r.getRandevuSaati().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")), SwingConstants.CENTER);
    lblTime.setFont(new Font("Segoe UI", Font.BOLD, 18));
    lblTime.setForeground(Color.decode("#CC0000"));

    JPanel dateTop = new JPanel(new GridLayout(2, 1));
    dateTop.setBackground(Color.decode("#F2F2F2"));
    dateTop.add(new JLabel(dayOfMonth + " " + month.substring(0, 1).toUpperCase() + month.substring(1), SwingConstants.CENTER));
    dateTop.add(new JLabel(r.getRandevuSaati().toLocalDate().getYear() + " " + dayOfWeek.substring(0, 1).toUpperCase() + dayOfWeek.substring(1), SwingConstants.CENTER));

    datePanel.add(dateTop, BorderLayout.NORTH);
    datePanel.add(lblTime, BorderLayout.CENTER);
    card.add(datePanel, BorderLayout.WEST);

    JPanel detailPanel = new JPanel();
    detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));
    detailPanel.setBackground(CARD_WHITE);
    detailPanel.setBorder(new EmptyBorder(0, 8, 0, 0));

    JLabel lblHospital = new JLabel(r.getHastaneAd());
    lblHospital.setFont(new Font("Segoe UI", Font.BOLD, 12));
    lblHospital.setForeground(PRIMARY_BLUE.darker());

    JLabel lblDoctor = new JLabel("👨‍⚕️ Dr. " + r.getDoktorAdSoyad());
    lblDoctor.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    lblDoctor.setForeground(TEXT_DARK);

    JLabel lblDepartment = new JLabel("🩺 " + r.getBolumAd());
    lblDepartment.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    lblDepartment.setForeground(TEXT_DARK);

    String klinikText = (r.getKlinikBilgisi() != null ? r.getKlinikBilgisi() : "Belirtilmemiş");
    JLabel lblKlinik = new JLabel("🚪 Klinik: " + klinikText);
    lblKlinik.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    lblKlinik.setForeground(PRIMARY_GREEN);

    JLabel lblStatus = new JLabel();
    lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 11));
    lblStatus.setForeground(Color.decode("#CC0000"));
    lblStatus.setVisible(false);

    detailPanel.add(lblHospital);
    detailPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    detailPanel.add(lblDoctor);
    detailPanel.add(lblDepartment);
    detailPanel.add(lblKlinik);
    detailPanel.add(lblStatus);

    card.add(detailPanel, BorderLayout.CENTER);

    JButton actionButton = null;
    String durum = r.getDurum().toLowerCase();

    if (listType.equals("YAKLASAN") && durum.equals("planlandi")) {
        actionButton = new JButton("İptal Et");
        actionButton.setBackground(Color.decode("#CC0000"));
        actionButton.setForeground(CARD_WHITE);
        actionButton.addActionListener(e -> {
            try {
                randevuIptalIslemi(r.getRandevuId());
            } catch (SQLException ex) {
                Logger.getLogger(HastaAnaMenu.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
    } else if (durum.equals("iptal")) {
        actionButton = new JButton("Geri Al");
        actionButton.setBackground(new Color(255, 193, 7)); 
        actionButton.setForeground(Color.BLACK);
        
        lblStatus.setText("* İptal Edildi");
        lblStatus.setVisible(true);

        actionButton.addActionListener(e -> {
            String sonuc = randevuService.randevuGeriAl(r.getRandevuId());
            if (sonuc.equals("OK")) {
                JOptionPane.showMessageDialog(card, "Randevu başarıyla geri alındı ve tekrar planlandı.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                try {
                    loadRandevular(randevuListPanel, activeAppointmentList);
                } catch (SQLException ex) {
                    Logger.getLogger(HastaAnaMenu.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                JOptionPane.showMessageDialog(card, sonuc, "Uyarı", JOptionPane.WARNING_MESSAGE);
            }
        });

    } else if (listType.equals("GEÇMİŞ") || durum.equals("tamamlandi")) {
        actionButton = new JButton("Randevu Al");
        actionButton.setBackground(PRIMARY_BLUE);
        actionButton.setForeground(CARD_WHITE);
        
        if (durum.equals("tamamlandi")) {
            lblStatus.setText("* Tamamlandı");
            lblStatus.setVisible(true);
        }

        actionButton.addActionListener(e -> {
             JOptionPane.showMessageDialog(card, "Yeni randevu arama ekranına yönlendiriliyorsunuz...", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
             showRandevuSecimEkrani();
        });
    }

    if (actionButton != null) {
        actionButton.setPreferredSize(new Dimension(90, 30)); 
        actionButton.setFont(new Font("Segoe UI", Font.BOLD, 11));

        JPanel buttonWrapper = new JPanel(new GridBagLayout()); 
        buttonWrapper.setBackground(CARD_WHITE);
        buttonWrapper.add(actionButton);
        card.add(buttonWrapper, BorderLayout.EAST);
    }

    return card;
}

    private void randevuIptalIslemi(Long randevuId) throws SQLException {
         int confirm = JOptionPane.showConfirmDialog(this,
             "Randevuyu iptal etmek istediğinizden emin misiniz?",
             "Randevu İptal Onayı", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

         if (confirm == JOptionPane.YES_OPTION) {
             boolean success = randevuService.randevuIptal(randevuId);

             if (success) {
                 JOptionPane.showMessageDialog(this, "Randevu başarıyla iptal edildi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
             } else {
                 JOptionPane.showMessageDialog(this, "Randevu iptali başarısız oldu. Bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
             }

             loadRandevular(randevuListPanel, activeAppointmentList);
         }
    }
    
    private void showBildirimEkrani() {
        JDialog dialog = new JDialog(this, "Bildirimleriniz", true);
        dialog.setSize(400, 600);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(CARD_WHITE);

        bildirimService.bildirimleriOkunduYap(aktifKullanici.getKullaniciId());
        updateBildirimCount();

        JLabel header = new JLabel("Tüm Bildirimler", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setBorder(new EmptyBorder(10, 10, 10, 10));
        header.setForeground(TEXT_DARK);
        dialog.add(header, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(BG_GRAY);

        List<Bildirim> bildirimler = bildirimService.getKullaniciBildirimleri(aktifKullanici.getKullaniciId(), false);

        if (bildirimler.isEmpty()) {
            listPanel.add(new JLabel("Görüntülenecek bildirim bulunmamaktadır.", SwingConstants.CENTER));
        } else {
            for (Bildirim b : bildirimler) {
                listPanel.add(createBildirimKarti(b));
                listPanel.add(Box.createVerticalStrut(5));
            }
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        dialog.add(scrollPane, BorderLayout.CENTER);

        dialog.setVisible(true);
    }
    
    
    private JPanel createBildirimKarti(Bildirim bildirim) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        Color cardColor = bildirim.isOkundu() ? Color.WHITE : new Color(240, 248, 255);
        Color borderColor = bildirim.isOkundu() ? Color.LIGHT_GRAY : new Color(0, 123, 255); 
        
        boolean isIptal = bildirim.getKonu().toLowerCase().contains("iptal");
        if (isIptal) {
            cardColor = new Color(255, 245, 245); 
            borderColor = new Color(220, 53, 69);
        }

        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 1, 0, borderColor), 
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90)); 
        card.setPreferredSize(new Dimension(350, 90));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(cardColor);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(cardColor);
        
        String icon = isIptal ? "❌ " : "📢 ";
        JLabel lblKonu = new JLabel(icon + bildirim.getKonu());
        lblKonu.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblKonu.setForeground(new Color(50, 50, 50));
        
        String saatStr = bildirim.getGonderimTarihi().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        String tarihStr = bildirim.getGonderimTarihi().toLocalDate().format(DateTimeFormatter.ofPattern("dd MMM"));
        JLabel lblTarih = new JLabel(tarihStr + " " + saatStr);
        lblTarih.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTarih.setForeground(Color.GRAY);

        headerPanel.add(lblKonu, BorderLayout.CENTER);
        headerPanel.add(lblTarih, BorderLayout.EAST);
        
        contentPanel.add(headerPanel);
        contentPanel.add(Box.createVerticalStrut(5)); 

        String rawIcerik = bildirim.getIcerik().replace("\n", " ").trim();
        if (rawIcerik.length() > 70) rawIcerik = rawIcerik.substring(0, 70) + "...";
        
        JLabel lblIcerik = new JLabel("<html>" + rawIcerik + "</html>");
        lblIcerik.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblIcerik.setForeground(new Color(100, 100, 100));
        
        contentPanel.add(lblIcerik);

        card.add(contentPanel, BorderLayout.CENTER);

        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                 JOptionPane.showMessageDialog(card,
                     "<html><body style='width: 300px;'>" + bildirim.getIcerik().replace("\n", "<br>") + "</body></html>",
                     bildirim.getKonu(),
                     isIptal ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
            }
        });

        return card;
    }}