package tr.mhrs.service;

import tr.mhrs.data.SistemAyarlariDAO;
import java.sql.SQLException;

public class SistemAyarService {
    private final SistemAyarlariDAO sistemAyarlariDAO = new SistemAyarlariDAO();

    public int getMaxRandevuGunSayisi() {
        try {
            return sistemAyarlariDAO.getMaxRandevuGunSayisi();
        } catch (SQLException e) {
            System.err.println("Maksimum randevu gün sayısı ayarı yüklenirken veritabanı hatası: " + e.getMessage());
            e.printStackTrace();
            return 5; 
        }
    }
}