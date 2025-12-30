package tr.mhrs.service;

import tr.mhrs.data.DoktorDAO; 
import tr.mhrs.data.RandevuDAO; 
import tr.mhrs.data.HastaDAO; 
import tr.mhrs.model.Doktor;
import tr.mhrs.model.RandevuDetay; 
import tr.mhrs.model.AileHekimiBilgisi;
import tr.mhrs.service.BildirimService;
import tr.mhrs.model.RandevuMemento;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class RandevuService {
    
    private static final RandevuCaretaker caretaker = new RandevuCaretaker();
    private final DoktorDAO doktorDAO = new DoktorDAO();
    private final RandevuDAO randevuDAO = new RandevuDAO();
    private final HastaDAO hastaDAO = new HastaDAO(); 
    private final BildirimService bildirimService = new BildirimService();

    public String randevuGeriAl(Long randevuId) {
        try {
            RandevuDetay detay = randevuDAO.getRandevuDetayById(randevuId);
            if (detay == null) return "Randevu bulunamadı.";

            if (detay.getRandevuSaati().isBefore(LocalDateTime.now())) {
                return "Randevu tarihi geçtiği için bu işlem geri alınamaz!";
            }

            RandevuMemento memento = caretaker.mementoGetir(randevuId);
            if (memento != null) {
                boolean basarili = randevuDAO.durumGuncelle(randevuId, memento.getEskiDurum());
                if (basarili) {
                    return "OK";
                }
            }
            return "Geri alma bilgisi bulunamadı (Oturum süresince geçerlidir).";
        } catch (SQLException e) {
            return "Veritabanı hatası: " + e.getMessage();
        }
    }

    public boolean randevuIptal(Long randevuId) throws SQLException {
        if (randevuId == null) return false;
        
        RandevuDetay detay = randevuDAO.getRandevuDetayById(randevuId);
        if (detay != null) {
            caretaker.mementoEkle(new RandevuMemento(randevuId, detay.getDurum()));
        }
        
        return randevuDAO.randevuIptal(randevuId);
    }

    public boolean yoneticiRandevuIptal(Long randevuId, String iptalSebebi) {
        if (randevuId == null) return false;
        try {
            RandevuDetay detay = randevuDAO.getRandevuDetayById(randevuId);
            
            if (detay != null) {
                caretaker.mementoEkle(new RandevuMemento(randevuId, detay.getDurum()));
            }

            boolean iptalBasarili = randevuDAO.randevuIptal(randevuId);

            if (iptalBasarili && detay != null) {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM HH:mm");
                String tarihStr = detay.getRandevuSaati().format(fmt);

                String hastaKonu = "❌ İptal: Dr. " + detay.getDoktorAdSoyad() + " (" + tarihStr + ")";
                String hastaIcerik = "Sayın Hastamız,\n" +
                                     detay.getHastaneAd() + " - " + detay.getBolumAd() + " bölümündeki randevunuz " +
                                     "yönetim tarafından iptal edilmiştir.\n\n" +
                                     "Sebep: " + iptalSebebi;
                
                bildirimService.bildirimGonder(detay.getHastaId(), hastaKonu, hastaIcerik);

                String doktorKonu = "⚠️ Yönetici İptali: " + tarihStr;
                String doktorIcerik = "Sayın Doktorumuz,\n" +
                                      detay.getRandevuSaati().format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm")) + 
                                      " tarihli hasta randevusu yönetim tarafından iptal edilmiştir.\n\n" +
                                      "Sebep: " + iptalSebebi;
                                      
                bildirimService.bildirimGonder(detay.getDoktorId(), doktorKonu, doktorIcerik);

                return true;
            }
        } catch (Exception e) {
            System.err.println("Yönetici iptal hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean doktorRandevuIptal(Long randevuId) throws SQLException {
        if (randevuId == null) return false;
        RandevuDetay detay = randevuDAO.getRandevuDetayById(randevuId);
        
        if (detay != null) {
            caretaker.mementoEkle(new RandevuMemento(randevuId, detay.getDurum()));
        }

        boolean basarili = randevuDAO.randevuIptal(randevuId);
        if (basarili && detay != null) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM HH:mm");
            String tarihStr = detay.getRandevuSaati().format(fmt);
            
            String konu = "❌ İptal: Dr. " + detay.getDoktorAdSoyad() + " (" + tarihStr + ")";
            
            String icerik = "Sayın Hastamız,\n" +
                    detay.getHastaneAd() + "\n" +
                    detay.getBolumAd() + " bölümündeki randevunuz " +
                    "doktor tarafından zorunlu nedenlerle iptal edilmiştir.\n" +
                    "Lütfen yeni bir randevu oluşturunuz.";
            
            bildirimService.bildirimGonder(detay.getHastaId(), konu, icerik);
        }
        return basarili;
    }

    public boolean doktorRandevuTamamla(Long randevuId) {
        if (randevuId == null) return false;
        return randevuDAO.randevuTamamla(randevuId);
    }

    public List<RandevuDetay> getTumAktifRandevular() {
        return randevuDAO.tumAktifRandevulariGetir();
    }

    public List<Doktor> randevuAra(String kriter) {
        if (kriter == null || kriter.trim().isEmpty()) return Collections.emptyList();
        try {
            return doktorDAO.doktorAra(kriter);
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
 
    public List<RandevuDetay> getYaklasanRandevular(Long hastaId) throws SQLException {
        if (hastaId == null) return Collections.emptyList();
        return randevuDAO.yaklasanRandevulariGetir(hastaId);
    }

    public List<RandevuDetay> getGecmisRandevular(Long hastaId) throws SQLException {
        if (hastaId == null) return Collections.emptyList();
        return randevuDAO.getGecmisRandevular(hastaId);
    }

    public AileHekimiBilgisi getAileHekimiBilgisi(Long hastaId) {
        if (hastaId == null) return null;
        try {
            return hastaDAO.aileHekimiGetir(hastaId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<LocalDateTime> getMusaitSaatler(Long doktorId, LocalDate tarih) {
        if (doktorId == null || tarih == null || tarih.isBefore(LocalDate.now())) {
            return Collections.emptyList();
        }
        Object[] mesaiBilgisi = randevuDAO.getDoktorMesaiBilgisi(doktorId, tarih);
        if (mesaiBilgisi == null) return Collections.emptyList();
        LocalTime baslangic = (LocalTime) mesaiBilgisi[0];
        LocalTime bitis = (LocalTime) mesaiBilgisi[1];
        int randevuSuresi = (int) mesaiBilgisi[2];
        List<LocalDateTime> potansiyelSaatler = new ArrayList<>();
        LocalTime mevcutSaat = baslangic;
        while (mevcutSaat.isBefore(bitis)) {
            potansiyelSaatler.add(LocalDateTime.of(tarih, mevcutSaat));
            mevcutSaat = mevcutSaat.plusMinutes(randevuSuresi);
        }
        List<LocalDateTime> doluSaatler = (List<LocalDateTime>) randevuDAO.getDoluRandevuSaatleri(doktorId, tarih);
        potansiyelSaatler.removeAll(doluSaatler);
        if (tarih.isEqual(LocalDate.now())) {
            potansiyelSaatler.removeIf(saat -> saat.isBefore(LocalDateTime.now().plusMinutes(randevuSuresi)));
        }
        return potansiyelSaatler;
    }

    public boolean randevuAl(Long hastaId, Long doktorId, Long bolumId, LocalDateTime randevuSaati) {
        if (hastaId == null || doktorId == null || bolumId == null || randevuSaati == null) return false;
        return randevuDAO.randevuOlustur(hastaId, doktorId, bolumId, randevuSaati);
    }

    public List<RandevuDetay> getDoktorYaklasanRandevular(Long doktorId) {
        if (doktorId == null) return Collections.emptyList();
        return randevuDAO.doktorYaklasanRandevulariGetir(doktorId);
    }

    public Long getRandevuHastaId(Long randevuId) {
        return randevuDAO.getHastaIdByRandevuId(randevuId);
    }

    public List<RandevuDetay> hastaAra(Long doktorId, String kriter) {
        if (doktorId == null || kriter == null || kriter.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return randevuDAO.hastaAra(doktorId, kriter);
    }
}