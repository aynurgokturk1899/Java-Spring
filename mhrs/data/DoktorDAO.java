package tr.mhrs.data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import tr.mhrs.model.Doktor;
import tr.mhrs.model.Hastane;
import tr.mhrs.model.Bolum;

public class DoktorDAO {
    private Connection connection = DatabaseManager.getInstance().getConnection();

    public String getDoktorAdSoyad(Long doktorId) {
        String sql = "SELECT ad, soyad FROM profil WHERE kullanici_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, doktorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("ad") + " " + rs.getString("soyad");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Doktor> doktorAra(String kriter) throws SQLException {
        List<Doktor> liste = new ArrayList<>();
        String sql = "SELECT d.doktor_id, d.hastane_id, d.bolum_id, p.ad, p.soyad, h.ad as hastane_ad, b.ad as bolum_ad " +
                     "FROM doktor d " +
                     "JOIN profil p ON d.doktor_id = p.kullanici_id " +
                     "JOIN hastane h ON d.hastane_id = h.hastane_id " +
                     "JOIN bolum b ON d.bolum_id = b.bolum_id " +
                     "WHERE p.ad ILIKE ? OR p.soyad ILIKE ? OR h.ad ILIKE ? OR b.ad ILIKE ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            String pattern = "%" + kriter + "%";
            for (int i = 1; i <= 4; i++) pstmt.setString(i, pattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Doktor d = new Doktor(rs.getLong("doktor_id"), rs.getLong("hastane_id"), rs.getLong("bolum_id"), "Uzman");
                    d.setAd(rs.getString("ad"));
                    d.setSoyad(rs.getString("soyad"));
                    d.setHastaneAd(rs.getString("hastane_ad"));
                    d.setBolumAd(rs.getString("bolum_ad"));
                    liste.add(d);
                }
            }
        }
        return liste;
    }

    public boolean doktorKaydet(Long doktorId, Long hastaneId, Long bolumId) throws SQLException {
        String sql = "INSERT INTO doktor (doktor_id, hastane_id, bolum_id) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, doktorId);
            pstmt.setLong(2, hastaneId);
            pstmt.setLong(3, bolumId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<Hastane> tumHastaneleriGetir() throws SQLException {
        List<Hastane> list = new ArrayList<>();
        String sql = "SELECT * FROM hastane ORDER BY ad ASC";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Hastane(rs.getLong("hastane_id"), rs.getString("ad")));
            }
        }
        return list;
    }

    public List<Bolum> tumBolumleriGetir() throws SQLException {
        List<Bolum> list = new ArrayList<>();
        String sql = "SELECT * FROM bolum ORDER BY ad ASC";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Bolum(rs.getLong("bolum_id"), rs.getString("ad")));
            }
        }
        return list;
    }
}