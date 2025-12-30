package tr.mhrs.view;

import tr.mhrs.model.Doktor;
import tr.mhrs.service.RandevuService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.awt.event.WindowAdapter; 
import java.awt.event.WindowEvent; 

public class RandevuAramaSonucEkrani extends JFrame {

    private final RandevuService randevuService = new RandevuService();
    private final String aramaKriteri;
    private final List<Doktor> doktorSonuclari;
    
    private final Long aktifHastaId; 
    
    private final JFrame callingFrame; 

    private JPanel sonucListesiPanel;

    public RandevuAramaSonucEkrani(String kriter, Long aktifHastaId, JFrame callingFrame) {
        super("MHRS | Randevu Arama Sonuçları");
        this.aramaKriteri = kriter;
        this.aktifHastaId = aktifHastaId; 
        this.callingFrame = callingFrame; 
        
        this.doktorSonuclari = randevuService.randevuAra(kriter); 

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 700);
        setLayout(new BorderLayout());

        Color MHRS_BLUE = new Color(0, 102, 204);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel header = new JLabel("Arama Sonuçları: '" + kriter + "' (" + doktorSonuclari.size() + " Hekim bulundu)", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setForeground(MHRS_BLUE.darker());
        mainPanel.add(header, BorderLayout.NORTH);

        sonucListesiPanel = new JPanel();
        sonucListesiPanel.setLayout(new BoxLayout(sonucListesiPanel, BoxLayout.Y_AXIS));
        sonucListesiPanel.setBackground(new Color(245, 245, 245));
        JScrollPane scrollPane = new JScrollPane(sonucListesiPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        listeyiDoldur();
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                if (RandevuAramaSonucEkrani.this.callingFrame != null) {
                    RandevuAramaSonucEkrani.this.callingFrame.setVisible(true); 
                }
            }
        });
        
        add(mainPanel);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void listeyiDoldur() {
        sonucListesiPanel.removeAll();

        if (doktorSonuclari.isEmpty()) {
            JLabel noResult = new JLabel("Aradığınız kritere uygun doktor bulunamadı.", SwingConstants.CENTER);
            noResult.setForeground(Color.GRAY);
            sonucListesiPanel.add(noResult);
            return;
        }

        for (Doktor doktor : doktorSonuclari) {
            sonucListesiPanel.add(createDoktorKarti(doktor));
            sonucListesiPanel.add(Box.createVerticalStrut(10));
        }
        sonucListesiPanel.revalidate();
        sonucListesiPanel.repaint();
    }

    private JPanel createDoktorKarti(Doktor doktor) {
        
        
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), 
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JPanel detailPanel = new JPanel(new GridLayout(2, 1));
        detailPanel.setBackground(Color.WHITE);
        
        JLabel lblAd = new JLabel("Dr. " + doktor.getAd() + " " + doktor.getSoyad());
        lblAd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JLabel lblBolum = new JLabel("Bölüm: " + doktor.getBolumAd() + " | Hastane: " + doktor.getHastaneAd());
        lblBolum.setForeground(Color.DARK_GRAY);
        
        detailPanel.add(lblAd);
        detailPanel.add(lblBolum);
        card.add(detailPanel, BorderLayout.CENTER);

        JButton btnRandevuAl = new JButton("Randevu Al");
        btnRandevuAl.setBackground(new Color(40, 167, 69)); 
        btnRandevuAl.setForeground(Color.WHITE);
        btnRandevuAl.setPreferredSize(new Dimension(120, 40));
        
        btnRandevuAl.addActionListener(e -> {
            
            Long hastaId = this.aktifHastaId;
            
            new RandevuSaatiSecimEkrani(hastaId, doktor, this).setVisible(true); 
            
            this.setVisible(false); 
        });
        
        card.add(btnRandevuAl, BorderLayout.EAST);
        
        return card;
    }

    public JFrame getCallingFrame() {
        return callingFrame;
    }
}