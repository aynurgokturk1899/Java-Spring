package tr.mhrs.view;

import tr.mhrs.data.DoktorDAO;
import tr.mhrs.model.Hastane;
import tr.mhrs.model.Bolum;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RandevuAramaEkrani extends JFrame {

    private final DoktorDAO doktorDAO = new DoktorDAO();
    private final Long aktifHastaId;
    private final JFrame callingFrame;
    private final String mode;

    private final Color MHRS_BLUE = new Color(0, 77, 153);
    private final Color BG_GRAY = new Color(240, 242, 245);

    public RandevuAramaEkrani(Long aktifHastaId, JFrame callingFrame) {
        this(aktifHastaId, callingFrame, "MENU");
    }

    public RandevuAramaEkrani(Long aktifHastaId, JFrame callingFrame, String mode) {
        super("MHRS | Randevu");
        this.aktifHastaId = aktifHastaId;
        this.callingFrame = callingFrame;
        this.mode = mode;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 750);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        add(createHeaderBar(), BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(BG_GRAY);

        if (mode.equals("MENU")) {
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
            
            mainPanel.add(createOptionCard("Hastaneye Göre", "Hastane seçerek randevu arayın", "🏥", "HASTANE"));
            mainPanel.add(Box.createVerticalStrut(15));
            mainPanel.add(createOptionCard("Polikliniğe Göre (Bölüm)", "Tıbbi branş seçerek randevu arayın", "➕", "BOLUM"));
            mainPanel.add(Box.createVerticalStrut(15));
            mainPanel.add(createOptionCard("Doktora Göre (Arama)", "Doktor adı veya uzmanlık alanı arayın", "🔍", "ARAMA"));
            add(mainPanel, BorderLayout.CENTER);
        } else {
            add(createSelectionListPanel(), BorderLayout.CENTER);
        }

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (callingFrame != null) callingFrame.setVisible(true);
            }
        });
    }

    private JPanel createHeaderBar() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(MHRS_BLUE);
        header.setPreferredSize(new Dimension(450, 60));
        header.setBorder(new EmptyBorder(0, 15, 0, 15));

        JLabel lblBack = new JLabel("←");
        lblBack.setForeground(Color.WHITE);
        lblBack.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblBack.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (mode.equals("MENU")) {
                    callingFrame.setVisible(true);
                } else {
                    new RandevuAramaEkrani(aktifHastaId, callingFrame, "MENU").setVisible(true);
                }
                dispose();
            }
        });

        String title = mode.equals("HASTANE") ? "Hastane Seçiniz" : (mode.equals("BOLUM") ? "Bölüm Seçiniz" : "Randevu Alma");
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));

        header.add(lblBack, BorderLayout.WEST);
        header.add(lblTitle, BorderLayout.CENTER);
        return header;
    }

    private JPanel createOptionCard(String title, String subTitle, String icon, String targetMode) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblIcon = new JLabel(icon, SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 30));
        lblIcon.setPreferredSize(new Dimension(60, 60));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(new JLabel("<html><b>" + title + "</b></html>"));
        JLabel lblSub = new JLabel(subTitle);
        lblSub.setForeground(Color.GRAY);
        textPanel.add(lblSub);

        card.add(lblIcon, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

card.addMouseListener(new MouseAdapter() {
    @Override
    public void mouseClicked(MouseEvent e) {
        if (targetMode.equals("ARAMA")) {
            new RandevuAramaMobil(aktifHastaId, RandevuAramaEkrani.this).setVisible(true);
            setVisible(false);
        } else if (targetMode.equals("HASTANE")) {
            new RandevuAramaEkrani(aktifHastaId, callingFrame, "HASTANE").setVisible(true);
            dispose();
        } else if (targetMode.equals("BOLUM")) {
            new RandevuAramaEkrani(aktifHastaId, callingFrame, "BOLUM").setVisible(true);
            dispose();
        }
    }
});        return card;
    }

    private JScrollPane createSelectionListPanel() {
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(BG_GRAY);
        listPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        try {
            if (mode.equals("HASTANE")) {
                List<Hastane> hastaneler = doktorDAO.tumHastaneleriGetir();
                for (Hastane h : hastaneler) {
                    listPanel.add(createListItem(h.getAd(), h.getHastaneId()));
                    listPanel.add(Box.createVerticalStrut(8));
                }
            } else if (mode.equals("BOLUM")) {
                List<Bolum> bolumler = doktorDAO.tumBolumleriGetir();
                for (Bolum b : bolumler) {
                    listPanel.add(createListItem(b.getAd(), b.getBolumId()));
                    listPanel.add(Box.createVerticalStrut(8));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        return scroll;
    }

    private JPanel createListItem(String ad, Long id) {
        JPanel item = new JPanel(new BorderLayout());
        item.setBackground(Color.WHITE);
        item.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));

        item.add(new JLabel(ad), BorderLayout.CENTER);
        item.add(new JLabel(">"), BorderLayout.EAST);

        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new RandevuAramaSonucEkrani(ad, aktifHastaId, RandevuAramaEkrani.this).setVisible(true);
                setVisible(false);
            }
        });
        return item;
    }
}