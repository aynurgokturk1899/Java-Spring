package tr.mhrs.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Bildirim {
    private Long bildirimId;
    private Long kullaniciId;
    private Long randevuId; 
    private String tur;    
    private String konu;    
    private String icerik;
    private LocalDateTime gonderimTarihi;
    private String durum;   


    public Bildirim(Long kullaniciId, Long randevuId, String tur, String konu, String icerik) {
        this.kullaniciId = kullaniciId;
        this.randevuId = randevuId;
        this.tur = (tur == null) ? "push" : tur;
        this.konu = konu;
        this.icerik = icerik;
        this.gonderimTarihi = LocalDateTime.now();
        this.durum = "beklemede";
    }

    public Bildirim(Long kullaniciId, String konu, String icerik) {
        this(kullaniciId, null, "push", konu, icerik);
    }

    public Long getBildirimId() { return bildirimId; }
    public void setBildirimId(Long bildirimId) { this.bildirimId = bildirimId; }
    
    public Long getKullaniciId() { return kullaniciId; }
    public Long getRandevuId() { return randevuId; }
    public void setRandevuId(Long randevuId) { this.randevuId = randevuId; }

    public String getTur() { return tur; }
    public void setTur(String tur) { this.tur = tur; }

    public String getKonu() { return konu; }
    public String getIcerik() { return icerik; }
    
    public LocalDateTime getGonderimTarihi() { return gonderimTarihi; }
    public void setGonderimTarihi(LocalDateTime gonderimTarihi) { this.gonderimTarihi = gonderimTarihi; }
    
    public String getDurum() { return durum; }
    public void setDurum(String durum) { this.durum = durum; }
    
    public boolean isOkundu() { 
        return !"beklemede".equals(durum); 
    }

    @Override
    public String toString() {
        return (isOkundu() ? "[\u2713] " : "[ ] ") + 
               gonderimTarihi.format(DateTimeFormatter.ofPattern("dd MMM HH:mm")) + 
               " - " + konu;
    }
}