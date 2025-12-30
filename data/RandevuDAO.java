package yonetici_app.data;

import java.sql.*;

public class RandevuDAO {

    private final Connection conn = DatabaseManager.getInstance().getConnection();

    public int getGunlukRandevuSayisi() throws SQLException {
        String sql = """
            SELECT COUNT(*)
            FROM randevu_log
            WHERE islem_tipi = 'OLUSTURULDU'
              AND DATE(islem_zamani) = CURRENT_DATE
        """;

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    public int getGunlukIptalSayisi() throws SQLException {
        String sql = """
            SELECT COUNT(*)
            FROM randevu_log
            WHERE islem_tipi = 'IPTAL'
              AND DATE(islem_zamani) = CURRENT_DATE
        """;

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    public double getIptalOrani() throws SQLException {
        String sql = """
            SELECT COALESCE(
                ROUND(
                    (SUM(CASE WHEN islem_tipi = 'IPTAL' THEN 1 ELSE 0 END)::decimal
                    / NULLIF(SUM(CASE WHEN islem_tipi = 'OLUSTURULDU' THEN 1 ELSE 0 END), 0)) * 100
                , 1)
            , 0)
            FROM randevu_log
        """;

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            return rs.getDouble(1);
        }
    }

    public int getAktifRandevuSayisi() throws SQLException {
        String sql = """
            SELECT COUNT(*)
            FROM randevu
            WHERE randevu_saati >= NOW()
              AND durum = 'planlandi'
        """;

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            return rs.getInt(1);
        }
    }
public int getAktifDoktorSayisi() throws SQLException {
    String sql = "SELECT COUNT(*) FROM doktor";
    try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
        rs.next(); return rs.getInt(1);
    }
}

public int getAktifHastaSayisi() throws SQLException {
    String sql = "SELECT COUNT(DISTINCT hasta_id) FROM randevu"; 
    try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
        rs.next(); return rs.getInt(1);
    }
}
}
