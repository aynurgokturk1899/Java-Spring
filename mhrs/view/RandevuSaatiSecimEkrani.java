package tr.mhrs.view;

import tr.mhrs.model.Doktor;
import tr.mhrs.service.RandevuService;
import tr.mhrs.service.SistemAyarService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter; 
import java.awt.event.WindowEvent; 
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RandevuSaatiSecimEkrani extends JFrame {

    private final Long aktifHastaId;
    private final Doktor seciliDoktor;
    private final RandevuService randevuService = new RandevuService();
    private final SistemAyarService sistemAyarService = new SistemAyarService(); 
    private final JFrame callingFrame; 
    
    private final int MAX_CALENDAR_RANGE_DAYS; 
    private LocalDate initialDate; 
    
    private LocalDate seciliTarih;
    private JButton btnOncekiGun, btnSonrakiGun;
    private JLabel lblTarihGosterim;
    private JPanel saatListPanel; 
    private JPanel saatGridPanel; 
    
    private final Color MHRS_GREEN = new Color(40, 167, 69);
    private final Color MHRS_BLUE = new Color(0, 102, 204);

    public RandevuSaatiSecimEkrani(Long hastaId, Doktor doktor, JFrame callingFrame) {
        super("Randevu Saati Seçimi: Dr. " + doktor.getAd() + " " + doktor.getSoyad());
        this.aktifHastaId = hastaId;
        this.seciliDoktor = doktor;
        this.callingFrame = callingFrame; 
        
        this.MAX_CALENDAR_RANGE_DAYS = sistemAyarService.getMaxRandevuGunSayisi();
        
        this.seciliTarih = findNextWorkingDay(LocalDate.now());
        this.initialDate = this.seciliTarih; 
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(480, 650); 
        setLayout(new BorderLayout());
        
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(new Color(245, 245, 245));
        
        saatListPanel = new JPanel();
        saatListPanel.setLayout(new BoxLayout(saatListPanel, BoxLayout.Y_AXIS)); 
        saatListPanel.setBackground(new Color(245, 245, 245));
        saatListPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); 
        
        JScrollPane scrollPane = new JScrollPane(saatListPanel);
        scrollPane.setBorder(null); 
        
        mainContentPanel.add(createCalendarNav(), BorderLayout.NORTH); 
        mainContentPanel.add(scrollPane, BorderLayout.CENTER); 

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(mainContentPanel, BorderLayout.CENTER);
        
        updateSaatListesi();
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                if (RandevuSaatiSecimEkrani.this.callingFrame != null) {
                    RandevuSaatiSecimEkrani.this.callingFrame.setVisible(true); 
                }
            }
        });

        setLocationRelativeTo(null);
    }
    
    private int getWorkingDayOffset(LocalDate startDate, LocalDate targetDate) {
        if (targetDate.isBefore(startDate)) return 0;
        
        int offset = 0;
        LocalDate current = startDate;
        while (!current.isAfter(targetDate)) {
            if (current.getDayOfWeek() != DayOfWeek.SATURDAY && current.getDayOfWeek() != DayOfWeek.SUNDAY) {
                offset++;
            }
            if (current.isEqual(targetDate)) break;
            current = current.plusDays(1);
        }
        return offset; 
    }
    
    private LocalDate findNextWorkingDay(LocalDate start) {
        LocalDate nextDay = start;
        do {
            nextDay = nextDay.plusDays(1);
        } while (nextDay.getDayOfWeek() == DayOfWeek.SATURDAY || nextDay.getDayOfWeek() == DayOfWeek.SUNDAY);
        return nextDay;
    }
    
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 5));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(Color.WHITE);
        
        JLabel lblTitle = new JLabel("Randevu Al: " + seciliDoktor.getBolumAd(), SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(MHRS_BLUE);
        
        JLabel lblDoktor = new JLabel("Dr. " + seciliDoktor.getAd() + " " + seciliDoktor.getSoyad() + 
                                      " (" + seciliDoktor.getHastaneAd() + ")", SwingConstants.CENTER);
        lblDoktor.setForeground(Color.DARK_GRAY);
        
        panel.add(lblTitle);
        panel.add(lblDoktor);
        return panel;
    }

    private JPanel createCalendarNav() {
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        navPanel.setBackground(new Color(230, 230, 230));

        btnOncekiGun = new JButton("<< Önceki");
        btnSonrakiGun = new JButton("Sonraki >>");
        lblTarihGosterim = new JLabel("", SwingConstants.CENTER);
        lblTarihGosterim.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTarihGosterim.setForeground(Color.BLACK);
        
        styleNavButton(btnOncekiGun);
        styleNavButton(btnSonrakiGun);

        btnOncekiGun.addActionListener(e -> navigateDay(-1));
        btnSonrakiGun.addActionListener(e -> navigateDay(1));

        navPanel.add(btnOncekiGun, BorderLayout.WEST);
        navPanel.add(lblTarihGosterim, BorderLayout.CENTER);
        navPanel.add(btnSonrakiGun, BorderLayout.EAST);
        
        return navPanel;
    }
    
    private void styleNavButton(JButton button) {
        button.setBackground(Color.WHITE);
        button.setForeground(MHRS_BLUE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(100, 35));
        button.setBorder(BorderFactory.createLineBorder(MHRS_BLUE, 1));
    }

    private void navigateDay(int days) {
        LocalDate hedefTarih = seciliTarih;
        
        for (int i = 0; i < Math.abs(days); i++) {
            hedefTarih = hedefTarih.plusDays(days > 0 ? 1 : -1);
            
            while (hedefTarih.getDayOfWeek() == DayOfWeek.SATURDAY || hedefTarih.getDayOfWeek() == DayOfWeek.SUNDAY) {
                hedefTarih = hedefTarih.plusDays(days > 0 ? 1 : -1);
            }
        }

        if (hedefTarih.isBefore(initialDate)) {
            JOptionPane.showMessageDialog(this, "Sadece " + initialDate.format(DateTimeFormatter.ofPattern("dd MMMM")) + " sonrası için randevu alabilirsiniz.", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int workingDayOffset = getWorkingDayOffset(initialDate, hedefTarih);
        
        if (workingDayOffset > MAX_CALENDAR_RANGE_DAYS) {
            JOptionPane.showMessageDialog(this, MAX_CALENDAR_RANGE_DAYS + " iş gününden sonrası için randevu gösterilmemektedir (Yönetici kısıtlaması).", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        seciliTarih = hedefTarih;
        updateSaatListesi();
    }
    
    private void updateSaatListesi() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy");
        lblTarihGosterim.setText(seciliTarih.format(formatter).toUpperCase());
        
        saatListPanel.removeAll();
        
        List<LocalDateTime> musaitSaatler = randevuService.getMusaitSaatler(seciliDoktor.getDoktorId(), seciliTarih);
        
        if (musaitSaatler.isEmpty()) {
            JLabel noResult = new JLabel("Seçilen günde müsait saat bulunmamaktadır.", SwingConstants.CENTER);
            noResult.setAlignmentX(Component.CENTER_ALIGNMENT);
            noResult.setForeground(Color.GRAY.darker());
            noResult.setBorder(new EmptyBorder(50, 0, 0, 0));
            saatListPanel.add(noResult);
        } else {
            saatGridPanel = new JPanel(new GridLayout(0, 4, 8, 8)); 
            saatGridPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            saatGridPanel.setBackground(new Color(245, 245, 245));
            
            for (LocalDateTime saat : musaitSaatler) {
                JButton btnSaat = new JButton(saat.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
                btnSaat.setBackground(MHRS_GREEN.darker()); 
                btnSaat.setForeground(Color.WHITE);
                btnSaat.setFont(new Font("Segoe UI", Font.BOLD, 12));
                btnSaat.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btnSaat.setFocusPainted(false);
                btnSaat.setBorder(BorderFactory.createLineBorder(MHRS_GREEN.darker().darker(), 2));

                btnSaat.addActionListener(e -> {
                    try {
                        randevuAlIslemi(saat);
                    } catch (SQLException ex) {
                        Logger.getLogger(RandevuSaatiSecimEkrani.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
                
                saatGridPanel.add(btnSaat);
            }
            saatListPanel.add(saatGridPanel);
        }
        
        saatListPanel.revalidate();
        saatListPanel.repaint();
    }
    
    private void randevuAlIslemi(LocalDateTime secilenSaat) throws SQLException {
        int onay = JOptionPane.showConfirmDialog(this, 
            "Dr. " + seciliDoktor.getAd() + " " + seciliDoktor.getSoyad() + 
            " için " + secilenSaat.format(DateTimeFormatter.ofPattern("dd MMMM HH:mm")) + 
            " saatine randevu almak istediğinizden emin misiniz?", 
            "Randevu Onayı", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (onay == JOptionPane.YES_OPTION) {
            boolean basarili = randevuService.randevuAl(
                aktifHastaId, 
                seciliDoktor.getDoktorId(), 
                seciliDoktor.getBolumId(), 
                secilenSaat
            );

            if (basarili) {
                JOptionPane.showMessageDialog(this, "Randevu başarıyla oluşturuldu!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                
                if (callingFrame != null) {
                    if (callingFrame instanceof RandevuAramaSonucEkrani) {
                        JFrame grandCallingFrame = ((RandevuAramaSonucEkrani) callingFrame).getCallingFrame();
                        
                        if (grandCallingFrame instanceof HastaAnaMenu) {
                             ((HastaAnaMenu) grandCallingFrame).loadYaklasanRandevular(); 
                             grandCallingFrame.setVisible(true);
                        } else {
                             callingFrame.setVisible(true); 
                        }
                    } else if (callingFrame instanceof HastaAnaMenu) {
                         ((HastaAnaMenu) callingFrame).loadYaklasanRandevular(); 
                         callingFrame.setVisible(true); 
                    }
                }
                this.dispose(); 
            } else {
                JOptionPane.showMessageDialog(this, "Randevu oluşturulamadı. Lütfen tekrar deneyin.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}