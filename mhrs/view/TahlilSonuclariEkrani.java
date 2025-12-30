package tr.mhrs.view;

import tr.mhrs.data.TibbiKayitDAO;
import tr.mhrs.model.TibbiKayit;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class TahlilSonuclariEkrani extends JPanel {
    private final Long hastaId;
    private final JPanel listPanel;
    private final TibbiKayitDAO tibbiKayitDAO = new TibbiKayitDAO();
    
    private final Color MHRS_BLUE = new Color(0, 77, 153);
    private final Color BG_GRAY = new Color(240, 242, 245);
    private final Color BORDER_COLOR = new Color(225, 225, 225);

    public TahlilSonuclariEkrani(Long hastaId) {
        this.hastaId = hastaId;
        setLayout(new BorderLayout());
        setBackground(BG_GRAY);

        JLabel lblTitle = new JLabel("Laboratuvar Sonuçlarım", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(MHRS_BLUE);
        lblTitle.setBorder(new EmptyBorder(25, 0, 20, 0));
        add(lblTitle, BorderLayout.NORTH);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(BG_GRAY);
        listPanel.setBorder(new EmptyBorder(0, 0, 0, 0)); 

        verileriYukle();

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void verileriYukle() {
        listPanel.removeAll();
        List<TibbiKayit> kayitlar = tibbiKayitDAO.getTahlilSonuclari(hastaId);

        if (kayitlar == null || kayitlar.isEmpty()) {
            JLabel lblEmpty = new JLabel("Henüz tahlil sonucunuz bulunmamaktadır.", SwingConstants.CENTER);
            lblEmpty.setForeground(Color.GRAY);
            lblEmpty.setBorder(new EmptyBorder(50, 0, 0, 0));
            listPanel.add(lblEmpty);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            for (TibbiKayit kayit : kayitlar) {
                String tarihStr = sdf.format(kayit.getOlusturmaTarihi());
                listPanel.add(createTahlilKarti(kayit, tarihStr));
                listPanel.add(Box.createVerticalStrut(1));
            }
        }
        listPanel.revalidate();
        listPanel.repaint();
    }

 private JPanel createTahlilKarti(TibbiKayit kayit, String tarih) {
    JPanel card = new JPanel(new BorderLayout(15, 0));
    card.setBackground(Color.WHITE);
    card.setMaximumSize(new Dimension(420, 100)); 
    card.setPreferredSize(new Dimension(420, 100));
    card.setAlignmentX(Component.CENTER_ALIGNMENT); 

    card.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
        BorderFactory.createEmptyBorder(10, 15, 10, 10) 
    ));

    JPanel infoPanel = new JPanel();
    infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
    infoPanel.setOpaque(false);
    
    JLabel lblAd = new JLabel("<html><body style='width: 220px;'><b>" + kayit.getAciklama() + "</b></body></html>");
    lblAd.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    
    JLabel lblDetay = new JLabel(tarih + " | Merkez Laboratuvarı");
    lblDetay.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    lblDetay.setForeground(Color.GRAY);

    infoPanel.add(Box.createVerticalGlue());
    infoPanel.add(lblAd);
    infoPanel.add(Box.createVerticalStrut(5));
    infoPanel.add(lblDetay);
    infoPanel.add(Box.createVerticalGlue());

    JButton btnDetay = new JButton("Görüntüle");
    btnDetay.addActionListener(e -> {
        List<String[]> detaylar = tibbiKayitDAO.getTahlilDetaylari(kayit.getKayitId());
        showTahlilDetay(kayit.getAciklama(), tarih, detaylar);
    });
    btnDetay.setBackground(MHRS_BLUE);
    btnDetay.setForeground(Color.WHITE);
    btnDetay.setPreferredSize(new Dimension(110, 35));
    btnDetay.setFocusPainted(false);

    JPanel buttonWrapper = new JPanel(new GridBagLayout());
    buttonWrapper.setOpaque(false);
    buttonWrapper.add(btnDetay);

    card.add(infoPanel, BorderLayout.CENTER);
    card.add(buttonWrapper, BorderLayout.EAST);

    return card;
}

    private void showTahlilDetay(String ad, String tarih, List<String[]> detaylar) {
        Window parentWindow = SwingUtilities.windowForComponent(this);
        JDialog detayDialog = new JDialog(parentWindow, "Tahlil Raporu", Dialog.ModalityType.APPLICATION_MODAL);
        detayDialog.setSize(380, 500);
        detayDialog.setLocationRelativeTo(this);
        detayDialog.setLayout(new BorderLayout());

        JPanel head = new JPanel(new BorderLayout());
        head.setBackground(MHRS_BLUE);
        JLabel title = new JLabel("<html><center>" + ad + "</center></html>", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setBorder(new EmptyBorder(15, 10, 15, 10));
        head.add(title);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(25, 25, 25, 25));

        content.add(new JLabel("<html><font color='gray'>İnceleme Tarihi:</font> <b>" + tarih + "</b></html>"));
        content.add(Box.createVerticalStrut(20));
        content.add(new JSeparator());
        content.add(Box.createVerticalStrut(15));
        
        if (detaylar.isEmpty()) {
            content.add(new JLabel("Bulgu kaydı bulunamadı."));
        } else {
            for (String[] d : detaylar) {
                content.add(createRow(d[0], d[1], d[2], d[3]));
                content.add(Box.createVerticalStrut(12));
            }
        }

        JButton btnKapat = new JButton("Kapat");
        btnKapat.setBackground(new Color(60, 60, 60));
        btnKapat.setForeground(Color.WHITE);
        btnKapat.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnKapat.setPreferredSize(new Dimension(0, 45));
        btnKapat.addActionListener(e -> detayDialog.dispose());

        detayDialog.add(head, BorderLayout.NORTH);
        detayDialog.add(new JScrollPane(content), BorderLayout.CENTER);
        detayDialog.add(btnKapat, BorderLayout.SOUTH);
        detayDialog.setVisible(true);
    }

    private JPanel createRow(String t, String s, String r, String d) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(600, 40));
        
        JLabel test = new JLabel("<html>" + t + "<br><small color='gray'>Ref: " + r + "</small></html>");
        JLabel sonuc = new JLabel(s, SwingConstants.RIGHT);
        sonuc.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        if (d != null && (d.equalsIgnoreCase("Yüksek") || d.equalsIgnoreCase("Düşük"))) {
            sonuc.setForeground(Color.RED);
        } else {
            sonuc.setForeground(new Color(40, 167, 69));
        }
        
        row.add(test, BorderLayout.CENTER);
        row.add(sonuc, BorderLayout.EAST);
        return row;
    }
}