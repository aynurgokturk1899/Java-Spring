package yonetici_app.data;

import yonetici_app.model.DoktorMusaitlik;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class YoneticiDAO {
    private final Connection connection = DatabaseManager.getInstance().getConnection();

    public List<String[]> getYoneticiPlanliDetayliListe(String zamanFiltresi) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT d.doktor_id, p.ad, p.soyad, b.ad AS bolum, m.mesai_tarihi, m.baslangic_saati, m.bitis_saati " +
            "FROM doktormusaitlik m " +
            "INNER JOIN doktor d ON m.doktor_id = d.doktor_id " +
            "LEFT JOIN profil p ON d.doktor_id = p.kullanici_id " +
            "LEFT JOIN bolum b ON d.bolum_id = b.bolum_id "
        );

        if ("GELECEK".equalsIgnoreCase(zamanFiltresi)) {
            sql.append("WHERE m.mesai_tarihi >= CURRENT_DATE ");
        } else if ("GECMIS".equalsIgnoreCase(zamanFiltresi)) {
            sql.append("WHERE m.mesai_tarihi < CURRENT_DATE ");
        }

        sql.append("ORDER BY m.mesai_tarihi DESC, m.baslangic_saati ASC");

        List<String[]> veriler = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql.toString())) {
            while (rs.next()) {
                String ad = rs.getString("ad") != null ? rs.getString("ad") : "Bilinmeyen";
                String soyad = rs.getString("soyad") != null ? rs.getString("soyad") : "";
                String bolum = rs.getString("bolum") != null ? rs.getString("bolum") : "Bölüm Yok";

                String drBilgi = "Dr. " + ad + " " + soyad + " [" + bolum + "]";

                veriler.add(new String[]{
                    "---",              
                    drBilgi,              
                    rs.getDate("mesai_tarihi").toString() + " " + rs.getTime("baslangic_saati"), 
                    "MESAİ TANIMLI",      
                    rs.getString("doktor_id") 
                });
            }
        }
        return veriler;
    }

    public List<String[]> getRandevuDetayliListe(String durumFiltresi) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT r.randevu_id, r.doktor_id, p_hasta.ad AS h_ad, p_hasta.soyad AS h_soyad, " +
            "p_doktor.ad AS d_ad, p_doktor.soyad AS d_soyad, b.ad AS bolum, r.randevu_saati, r.durum " +
            "FROM randevu r JOIN doktor d ON r.doktor_id = d.doktor_id " +
            "LEFT JOIN profil p_doktor ON d.doktor_id = p_doktor.kullanici_id " + 
            "LEFT JOIN profil p_hasta ON r.hasta_id = p_hasta.kullanici_id " +    
            "LEFT JOIN bolum b ON d.bolum_id = b.bolum_id "
        );

        if (durumFiltresi != null && !durumFiltresi.equalsIgnoreCase("TÜMÜ")) {
            sql.append("WHERE r.durum = ? ");
        }
        sql.append("ORDER BY r.randevu_saati DESC");

        List<String[]> veriler = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {
            if (durumFiltresi != null && !durumFiltresi.equalsIgnoreCase("TÜMÜ")) {
                pstmt.setString(1, durumFiltresi.toLowerCase());
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    veriler.add(new String[]{
                        rs.getString("h_ad") + " " + rs.getString("h_soyad"),
                        "Dr. " + rs.getString("d_ad") + " " + rs.getString("d_soyad") + " [" + rs.getString("bolum") + "]",
                        rs.getTimestamp("randevu_saati").toString(),
                        rs.getString("durum").toUpperCase(),
                        rs.getString("randevu_id")
                    });
                }
            }
        }
        return veriler;
    }

    public List<String[]> getDoktorGunlukPlan(Long doktorId, LocalDate tarih) throws SQLException {
        String sql = "SELECT r.randevu_id, r.randevu_saati, r.durum, p.ad, p.soyad, p.tc_no, k.eposta " +
                     "FROM randevu r LEFT JOIN profil p ON r.hasta_id = p.kullanici_id " +
                     "LEFT JOIN kullanici k ON r.hasta_id = k.kullanici_id " +
                     "WHERE r.doktor_id = ? AND CAST(r.randevu_saati AS DATE) = ? ORDER BY r.randevu_saati ASC";
        List<String[]> plan = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, doktorId);
            pstmt.setDate(2, Date.valueOf(tarih));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    plan.add(new String[]{
                        rs.getString("randevu_id"),
                        rs.getTimestamp("randevu_saati").toLocalDateTime().toLocalTime().toString(),
                        rs.getString("durum"),
                        rs.getString("ad") + " " + rs.getString("soyad"),
                        rs.getString("tc_no"),
                        rs.getString("eposta")
                    });
                }
            }
        }
        return plan;
    }

    public boolean saveOrUpdateMesai(Long doktorId, LocalDate tarih, LocalTime baslangic, LocalTime bitis, int sure) throws SQLException {
        connection.setAutoCommit(false);
        try {
            String sqlMesai = "INSERT INTO doktormusaitlik (doktor_id, mesai_tarihi, baslangic_saati, bitis_saati, randevu_suresi_dk) " +
                             "VALUES (?, ?, ?, ?, ?) ON CONFLICT (doktor_id, mesai_tarihi) DO UPDATE SET " +
                             "baslangic_saati = EXCLUDED.baslangic_saati, bitis_saati = EXCLUDED.bitis_saati, randevu_suresi_dk = EXCLUDED.randevu_suresi_dk";
            try (PreparedStatement pstmt = connection.prepareStatement(sqlMesai)) {
                pstmt.setLong(1, doktorId); pstmt.setDate(2, Date.valueOf(tarih));
                pstmt.setTime(3, Time.valueOf(baslangic)); pstmt.setTime(4, Time.valueOf(bitis));
                pstmt.setInt(5, sure); pstmt.executeUpdate();
            }
            connection.commit();
            return true;
        } catch (SQLException e) { connection.rollback(); throw e; }
        finally { connection.setAutoCommit(true); }
    }

    public DoktorMusaitlik getMesaiBilgisi(Long doktorId, LocalDate tarih) throws SQLException {
        String sql = "SELECT baslangic_saati, bitis_saati, randevu_suresi_dk FROM doktormusaitlik WHERE doktor_id = ? AND mesai_tarihi = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, doktorId); pstmt.setDate(2, Date.valueOf(tarih));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return new DoktorMusaitlik(doktorId, tarih, rs.getTime("baslangic_saati").toLocalTime(),
                        rs.getTime("bitis_saati").toLocalTime(), rs.getInt("randevu_suresi_dk"));
            }
        }
        return null;
    }

    public int getMaxRandevuGunSayisi() throws SQLException {
        String sql = "SELECT deger FROM sistemayarlari WHERE anahtar = 'MAX_CALENDAR_RANGE_DAYS'";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return Integer.parseInt(rs.getString("deger"));
        }
        return 5;
    }

    public boolean saveMaxRandevuGunSayisi(int gun) throws SQLException {
        String sql = "INSERT INTO sistemayarlari (anahtar, deger) VALUES ('MAX_CALENDAR_RANGE_DAYS', ?) ON CONFLICT (anahtar) DO UPDATE SET deger = EXCLUDED.deger";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, String.valueOf(gun)); return pstmt.executeUpdate() > 0;
        }
    }

    public boolean iptalEtRandevu(Long randevuId) throws SQLException {
        String sql = "UPDATE randevu SET durum = 'iptal' WHERE randevu_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, randevuId); 
            return pstmt.executeUpdate() > 0;
        }
    }

    public String getHastaEmailVeAd(Long randevuId) throws SQLException {
        String sql = "SELECT p.ad, k.eposta FROM randevu r JOIN profil p ON r.hasta_id = p.kullanici_id JOIN kullanici k ON r.hasta_id = k.kullanici_id WHERE r.randevu_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, randevuId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getString("ad") + ";" + rs.getString("eposta");
            }
        }
        return null;
    }

    public boolean bildirimKaydet(Long kullaniciId, Long randevuId, String tur, String icerik) throws SQLException {
        String sql = "INSERT INTO bildirim (kullanici_id, randevu_id, tur, icerik, gonderim_tarihi, durum) " +
                     "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, 'beklemede')";
        
        String formatliIcerik = icerik;
        if (!icerik.startsWith("Konu:") && !icerik.startsWith("❌") && !icerik.startsWith("⚠️")) {
             formatliIcerik = "Konu: MHRS Yönetici Bildirimi\n" + icerik;
        }
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setObject(1, kullaniciId);
            pstmt.setObject(2, randevuId);
            String temizTur = (tur != null) ? tur.toLowerCase().trim() : "push";
            if (temizTur.contains("e-posta")) temizTur = "email";
            pstmt.setString(3, temizTur);
            pstmt.setString(4, formatliIcerik);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Bildirim Kayıt Hatası: " + e.getMessage());
            throw e;
        }
    }

    public Long getHastaIdByRandevu(Long randevuId) throws SQLException {
        String sql = "SELECT hasta_id FROM randevu WHERE randevu_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, randevuId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getLong("hasta_id");
            }
        }
        return null;
    }

    public Long getDoktorIdByRandevu(Long randevuId) throws SQLException {
        String sql = "SELECT doktor_id FROM randevu WHERE randevu_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, randevuId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getLong("doktor_id");
            }
        }
        return null;
    }

    public String[] getRandevuDetaylariForBildirim(Long randevuId) throws SQLException {
        String sql = "SELECT p.ad, p.soyad, h.ad AS hastane, b.ad AS bolum, r.randevu_saati " +
                     "FROM randevu r " +
                     "JOIN doktor d ON r.doktor_id = d.doktor_id " +
                     "JOIN profil p ON d.doktor_id = p.kullanici_id " +
                     "JOIN hastane h ON d.hastane_id = h.hastane_id " +
                     "JOIN bolum b ON d.bolum_id = b.bolum_id " +
                     "WHERE r.randevu_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, randevuId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String drAdSoyad = rs.getString("ad") + " " + rs.getString("soyad");
                    String hastane = rs.getString("hastane");
                    String bolum = rs.getString("bolum");
                    String tarih = rs.getTimestamp("randevu_saati").toLocalDateTime().toString();
                    return new String[]{drAdSoyad, hastane, bolum, tarih};
                }
            }
        }
        return null;
    }

    public String getDoktorIletisim(Long doktorId) throws SQLException {
        String sql = "SELECT p.ad, p.soyad, k.eposta FROM doktor d " +
                     "JOIN profil p ON d.doktor_id = p.kullanici_id " +
                     "JOIN kullanici k ON d.doktor_id = k.kullanici_id " +
                     "WHERE d.doktor_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, doktorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("ad") + " " + rs.getString("soyad") + ";" + rs.getString("eposta");
                }
            }
        }
        return null;
    }

    public List<Long[]> getDoktorGunlukAktifRandevuIds(Long doktorId, LocalDate tarih) throws SQLException {
        List<Long[]> liste = new ArrayList<>();
        String sql = "SELECT randevu_id, hasta_id FROM randevu " +
                     "WHERE doktor_id = ? AND CAST(randevu_saati AS DATE) = ? AND durum = 'planlandi'";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, doktorId);
            pstmt.setDate(2, Date.valueOf(tarih));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    liste.add(new Long[]{rs.getLong("randevu_id"), rs.getLong("hasta_id")});
                }
            }
        }
        return liste;
    }

    public boolean iptalEtDoktorGunlukRandevular(Long doktorId, LocalDate tarih) throws SQLException {
        String sql = "UPDATE randevu SET durum = 'iptal' " +
                     "WHERE doktor_id = ? AND CAST(randevu_saati AS DATE) = ? AND durum = 'planlandi'";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, doktorId);
            pstmt.setDate(2, Date.valueOf(tarih));
            return pstmt.executeUpdate() >= 0;
        }
    }
}