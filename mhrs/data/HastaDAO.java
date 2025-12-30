package tr.mhrs.data;

import tr.mhrs.model.AileHekimiBilgisi;
import java.sql.*;

public class HastaDAO {
    private Connection connection = DatabaseManager.getInstance().getConnection();

   public AileHekimiBilgisi aileHekimiGetir(Long hastaId) throws SQLException {
    String sql = "SELECT p.ad, p.soyad, s.ad AS birim_ad, s.telefon " +
                 "FROM Hasta h " +
                 "JOIN Profil p ON h.aile_hekimi_id = p.kullanici_id " +
                 "JOIN AileHekimi ah ON h.aile_hekimi_id = ah.doktor_id " +
                 "JOIN SaglikBirimi s ON ah.saglik_birimi_id = s.birim_id " +
                 "WHERE h.hasta_id = ?";
    
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setLong(1, hastaId);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                String adSoyad = rs.getString("ad") + " " + rs.getString("soyad");
                return new AileHekimiBilgisi(
                    adSoyad,
                    rs.getString("birim_ad"),
                    "Malatya Darende Ilıca Mah. Kabaşlar Mevkii", 
                    rs.getString("telefon") 
                );
            }
        }
    }
    return null; 
}
}