package yonetici_app.data;

import yonetici_app.model.Doktor; 
import yonetici_app.model.Hastane;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoktorDAO {
    private final Connection connection = DatabaseManager.getInstance().getConnection();

    public List<Hastane> tumHastaneleriGetir() throws SQLException {
        List<Hastane> hastaneler = new ArrayList<>();
        String sql = "SELECT hastane_id, ad FROM Hastane ORDER BY ad";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                 hastaneler.add(new Hastane(rs.getLong("hastane_id"), rs.getString("ad")));
            }
        }
        return hastaneler;
    }


public List<Doktor> doktorlariHastaneIdIleGetir(Long hastaneId) throws SQLException {
    String sql = "SELECT d.doktor_id, p.ad, p.soyad, h.ad AS hastane_ad, b.ad AS bolum_ad, " +
                 "d.hastane_id, d.bolum_id FROM Doktor d " +
                 "LEFT JOIN Profil p ON d.doktor_id = p.kullanici_id " +
                 "LEFT JOIN Hastane h ON d.hastane_id = h.hastane_id " +
                 "LEFT JOIN Bolum b ON d.bolum_id = b.bolum_id " +
                 "WHERE d.hastane_id = ? ORDER BY p.ad ASC, p.soyad ASC"; 

    List<Doktor> sonuclar = new ArrayList<>();
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setLong(1, hastaneId);
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Doktor d = new Doktor();
                d.setDoktorId(rs.getLong("doktor_id"));
                d.setHastaneId(rs.getLong("hastane_id"));
                d.setBolumId(rs.getLong("bolum_id"));
                
                String ad = rs.getString("ad");
                String soyad = rs.getString("soyad");
                d.setAd(ad != null ? ad : "Doktor");
                d.setSoyad(soyad != null ? soyad : "(ID: " + rs.getLong("doktor_id") + ")");
                
                d.setHastaneAd(rs.getString("hastane_ad"));
                d.setBolumAd(rs.getString("bolum_ad") != null ? rs.getString("bolum_ad") : "Bölüm Yok");
                sonuclar.add(d);
            }
        }
    }
    return sonuclar;
}

    public List<Doktor> doktorAra(String aramaKriteri) throws SQLException {
        String sql = "SELECT d.doktor_id, p.ad, p.soyad, h.ad AS hastane_ad, b.ad AS bolum_ad, " +
                     "d.hastane_id, d.bolum_id FROM Doktor d " +
                     "LEFT JOIN Profil p ON d.doktor_id = p.kullanici_id " +
                     "LEFT JOIN Hastane h ON d.hastane_id = h.hastane_id " +
                     "LEFT JOIN Bolum b ON d.bolum_id = b.bolum_id " +
                     "WHERE h.ad ILIKE ? OR b.ad ILIKE ? OR p.ad ILIKE ? OR p.soyad ILIKE ?";
        
        List<Doktor> sonuclar = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            String kriter = "%" + aramaKriteri + "%";
            pstmt.setString(1, kriter); pstmt.setString(2, kriter);
            pstmt.setString(3, kriter); pstmt.setString(4, kriter);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Doktor d = new Doktor();
                    d.setDoktorId(rs.getLong("doktor_id"));
                    d.setAd(rs.getString("ad")); d.setSoyad(rs.getString("soyad"));
                    d.setHastaneAd(rs.getString("hastane_ad")); d.setBolumAd(rs.getString("bolum_ad"));
                    sonuclar.add(d);
                }
            }
        }
        return sonuclar;
    }
}