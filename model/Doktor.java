package yonetici_app.model;

public class Doktor {
    private Long doktorId;
    private Long hastaneId;
    private Long bolumId;
    private String ad;
    private String soyad;
    private String hastaneAd;
    private String bolumAd;

    public Long getDoktorId() { return doktorId; }
    public void setDoktorId(Long doktorId) { this.doktorId = doktorId; }
    public Long getHastaneId() { return hastaneId; }
    public void setHastaneId(Long hastaneId) { this.hastaneId = hastaneId; }
    public Long getBolumId() { return bolumId; }
    public void setBolumId(Long bolumId) { this.bolumId = bolumId; }
    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }
    public String getSoyad() { return soyad; }
    public void setSoyad(String soyad) { this.soyad = soyad; }
    public String getHastaneAd() { return hastaneAd; }
    public void setHastaneAd(String hastaneAd) { this.hastaneAd = hastaneAd; }
    public String getBolumAd() { return bolumAd; }
    public void setBolumAd(String bolumAd) { this.bolumAd = bolumAd; }

    @Override
    public String toString() {
        if (doktorId != null && doktorId == 0L) {
            return "--- Doktor Seçiniz ---";
        }
        
        String gosterilecekAd = (ad != null && !ad.equalsIgnoreCase("null")) ? ad : "İsimsiz";
        String gosterilecekSoyad = (soyad != null && !soyad.equalsIgnoreCase("null")) ? soyad : "Doktor";
        String gosterilecekBolum = (bolumAd != null) ? bolumAd : "Bölüm Yok";
        
        return "Dr. " + gosterilecekAd + " " + gosterilecekSoyad + " (" + gosterilecekBolum + ")";
    }
}