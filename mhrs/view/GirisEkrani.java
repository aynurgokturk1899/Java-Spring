package tr.mhrs.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL; 

public class GirisEkrani extends JFrame {

    private final Color MHRS_BLUE_DARK = new Color(0, 77, 153); 
    private final Color MHRS_GREEN_DARK = new Color(40, 167, 69); 
    private final Color LIGHT_GRAY_BG = Color.decode("#E0E0E0"); 
    
    private final int MOBILE_WIDTH = 450;
    private final int MOBILE_HEIGHT = 750;

    public GirisEkrani() {
        setTitle("MHRS Mobil");
        setSize(MOBILE_WIDTH, MOBILE_HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout()); 

        JPanel backgroundPanel = new JPanel(new BorderLayout());
        backgroundPanel.setBackground(LIGHT_GRAY_BG);

        JPanel centerContainer = new JPanel();
        centerContainer.setLayout(new BoxLayout(centerContainer, BoxLayout.Y_AXIS));
        centerContainer.setOpaque(false);
        centerContainer.setBorder(new EmptyBorder(40, 40, 40, 40)); 

        JLabel logoImage = new JLabel();
        logoImage.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoImage.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        URL logoURL = getClass().getClassLoader().getResource("assets/logo_mhrs.png");
        if (logoURL != null) {
             try {
                 ImageIcon icon = new ImageIcon(logoURL);
                 logoImage.setIcon(new ImageIcon(icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
             } catch (Exception e) {
                 logoImage.setText("⚕️ MHRS LOGO (Hata)");
                 logoImage.setFont(new Font("Segoe UI", Font.BOLD, 40));
                 logoImage.setForeground(MHRS_BLUE_DARK);
             }
        } else {
             logoImage.setText("️ MHRS");
             logoImage.setFont(new Font("Segoe UI", Font.BOLD, 40));
             logoImage.setForeground(MHRS_BLUE_DARK);
        }
        
        JLabel lblTitle = new JLabel("MHRS Mobil Randevu", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(MHRS_BLUE_DARK);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel imageHospital = new JLabel();
        imageHospital.setAlignmentX(Component.CENTER_ALIGNMENT);
        imageHospital.setBorder(new EmptyBorder(20, 0, 30, 0));
        
        URL imageURL = getClass().getClassLoader().getResource("assets/hastane.jpg");
        if (imageURL != null) {
             try {
                 ImageIcon originalIcon = new ImageIcon(imageURL);
                 Image originalImage = originalIcon.getImage();
                 Image resizedImage = originalImage.getScaledInstance(MOBILE_WIDTH - 80, 180, Image.SCALE_SMOOTH);
                 imageHospital.setIcon(new ImageIcon(resizedImage));
             } catch (Exception e) {
                 imageHospital.setText("(Hastane Resmi Yüklenemedi)");
                 imageHospital.setForeground(Color.RED);
             }
        }
        
        JButton btnHasta = createModernButton("Hasta Girişi", MHRS_GREEN_DARK);
        JButton btnDoktor = createModernButton(" Doktor Girişi", MHRS_BLUE_DARK);
        
        btnHasta.addActionListener(e -> {
            new HastaGirisPanel(this).setVisible(true);
            setVisible(false);
        });

        btnDoktor.addActionListener(e -> {
            new DoktorGirisPanel(this).setVisible(true);
            setVisible(false);
        });
        
        centerContainer.add(logoImage);
        centerContainer.add(lblTitle);
        centerContainer.add(imageHospital);
        centerContainer.add(Box.createVerticalStrut(20));
        centerContainer.add(btnHasta);
        centerContainer.add(Box.createVerticalStrut(15));
        centerContainer.add(btnDoktor);
        centerContainer.add(Box.createVerticalGlue()); 

        backgroundPanel.add(centerContainer, BorderLayout.CENTER);
        add(backgroundPanel, BorderLayout.CENTER);
    }
    
    private JButton createModernButton(String text, Color baseColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(300, 55));
        button.setMaximumSize(new Dimension(300, 55));
        
        button.setBackground(baseColor); 
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setOpaque(true);
        
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(baseColor.darker(), 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GirisEkrani().setVisible(true));
    }
}