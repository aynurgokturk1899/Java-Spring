package tr.mhrs.data;

import tr.mhrs.model.TibbiKayit;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TibbiKayitDAO {
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:postgresql://localhost:4444/MHRSProje3", "postgres", "aynur");
    }

public List<TibbiKayit> getTahlilSonuclari(Long hastaId) {
    List<TibbiKayit> list = new ArrayList<>();
  
    String sql = "SELECT * FROM tibbi_kayit WHERE hasta_id = ? AND kayit_tipi = 'laboratuvar' ORDER BY olusturma_tarihi DESC";

    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setLong(1, hastaId);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            TibbiKayit kayit = new TibbiKayit();
            kayit.setKayitId(rs.getLong("kayit_id"));
            kayit.setKayitTipi(rs.getString("kayit_tipi"));
            kayit.setAciklama(rs.getString("aciklama"));
            kayit.setOlusturma_tarihi(rs.getTimestamp("olusturma_tarihi"));
            list.add(kayit);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return list;
}
public List<String[]> getTahlilDetaylari(Long kayitId) {
    List<String[]> detaylar = new ArrayList<>();
    String sql = "SELECT test_adi, sonuc_degeri, referans_araligi, durum FROM tahlil_sonuclari WHERE kayit_id = ?";
    
    try (Connection conn = getConnection(); 
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setLong(1, kayitId);
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            detaylar.add(new String[]{
                rs.getString("test_adi"),
                rs.getString("sonuc_degeri"),
                rs.getString("referans_araligi"),
                rs.getString("durum")
            });
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return detaylar;
}
}