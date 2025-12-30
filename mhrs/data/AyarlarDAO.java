package tr.mhrs.data;

import tr.mhrs.model.KullaniciAyarlari;
import java.sql.*;

public class AyarlarDAO {
    private Connection connection = DatabaseManager.getInstance().getConnection();

    public KullaniciAyarlari ayarlariGetir(Long kullaniciId) throws SQLException {

        String sql = "SELECT * FROM ayarlar WHERE kullanici_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, kullaniciId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new KullaniciAyarlari(
                        rs.getLong("kullanici_id"),
                        rs.getString("tema"),
                        rs.getString("dil"),
                        rs.getString("bildirim_tercihi")
                    );
                }
            }
        }
        return new KullaniciAyarlari(kullaniciId, "Açık", "Türkçe", "Hepsi");
    }

   public boolean ayarlariKaydet(KullaniciAyarlari ayar) throws SQLException {
    String checkSql = "SELECT COUNT(*) FROM ayarlar WHERE kullanici_id = ?";
    boolean exists = false;
    
    try (PreparedStatement pstmt = connection.prepareStatement(checkSql)) {
        pstmt.setLong(1, ayar.getKullaniciId());
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) exists = rs.getInt(1) > 0;
        }
    }

    String sql;
    if (exists) {
        sql = "UPDATE ayarlar SET tema = ?, dil = ?, bildirim_tercihi = ?, guncellenme_tarihi = CURRENT_TIMESTAMP WHERE kullanici_id = ?";
    } else {
        sql = "INSERT INTO ayarlar (tema, dil, bildirim_tercihi, kullanici_id, guncellenme_tarihi) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
    }

    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setString(1, ayar.getTema());
        pstmt.setString(2, ayar.getDil());
        pstmt.setString(3, ayar.getBildirimTercihi());
        pstmt.setLong(4, ayar.getKullaniciId());
        return pstmt.executeUpdate() > 0;
    }
}
}