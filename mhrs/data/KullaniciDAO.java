package tr.mhrs.data;

import tr.mhrs.model.Kullanici;
import java.sql.*;

public class KullaniciDAO {
    private Connection connection = DatabaseManager.getInstance().getConnection();

    public boolean kullaniciKaydet(Kullanici kullanici) throws SQLException {
        String sql = "INSERT INTO kullanici (tc_kimlik_no, sifre_hash, eposta, rol_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, kullanici.getTcKimlikNo());
            pstmt.setString(2, kullanici.getSifreHash()); 
            pstmt.setString(3, kullanici.getEposta());
            pstmt.setLong(4, kullanici.getRolId()); 
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) kullanici.setKullaniciId(rs.getLong(1));
                }
            }
            return affectedRows > 0;
        }
    }

    public boolean profilKaydet(Long kullaniciId, String ad, String soyad) throws SQLException {
        String sql = "INSERT INTO profil (kullanici_id, ad, soyad) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, kullaniciId);
            pstmt.setString(2, ad);
            pstmt.setString(3, soyad);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean hastaKaydet(Long kullaniciId) throws SQLException {
        String sql = "INSERT INTO hasta (hasta_id) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, kullaniciId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean kullaniciAdiVarMi(String tcKimlikNo) throws SQLException {
        String sql = "SELECT count(*) FROM kullanici WHERE tc_kimlik_no = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, tcKimlikNo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    // YENİ: E-posta çakışmasını önlemek için eklendi
    public boolean epostaVarMi(String eposta) throws SQLException {
        String sql = "SELECT count(*) FROM kullanici WHERE eposta = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, eposta);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }
public boolean iletisimGuncelle(Long id, String eposta, String telefon) throws SQLException {
    String sql = "UPDATE kullanici SET eposta = ?, telefon = ? WHERE kullanici_id = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setString(1, eposta);
        pstmt.setString(2, telefon);
        pstmt.setLong(3, id);
        
        int sonuc = pstmt.executeUpdate();
        
        System.out.println("Güncellenen Satır Sayısı: " + sonuc + " | ID: " + id);
        
        return sonuc > 0;
    } catch (SQLException e) {
        System.err.println("SQL Hatası: " + e.getMessage());
        throw e;
    }
}

public Kullanici kullaniciGetir(String tcKimlikNo) throws SQLException {
    String sql = "SELECT k.kullanici_id, k.tc_kimlik_no, k.sifre_hash, k.eposta, k.rol_id, p.ad, p.soyad " +
                 "FROM kullanici k " +
                 "LEFT JOIN profil p ON k.kullanici_id = p.kullanici_id " +
                 "WHERE k.tc_kimlik_no = ?";
                 
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setString(1, tcKimlikNo);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                Kullanici k = new Kullanici();
                k.setKullaniciId(rs.getLong("kullanici_id"));
                k.setTcKimlikNo(rs.getString("tc_kimlik_no"));
                k.setSifreHash(rs.getString("sifre_hash"));
                k.setEposta(rs.getString("eposta"));
                k.setRolId(rs.getLong("rol_id"));
                
                k.setAd(rs.getString("ad"));
                k.setSoyad(rs.getString("soyad"));
                
                return k;
            }
        }
    }
    return null;
}}