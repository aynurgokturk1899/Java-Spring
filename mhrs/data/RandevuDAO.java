package tr.mhrs.data;

import tr.mhrs.model.RandevuDetay;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RandevuDAO {
    private Connection connection;

    public RandevuDAO() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

public boolean durumGuncelle(Long randevuId, String yeniDurum) throws SQLException {
    String sql = "UPDATE randevu SET durum = ? WHERE randevu_id = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setString(1, yeniDurum);
        pstmt.setLong(2, randevuId);
        
        int affectedRows = pstmt.executeUpdate();
        System.out.println("Durum Güncellendi: " + (affectedRows > 0) + " | Randevu ID: " + randevuId); // Debug için
        return affectedRows > 0;
    }
}
    public List<RandevuDetay> getGecmisRandevular(Long hastaId) throws SQLException {
        String sql = "SELECT r.*, p.ad AS doktor_ad, p.soyad AS doktor_soyad, h.ad AS hastane_ad, b.ad AS bolum_ad " +
                     "FROM randevu r JOIN profil p ON r.doktor_id = p.kullanici_id " +
                     "JOIN doktor d ON r.doktor_id = d.doktor_id JOIN hastane h ON d.hastane_id = h.hastane_id " +
                     "JOIN bolum b ON r.bolum_id = b.bolum_id " +
                     "WHERE r.hasta_id = ? AND (r.randevu_saati < NOW() OR r.durum != 'planlandi') " +
                     "ORDER BY r.randevu_saati DESC";
        return executeRandevuQuery(sql, hastaId);
    }

    public List<RandevuDetay> yaklasanRandevulariGetir(Long hastaId) throws SQLException {
        String sql = "SELECT r.*, p.ad AS doktor_ad, p.soyad AS doktor_soyad, h.ad AS hastane_ad, b.ad AS bolum_ad " +
                     "FROM randevu r JOIN profil p ON r.doktor_id = p.kullanici_id " +
                     "JOIN doktor d ON r.doktor_id = d.doktor_id JOIN hastane h ON d.hastane_id = h.hastane_id " +
                     "JOIN bolum b ON r.bolum_id = b.bolum_id " +
                     "WHERE r.hasta_id = ? AND r.randevu_saati >= NOW() AND r.durum = 'planlandi' " +
                     "ORDER BY r.randevu_saati ASC";
        return executeRandevuQuery(sql, hastaId);
    }

    public boolean randevuIptal(Long randevuId) throws SQLException {
        String sql = "UPDATE randevu SET durum = 'iptal' WHERE randevu_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, randevuId);
            return pstmt.executeUpdate() > 0;
        }
    }

   public RandevuDetay getRandevuDetayById(Long randevuId) throws SQLException {
    String sql = "SELECT r.*, p.ad AS doktor_ad, p.soyad AS doktor_soyad, h.ad AS hastane_ad, b.ad AS bolum_ad " +
                 "FROM randevu r JOIN profil p ON r.doktor_id = p.kullanici_id " +
                 "JOIN doktor d ON r.doktor_id = d.doktor_id JOIN hastane h ON d.hastane_id = h.hastane_id " +
                 "JOIN bolum b ON r.bolum_id = b.bolum_id WHERE r.randevu_id = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setLong(1, randevuId);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return new RandevuDetay.Builder()
                    .randevuId(rs.getLong("randevu_id"))
                    .hastaId(rs.getLong("hasta_id"))
                    .doktorId(rs.getLong("doktor_id"))
                    .bolumId(rs.getLong("bolum_id"))
                    .randevuSaati(rs.getTimestamp("randevu_saati").toLocalDateTime())
                    .durum(rs.getString("durum"))
                    .doktorAdSoyad(rs.getString("doktor_ad"), rs.getString("doktor_soyad"))
                    .hastaneAd(rs.getString("hastane_ad"))
                    .bolumAd(rs.getString("bolum_ad"))
                    .klinikBilgisi("Poliklinik")
                    .build();
            }
        }
    }
    return null;
}

 private List<RandevuDetay> executeRandevuQuery(String sql, Long id) throws SQLException {
    List<RandevuDetay> liste = new ArrayList<>();
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setLong(1, id);
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                RandevuDetay detay = new RandevuDetay.Builder()
                    .randevuId(rs.getLong("randevu_id"))
                    .hastaId(rs.getLong("hasta_id"))
                    .doktorId(rs.getLong("doktor_id"))
                    .bolumId(rs.getLong("bolum_id"))
                    .randevuSaati(rs.getTimestamp("randevu_saati").toLocalDateTime())
                    .durum(rs.getString("durum"))
                    .doktorAdSoyad(rs.getString("doktor_ad"), rs.getString("doktor_soyad"))
                    .hastaneAd(rs.getString("hastane_ad"))
                    .bolumAd(rs.getString("bolum_ad"))
                    .klinikBilgisi("Poliklinik") 
                    .build();
                
                liste.add(detay);
            }
        }
    }
    return liste;
}
 public List<RandevuDetay> hastaAra(Long doktorId, String kriter) {
    List<RandevuDetay> liste = new ArrayList<>();
    String sql = "SELECT r.*, p.ad AS hasta_ad, p.soyad AS hasta_soyad, " +
                 "h.ad AS hastane_ad, b.ad AS bolum_ad " +
                 "FROM randevu r " +
                 "JOIN profil p ON r.hasta_id = p.kullanici_id " +
                 "JOIN doktor d ON r.doktor_id = d.doktor_id " +
                 "JOIN hastane h ON d.hastane_id = h.hastane_id " +
                 "JOIN bolum b ON r.bolum_id = b.bolum_id " +
                 "WHERE r.doktor_id = ? AND (p.ad ILIKE ? OR p.soyad ILIKE ?) " +
                 "ORDER BY r.randevu_saati DESC";

    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setLong(1, doktorId);
        String pattern = "%" + kriter + "%";
        pstmt.setString(2, pattern);
        pstmt.setString(3, pattern);

        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                RandevuDetay detay = new RandevuDetay.Builder()
                    .randevuId(rs.getLong("randevu_id"))
                    .hastaId(rs.getLong("hasta_id"))
                    .doktorId(rs.getLong("doktor_id"))
                    .bolumId(rs.getLong("bolum_id"))
                    .randevuSaati(rs.getTimestamp("randevu_saati").toLocalDateTime())
                    .durum(rs.getString("durum"))
                  
                    .doktorAdSoyad(rs.getString("hasta_ad"), rs.getString("hasta_soyad"))
                    .hastaneAd(rs.getString("hastane_ad"))
                    .bolumAd(rs.getString("bolum_ad"))
                    .klinikBilgisi("Poliklinik")
                    .build();
                liste.add(detay);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return liste;
}
public Long getHastaIdByRandevuId(Long randevuId) {
    String sql = "SELECT hasta_id FROM randevu WHERE randevu_id = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setLong(1, randevuId);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong("hasta_id");
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}

public List<RandevuDetay> doktorYaklasanRandevulariGetir(Long doktorId) {
    List<RandevuDetay> liste = new ArrayList<>();

    String sql = "SELECT r.*, p.ad AS hasta_ad, p.soyad AS hasta_soyad " +
                 "FROM randevu r " +
                 "JOIN profil p ON r.hasta_id = p.kullanici_id " +
                 "WHERE r.doktor_id = ? " +
                 "ORDER BY r.randevu_saati ASC";

    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setLong(1, doktorId);
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                RandevuDetay detay = new RandevuDetay.Builder()
                    .randevuId(rs.getLong("randevu_id"))
                    .hastaId(rs.getLong("hasta_id"))
                    .doktorId(rs.getLong("doktor_id"))
                    .bolumId(rs.getLong("bolum_id"))
                    .randevuSaati(rs.getTimestamp("randevu_saati").toLocalDateTime())
                    .durum(rs.getString("durum"))
              
                    .doktorAdSoyad(rs.getString("hasta_ad"), rs.getString("hasta_soyad"))
                    .hastaneAd("") 
                    .bolumAd("")   
                    .klinikBilgisi("Poliklinik")
                    .build();
                
                liste.add(detay);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return liste;
}

    public boolean randevuOlustur(Long hastaId, Long doktorId, Long bolumId, LocalDateTime saat) {
    String sql = "INSERT INTO randevu (hasta_id, doktor_id, bolum_id, randevu_saati, durum) VALUES (?, ?, ?, ?, 'planlandi')";
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setLong(1, hastaId);
        pstmt.setLong(2, doktorId);
        pstmt.setLong(3, bolumId);
        pstmt.setTimestamp(4, Timestamp.valueOf(saat));
        return pstmt.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

   public Collection<LocalDateTime> getDoluRandevuSaatleri(Long doktorId, LocalDate tarih) {
    List<LocalDateTime> doluSaatler = new ArrayList<>();
    String sql = "SELECT randevu_saati FROM randevu WHERE doktor_id = ? " +
                 "AND CAST(randevu_saati AS DATE) = ? AND durum = 'planlandi'";
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setLong(1, doktorId);
        pstmt.setDate(2, java.sql.Date.valueOf(tarih));
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                doluSaatler.add(rs.getTimestamp("randevu_saati").toLocalDateTime());
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return doluSaatler;
}
   
public Object[] getDoktorMesaiBilgisi(Long doktorId, LocalDate tarih) {
    String sql = "SELECT baslangic_saati, bitis_saati, randevu_suresi_dk " +
                 "FROM doktormusaitlik " +
                 "WHERE doktor_id = ? AND mesai_tarihi = ?";
                 
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setLong(1, doktorId);
        pstmt.setDate(2, java.sql.Date.valueOf(tarih));
        
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return new Object[]{
                    rs.getTime("baslangic_saati").toLocalTime(),
                    rs.getTime("bitis_saati").toLocalTime(),
                    rs.getInt("randevu_suresi_dk")
                };
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
 
    return null; 
}

   public List<RandevuDetay> tumAktifRandevulariGetir() {
    List<RandevuDetay> liste = new ArrayList<>();
    String sql = "SELECT r.*, p.ad AS doktor_ad, p.soyad AS doktor_soyad, " +
                 "h.ad AS hastane_ad, b.ad AS bolum_ad " +
                 "FROM randevu r " +
                 "JOIN profil p ON r.doktor_id = p.kullanici_id " +
                 "JOIN doktor d ON r.doktor_id = d.doktor_id " +
                 "JOIN hastane h ON d.hastane_id = h.hastane_id " +
                 "JOIN bolum b ON r.bolum_id = b.bolum_id " +
                 "WHERE r.durum = 'planlandi' AND r.randevu_saati >= NOW() " +
                 "ORDER BY r.randevu_saati ASC";

    try (Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        
        while (rs.next()) {
            RandevuDetay detay = new RandevuDetay.Builder()
                .randevuId(rs.getLong("randevu_id"))
                .hastaId(rs.getLong("hasta_id"))
                .doktorId(rs.getLong("doktor_id"))
                .bolumId(rs.getLong("bolum_id"))
                .randevuSaati(rs.getTimestamp("randevu_saati").toLocalDateTime())
                .durum(rs.getString("durum"))
                .doktorAdSoyad(rs.getString("doktor_ad"), rs.getString("doktor_soyad"))
                .hastaneAd(rs.getString("hastane_ad"))
                .bolumAd(rs.getString("bolum_ad"))
                .klinikBilgisi("Poliklinik")
                .build();
            liste.add(detay);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return liste;
}

   public boolean randevuTamamla(Long randevuId) {
    String sql = "UPDATE randevu SET durum = 'tamamlandi' WHERE randevu_id = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setLong(1, randevuId);
        return pstmt.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
}