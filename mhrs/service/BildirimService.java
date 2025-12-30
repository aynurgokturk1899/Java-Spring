package tr.mhrs.service;

import tr.mhrs.data.BildirimDAO;
import tr.mhrs.model.Bildirim;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class BildirimService {
    
    private final BildirimDAO bildirimDAO = new BildirimDAO();

    public boolean bildirimGonder(Long kullaniciId, String konu, String icerik) {
        if (kullaniciId == null || konu == null || icerik == null) {
            System.err.println("Bildirim için gerekli alanlar eksik.");
            return false;
        }
        
        Bildirim yeniBildirim = new Bildirim(kullaniciId, konu, icerik);
        
        try {
            return bildirimDAO.bildirimKaydet(yeniBildirim);
        } catch (SQLException e) {
            System.err.println("Bildirim kaydı sırasında veritabanı hatası: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public int getOkunmamisBildirimSayisi(Long kullaniciId) {
        if (kullaniciId == null) return 0;
        try {
            return bildirimDAO.okunmamisBildirimSayisi(kullaniciId);
        } catch (SQLException e) {
            System.err.println("Okunmamış bildirim sayısı çekilirken hata: " + e.getMessage());
            return 0;
        }
    }

    public List<Bildirim> getKullaniciBildirimleri(Long kullaniciId, boolean sadeceOkunmamis) {
         if (kullaniciId == null) return Collections.emptyList();
         try {
             return bildirimDAO.kullaniciBildirimleriniGetir(kullaniciId, sadeceOkunmamis);
         } catch (SQLException e) {
             System.err.println("Bildirimler listelenirken hata: " + e.getMessage());
             return Collections.emptyList();
         }
    }

    public void bildirimleriOkunduYap(Long kullaniciId) {
        if (kullaniciId == null) return;
        try {
            bildirimDAO.bildirimleriOkunduYap(kullaniciId);
        } catch (SQLException e) {
            System.err.println("Bildirimler okundu olarak işaretlenirken hata: " + e.getMessage());
        }
    }
}