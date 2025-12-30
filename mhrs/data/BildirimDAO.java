package tr.mhrs.data;

import tr.mhrs.model.Bildirim;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BildirimDAO {
    private Connection connection = DatabaseManager.getInstance().getConnection();

    public boolean bildirimKaydet(Bildirim bildirim) throws SQLException {
        String sql = "INSERT INTO bildirim (kullanici_id, randevu_id, tur, icerik, gonderim_tarihi, durum) VALUES (?, ?, ?, ?, ?, ?)";
        
        String tamIcerik = "Konu: " + bildirim.getKonu() + "\n" + bildirim.getIcerik();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setLong(1, bildirim.getKullaniciId());
            
            if (bildirim.getRandevuId() != null) pstmt.setLong(2, bildirim.getRandevuId());
            else pstmt.setNull(2, Types.BIGINT);
            
            pstmt.setString(3, bildirim.getTur());
            pstmt.setString(4, tamIcerik);
            pstmt.setTimestamp(5, Timestamp.valueOf(bildirim.getGonderimTarihi()));
            pstmt.setString(6, "beklemede"); 
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) bildirim.setBildirimId(rs.getLong(1));
                }
            }
            return affectedRows > 0;
        }
    }

    public List<Bildirim> kullaniciBildirimleriniGetir(Long kullaniciId, boolean sadeceOkunmamis) throws SQLException {
        List<Bildirim> bildirimler = new ArrayList<>();
        String sql = "SELECT bildirim_id, kullanici_id, randevu_id, tur, icerik, gonderim_tarihi, durum FROM bildirim WHERE kullanici_id = ?";
        
        if (sadeceOkunmamis) {
            sql += " AND durum = 'beklemede'"; 
        }
        sql += " ORDER BY gonderim_tarihi DESC"; 

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, kullaniciId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String fullIcerik = rs.getString("icerik");
                    String konu = "Bildirim";
                    String icerik = fullIcerik;
                    
                    if (fullIcerik.startsWith("Konu: ")) {
                        int index = fullIcerik.indexOf("\n");
                        if (index != -1) {
                            konu = fullIcerik.substring(6, index).trim();
                            icerik = fullIcerik.substring(index + 1).trim();
                        }
                    }
                    
                    Bildirim b = new Bildirim(rs.getLong("kullanici_id"), rs.getLong("randevu_id"), rs.getString("tur"), konu, icerik);
                    b.setBildirimId(rs.getLong("bildirim_id"));
                    b.setDurum(rs.getString("durum"));
                    b.setGonderimTarihi(rs.getTimestamp("gonderim_tarihi").toLocalDateTime());
                    
                    bildirimler.add(b);
                }
            }
        }
        return bildirimler;
    }
    
    public int okunmamisBildirimSayisi(Long kullaniciId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM bildirim WHERE kullanici_id = ? AND durum = 'beklemede'";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, kullaniciId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }
    
    public boolean bildirimleriOkunduYap(Long kullaniciId) throws SQLException {
        String sql = "UPDATE bildirim SET durum = 'gonderildi' WHERE kullanici_id = ? AND durum = 'beklemede'";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, kullaniciId);
            return pstmt.executeUpdate() > 0;
        }
    }
}