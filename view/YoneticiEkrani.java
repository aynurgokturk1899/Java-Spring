package yonetici_app.view;

import yonetici_app.data.*;
import yonetici_app.model.*;
import yonetici_app.strategy.*;
import yonetici_app.observer.*; 

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.*;
import java.util.List;

public class YoneticiEkrani extends JFrame {
    private final YoneticiDAO yoneticiDAO = new YoneticiDAO();
    private final DoktorDAO doktorDAO = new DoktorDAO();
    private final RandevuDAO randevuDAO = new RandevuDAO();
    private final RandevuIptalSubject iptalSubject;

    private final Color SIDEBAR_BG = new Color(24, 28, 32);
    private final Color MAIN_BG = new Color(30, 34, 39);
    private final Color CARD_BG = new Color(42, 48, 56);
    private final Color ACCENT_COLOR = new Color(0, 168, 255);
    private final Color TEXT_COLOR = new Color(210, 210, 210);
    private final Color GREEN_STATUS = new Color(46, 204, 113);
    private final Color RED_STATUS = new Color(231, 76, 60);
    private final Color BLUE_STATUS = new Color(52, 152, 219);
    private final Color PURPLE_COLOR = new Color(155, 89, 182);

    private JComboBox<Hastane> cmbHastane;
    private JComboBox<Doktor> cmbDoktor;
    private JTextField txtBaslangic, txtBitis, txtSure, txtMaxGun;
    private JTable tblRandevular;
    private DefaultTableModel tableModel;
    private JLabel lblTarihGosterim;

    private JLabel lblGunlukRandevu, lblIptalOrani, lblAktifDoktor, lblAktifHasta;

    private JPanel calendarGridPanel, contentPanel;
    private CardLayout cardLayout = new CardLayout();
    private LocalDate seciliTarih = LocalDate.now().plusDays(1);
    private YearMonth gosterilenAy = YearMonth.from(seciliTarih);
    private boolean isUpdating = false;

    public YoneticiEkrani() {
        super("MHRS Yönetici Paneli");
        
        iptalSubject = new RandevuIptalSubject();
        iptalSubject.ekle(new DoktorBildirimObserver()); 
        iptalSubject.ekle(new HastaBildirimObserver(this));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);

        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(MAIN_BG);
        
        contentPanel.add(createDashboardSayfasi(), "DASHBOARD");
        contentPanel.add(createRandevuSayfasi(), "RANDEVU");
        contentPanel.add(createMesaiSayfasi(), "MESAI");

        add(contentPanel, BorderLayout.CENTER);

        loadInitialData();
        refreshDashboardStats();
        setVisible(true);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(40, 20, 20, 20));

        JLabel logo = new JLabel("MHRS ADMIN");
        logo.setForeground(ACCENT_COLOR);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(60));

        sidebar.add(createMenuButton("Dashboard", "DASHBOARD"));
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(createMenuButton("Randevu Takibi", "RANDEVU"));
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(createMenuButton("Mesai Ayarları", "MESAI"));

        return sidebar;
    }

    private JButton createMenuButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(220, 50));
        btn.setBackground(SIDEBAR_BG);
        btn.setForeground(TEXT_COLOR);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);

        btn.addActionListener(e -> {
            cardLayout.show(contentPanel, cardName);
            if ("DASHBOARD".equals(cardName) || "RANDEVU".equals(cardName)) {
                refreshDashboardStats();
            }
        });
        return btn;
    }

    private JPanel createDashboardSayfasi() {
        JPanel p = new JPanel(new BorderLayout(25, 25));
        p.setBackground(MAIN_BG);
        p.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel title = new JLabel("SİSTEM ÖZETİ");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        p.add(title, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 2, 30, 30));
        grid.setOpaque(false);

        lblGunlukRandevu = new JLabel("0");
        lblIptalOrani = new JLabel("0%");
        lblAktifDoktor = new JLabel("0");
        lblAktifHasta = new JLabel("0");

        grid.add(createStatCard("Günlük Randevular", lblGunlukRandevu, ACCENT_COLOR));
        grid.add(createStatCard("İptal Oranı", lblIptalOrani, RED_STATUS));
        grid.add(createStatCard("Aktif Doktorlar", lblAktifDoktor, GREEN_STATUS));
        grid.add(createStatCard("Aktif Hastalar", lblAktifHasta, PURPLE_COLOR));

        p.add(grid, BorderLayout.CENTER);
        return p;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel lblT = new JLabel(title);
        lblT.setForeground(TEXT_COLOR);
        lblT.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        valueLabel.setForeground(color);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));

        card.add(lblT, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createRandevuSayfasi() {
        JPanel p = new JPanel(new BorderLayout(25, 25));
        p.setBackground(MAIN_BG);
        p.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel listCard = new JPanel(new BorderLayout(15, 15));
        listCard.setBackground(CARD_BG);
        listCard.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterBar.setOpaque(false);
        filterBar.add(createStyledFilterBtn("Planlı (Aktif)", PURPLE_COLOR, "yonetici_planli_gelecek"));
        filterBar.add(createStyledFilterBtn("Planlı (Geçmiş)", Color.GRAY, "yonetici_planli_gecmis"));
        filterBar.add(createStyledFilterBtn("Aktif Randevular", BLUE_STATUS, "planlandi"));
        filterBar.add(createStyledFilterBtn("Tamamlandı", GREEN_STATUS, "tamamlandi"));
        filterBar.add(createStyledFilterBtn("İptal Edildi", RED_STATUS, "iptal"));

        listCard.add(filterBar, BorderLayout.NORTH);

        tblRandevular = new JTable();
        setupModernTable();
        JScrollPane scroll = new JScrollPane(tblRandevular);
        scroll.getViewport().setBackground(MAIN_BG);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        listCard.add(scroll, BorderLayout.CENTER);

        JPanel actionP = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionP.setOpaque(false);
        JButton btnMsg = new JButton("Özel Bildirim");
        btnMsg.setBackground(new Color(60, 68, 80));
        btnMsg.setForeground(Color.WHITE);
        btnMsg.addActionListener(e -> gonderOzelBildirim());

        JButton btnIptal = new JButton("İptal Et & Bildir");
        btnIptal.setBackground(new Color(60, 68, 80));
        btnIptal.setForeground(Color.WHITE);
        btnIptal.addActionListener(e -> iptalEtVeBilgilendir());

        actionP.add(btnMsg);
        actionP.add(btnIptal);
        listCard.add(actionP, BorderLayout.SOUTH);

        p.add(listCard, BorderLayout.CENTER);
        return p;
    }

    private void setupModernTable() {
        String[] columns = {"HASTA ADI", "DOKTOR & BÖLÜM", "TARİH / SAAT", "DURUM", "RAND_ID"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblRandevular.setModel(tableModel);
        tblRandevular.setRowHeight(50);
        tblRandevular.getTableHeader().setBackground(SIDEBAR_BG);
        tblRandevular.getTableHeader().setForeground(Color.WHITE);
        
        tblRandevular.removeColumn(tblRandevular.getColumnModel().getColumn(4)); 

        tblRandevular.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = tblRandevular.getSelectedRow();
                    if (row != -1) handleTableDoubleClick(row);
                }
            }
        });
    }

    private void handleTableDoubleClick(int row) {
        try {
            String drBilgi = tableModel.getValueAt(row, 1).toString();
            String tarihStr = tableModel.getValueAt(row, 2).toString().split(" ")[0];
            String durum = tableModel.getValueAt(row, 3).toString();
            int modelCol = tableModel.getColumnCount() - 1;
            Long hiddenId = Long.parseLong(tableModel.getValueAt(row, modelCol).toString());
            Long docId = ("MESAİ TANIMLI".equalsIgnoreCase(durum)) ? hiddenId : yoneticiDAO.getDoktorIdByRandevu(hiddenId);

            if (docId != null) {
                new DoktorRandevuEkrani(docId, drBilgi, LocalDate.parse(tarihStr));
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void refreshDashboardStats() {
        try {
            if (lblGunlukRandevu != null) lblGunlukRandevu.setText(String.valueOf(randevuDAO.getGunlukRandevuSayisi()));
            if (lblIptalOrani != null) lblIptalOrani.setText(randevuDAO.getIptalOrani() + "%");
            if (lblAktifDoktor != null) lblAktifDoktor.setText(String.valueOf(randevuDAO.getAktifDoktorSayisi()));
            if (lblAktifHasta != null) lblAktifHasta.setText(String.valueOf(randevuDAO.getAktifHastaSayisi()));
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private JPanel createMesaiSayfasi() {
        JPanel p = new JPanel(new BorderLayout(25, 25));
        p.setBackground(MAIN_BG);
        p.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel form = new JPanel(new GridLayout(3, 4, 15, 15));
        form.setBackground(CARD_BG);
        form.setBorder(new EmptyBorder(25, 25, 25, 25));

        cmbHastane = new JComboBox<>();
        cmbDoktor = new JComboBox<>();
        txtBaslangic = new JTextField("09:00");
        txtBitis = new JTextField("17:00");
        txtSure = new JTextField("20");
        txtMaxGun = new JTextField();

        form.add(new JLabel("Hastane:") {{ setForeground(TEXT_COLOR); }});
        form.add(cmbHastane);
        form.add(new JLabel("Doktor:") {{ setForeground(TEXT_COLOR); }});
        form.add(cmbDoktor);
        form.add(new JLabel("Başlangıç:") {{ setForeground(TEXT_COLOR); }});
        form.add(txtBaslangic);
        form.add(new JLabel("Bitiş:") {{ setForeground(TEXT_COLOR); }});
        form.add(txtBitis);
        form.add(new JLabel("Süre (dk):") {{ setForeground(TEXT_COLOR); }});
        form.add(txtSure);
        form.add(new JLabel("Maks. Gün:") {{ setForeground(TEXT_COLOR); }});
        form.add(txtMaxGun);

        p.add(form, BorderLayout.NORTH);

        JPanel cal = new JPanel(new BorderLayout(15, 15));
        cal.setBackground(CARD_BG);
        cal.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel calHeader = new JPanel(new BorderLayout());
        calHeader.setBackground(CARD_BG);
        JButton btnPrev = new JButton("<<");
        JButton btnNext = new JButton(">>");
        lblTarihGosterim = new JLabel("", SwingConstants.CENTER);
        lblTarihGosterim.setForeground(Color.WHITE);
        btnPrev.addActionListener(e -> { gosterilenAy = gosterilenAy.minusMonths(1); updateCalendar(); });
        btnNext.addActionListener(e -> { gosterilenAy = gosterilenAy.plusMonths(1); updateCalendar(); });
        calHeader.add(btnPrev, BorderLayout.WEST);
        calHeader.add(lblTarihGosterim, BorderLayout.CENTER);
        calHeader.add(btnNext, BorderLayout.EAST);

        calendarGridPanel = new JPanel(new GridLayout(0, 7, 10, 10));
        calendarGridPanel.setBackground(CARD_BG);
        cal.add(calHeader, BorderLayout.NORTH);
        cal.add(calendarGridPanel, BorderLayout.CENTER);

        JButton save = new JButton("KAYDET");
        save.setBackground(ACCENT_COLOR);
        save.setForeground(Color.WHITE);
        save.addActionListener(e -> handleSave());
        cal.add(save, BorderLayout.SOUTH);

        p.add(cal, BorderLayout.CENTER);
        cmbHastane.addActionListener(e -> { if (!isUpdating) loadDoktorlar(); });
        cmbDoktor.addActionListener(e -> { if (!isUpdating) yukleMesai(); });

        return p;
    }

    private void updateCalendar() {
        calendarGridPanel.removeAll();
        lblTarihGosterim.setText(gosterilenAy.toString() + " | Seçili: " + seciliTarih);
        LocalDate first = gosterilenAy.atDay(1);
        int dayOfWeek = first.getDayOfWeek().getValue();
        for (int i = 1; i < dayOfWeek; i++) calendarGridPanel.add(new JLabel(""));
        for (int i = 1; i <= gosterilenAy.lengthOfMonth(); i++) {
            final int gun = i;
            JButton b = new JButton(String.valueOf(i));
            LocalDate d = gosterilenAy.atDay(gun);
            if (d.isBefore(LocalDate.now())) {
                b.setBackground(new Color(45, 45, 45));
                b.setForeground(Color.GRAY);
                b.setEnabled(false);
            } else {
                b.setBackground(d.equals(seciliTarih) ? ACCENT_COLOR : new Color(60, 68, 80));
                b.setForeground(Color.WHITE);
            }
            b.addActionListener(e -> { seciliTarih = d; updateCalendar(); yukleMesai(); });
            calendarGridPanel.add(b);
        }
        calendarGridPanel.revalidate(); calendarGridPanel.repaint();
    }

    private void loadInitialData() {
        try {
            isUpdating = true;
            cmbHastane.removeAllItems(); cmbHastane.addItem(new Hastane(0L, "--- Seçiniz ---"));
            List<Hastane> hList = doktorDAO.tumHastaneleriGetir();
            for (Hastane h : hList) cmbHastane.addItem(h);
            txtMaxGun.setText(String.valueOf(yoneticiDAO.getMaxRandevuGunSayisi()));
            isUpdating = false; updateCalendar();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadDoktorlar() {
        if (isUpdating) return;
        try {
            isUpdating = true; cmbDoktor.removeAllItems();
            Doktor bos = new Doktor(); bos.setDoktorId(0L); bos.setAd("---"); bos.setSoyad("Seçiniz ---");
            cmbDoktor.addItem(bos);
            Object selected = cmbHastane.getSelectedItem();
            if (selected instanceof Hastane h && h.getHastaneId() != 0L) {
                List<Doktor> doktorlar = doktorDAO.doktorlariHastaneIdIleGetir(h.getHastaneId());
                for (Doktor d : doktorlar) cmbDoktor.addItem(d);
            }
        } catch (SQLException e) { e.printStackTrace(); } finally { isUpdating = false; }
    }

    private void yukleMesai() {
        Object selected = cmbDoktor.getSelectedItem();
        if (selected instanceof Doktor d && d.getDoktorId() != 0L) {
            try {
                DoktorMusaitlik m = yoneticiDAO.getMesaiBilgisi(d.getDoktorId(), seciliTarih);
                if (m != null) {
                    txtBaslangic.setText(m.getBaslangicSaati().toString());
                    txtBitis.setText(m.getBitisSaati().toString());
                    txtSure.setText(String.valueOf(m.getRandevuSuresiDk()));
                } else {
                    txtBaslangic.setText("09:00"); txtBitis.setText("17:00"); txtSure.setText("20");
                }
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private void handleSave() {
        try {
            if (seciliTarih.isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(this, "Geçmiş bir tarihe işlem yapamazsınız!"); return;
            }
            if (cmbDoktor.getSelectedItem() instanceof Doktor d && d.getDoktorId() != 0L) {
                yoneticiDAO.saveOrUpdateMesai(d.getDoktorId(), seciliTarih, LocalTime.parse(txtBaslangic.getText()),
                                               LocalTime.parse(txtBitis.getText()), Integer.parseInt(txtSure.getText()));
            }
            yoneticiDAO.saveMaxRandevuGunSayisi(Integer.parseInt(txtMaxGun.getText()));
            JOptionPane.showMessageDialog(this, "Veriler güncellendi!");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Hata: " + e.getMessage()); }
    }

private void gonderOzelBildirim() {
    int row = tblRandevular.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Lütfen bir kayıt seçin!");
        return;
    }
    try {
        int modelCol = tableModel.getColumnCount() - 1;
        Long hiddenId = Long.parseLong(tableModel.getValueAt(row, modelCol).toString());
        String durum = tableModel.getValueAt(row, 3).toString(); 

        String ad, iletisim, konu = "Yönetici Özel Bildirimi";
        Long hedefId;

        if ("MESAİ TANIMLI".equalsIgnoreCase(durum)) {
            String info = yoneticiDAO.getDoktorIletisim(hiddenId);
            if (info == null) return;
            String[] p = info.split(";");
            ad = p[0]; iletisim = p[1]; hedefId = hiddenId;
        } else {
            hedefId = yoneticiDAO.getHastaIdByRandevu(hiddenId);
            String info = yoneticiDAO.getHastaEmailVeAd(hiddenId);
            if (info == null) return;
            String[] p = info.split(";");
            ad = p[0]; iletisim = p[1];
        }

        String mesaj = JOptionPane.showInputDialog(this, ad + " kişisine iletilecek mesaj:");
        if (mesaj == null || mesaj.isEmpty()) return;

        BildirimContext context = new BildirimContext(this);
        String secilenTur = context.bildirimiGonderSorarak(ad, iletisim, konu, mesaj);

        if (secilenTur != null) {
            yoneticiDAO.bildirimKaydet(hedefId, hiddenId, secilenTur, mesaj);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}
    private void iptalEtVeBilgilendir() {
        int row = tblRandevular.getSelectedRow();
        if (row == -1) return;

        try {
            int modelRow = tblRandevular.convertRowIndexToModel(row);
            Long hiddenId = Long.parseLong(tableModel.getValueAt(modelRow, 4).toString());
            String durum = tableModel.getValueAt(modelRow, 3).toString();

            if ("MESAİ TANIMLI".equalsIgnoreCase(durum)) {
                LocalDate tarih = LocalDate.parse(tableModel.getValueAt(modelRow, 2).toString().split(" ")[0]);
                List<Long[]> etkilenenRandevular = yoneticiDAO.getDoktorGunlukAktifRandevuIds(hiddenId, tarih);

                yoneticiDAO.iptalEtDoktorGunlukRandevular(hiddenId, tarih);

for (Long[] rData : etkilenenRandevular) {
    Long rId = rData[0];
    Long hId = rData[1];
    
    String[] detaylar = yoneticiDAO.getRandevuDetaylariForBildirim(rId);
    
    if (detaylar != null) {
        String drAd = detaylar[0];
        String tarihSaat = detaylar[3];
        
        String detayliMesaj = String.format(
            "Sayın Hastamız, %s tarihindeki Dr. %s randevunuz, doktorun mesai değişikliği nedeniyle iptal edilmiştir.", 
            tarihSaat, drAd
        );
        
        iptalSubject.iptalBildir(rId, hId, hiddenId, detayliMesaj);
    }
}
                
                refreshYoneticiPlanliGörünümü("GELECEK");
                JOptionPane.showMessageDialog(this, "Tüm randevular iptal edildi ve hastalara bildirildi.");
            } else {
                if (yoneticiDAO.iptalEtRandevu(hiddenId)) {
                    Long hId = yoneticiDAO.getHastaIdByRandevu(hiddenId);
                    Long dId = yoneticiDAO.getDoktorIdByRandevu(hiddenId);
                    
                    if (dId != null && hId != null) {
                        iptalSubject.iptalBildir(hiddenId, hId, dId, "Randevunuz yönetici tarafından iptal edildi.");
                    }
                    
                    refreshDashboardStats();
                    refreshRandevularDetayli("planlandi");
                    JOptionPane.showMessageDialog(this, "İptal işlemi ve bildirim tamamlandı.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Hata: " + e.getMessage());
        }
    }

    private void refreshRandevularDetayli(String filtre) {
        try {
            tblRandevular.getColumnModel().getColumn(0).setMinWidth(150);
            tblRandevular.getColumnModel().getColumn(0).setMaxWidth(Integer.MAX_VALUE);
            tblRandevular.getColumnModel().getColumn(0).setPreferredWidth(150);

            List<String[]> list = yoneticiDAO.getRandevuDetayliListe(filtre);
            tableModel.setRowCount(0);
            for (String[] r : list) tableModel.addRow(r);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void refreshYoneticiPlanliGörünümü(String zamanDurumu) {
        try {
            tblRandevular.getColumnModel().getColumn(0).setMinWidth(0);
            tblRandevular.getColumnModel().getColumn(0).setMaxWidth(0);
            tblRandevular.getColumnModel().getColumn(0).setPreferredWidth(0);

            List<String[]> list = yoneticiDAO.getYoneticiPlanliDetayliListe(zamanDurumu);
            tableModel.setRowCount(0);
            for (String[] r : list) tableModel.addRow(r);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private JButton createStyledFilterBtn(String text, Color bg, String f) {
        JButton btn = new JButton(text);
        btn.setBackground(bg); btn.setForeground(Color.WHITE);
        btn.addActionListener(e -> {
            if (f.contains("yonetici_planli")) refreshYoneticiPlanliGörünümü(f.contains("gelecek") ? "GELECEK" : "GECMIS");
            else refreshRandevularDetayli(f);
        });
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(YoneticiEkrani::new);
    }
}