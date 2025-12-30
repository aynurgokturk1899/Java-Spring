package tr.mhrs.model;

import java.time.LocalDateTime;

public class RandevuDetay extends Randevu {
    private String doktorAdSoyad;
    private String hastaneAd;
    private String bolumAd;
    private String klinikBilgisi;

    private RandevuDetay(Builder builder) {
        setRandevuId(builder.randevuId);
        setHastaId(builder.hastaId);
        setDoktorId(builder.doktorId);
        setBolumId(builder.bolumId);
        setRandevuSaati(builder.randevuSaati);
        setDurum(builder.durum);
        this.doktorAdSoyad = builder.doktorAdSoyad;
        this.hastaneAd = builder.hastaneAd;
        this.bolumAd = builder.bolumAd;
        this.klinikBilgisi = builder.klinikBilgisi;
    }
     

    public String getDoktorAdSoyad() { return doktorAdSoyad; }
    public String getHastaneAd() { return hastaneAd; }
    public String getBolumAd() { return bolumAd; }
    public String getKlinikBilgisi() { return klinikBilgisi; }

    public static class Builder {
        private Long randevuId, hastaId, doktorId, bolumId;
        private LocalDateTime randevuSaati;
        private String durum, doktorAdSoyad, hastaneAd, bolumAd, klinikBilgisi;

        public Builder randevuId(Long id) { this.randevuId = id; return this; }
        public Builder hastaId(Long id) { this.hastaId = id; return this; }
        public Builder doktorId(Long id) { this.doktorId = id; return this; }
        public Builder bolumId(Long id) { this.bolumId = id; return this; }
        public Builder randevuSaati(LocalDateTime saat) { this.randevuSaati = saat; return this; }
        public Builder durum(String durum) { this.durum = durum; return this; }
        public Builder doktorAdSoyad(String ad, String soyad) { 
            this.doktorAdSoyad = ad + " " + soyad; 
            return this; 
        }
        public Builder hastaneAd(String ad) { this.hastaneAd = ad; return this; }
        public Builder bolumAd(String ad) { this.bolumAd = ad; return this; }
        public Builder klinikBilgisi(String bilgi) { this.klinikBilgisi = bilgi; return this; }

        public RandevuDetay build() {
            return new RandevuDetay(this);
        }
    }
}