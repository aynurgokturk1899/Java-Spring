package tr.mhrs.data;

import java.sql.*;

public class SistemAyarlariDAO {
    private final Connection connection = DatabaseManager.getInstance().getConnection();
    private static final String MAX_DAYS_KEY = "MAX_CALENDAR_RANGE_DAYS";

    public int getMaxRandevuGunSayisi() throws SQLException {
        String sql = "SELECT deger FROM SistemAyarlari WHERE anahtar = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, MAX_DAYS_KEY);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Integer.parseInt(rs.getString("deger"));
                }
            }
        }
        return 5; 
    }
}