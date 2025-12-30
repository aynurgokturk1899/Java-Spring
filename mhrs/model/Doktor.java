package tr.mhrs.model;

public class Doktor {
    
    private Long doktorId;
    
    private Long hastaneId;
    
    private Long bolumId;
    
    private String doktorTipi; 
    
    private String ad;
    
    private String soyad;

    private String hastaneAd;
    private String bolumAd;

    public Doktor() {
    }

    public Doktor(Long doktorId, Long hastaneId, Long bolumId, String doktorTipi) {
        this.doktorId = doktorId;
        this.hastaneId = hastaneId;
        this.bolumId = bolumId;
        this.doktorTipi = doktorTipi;
    }

    public Long getDoktorId() {
        return doktorId;
    }

    public void setDoktorId(Long doktorId) {
        this.doktorId = doktorId;
    }

    public Long getHastaneId() {
        return hastaneId;
    }

    public void setHastaneId(Long hastaneId) {
        this.hastaneId = hastaneId;
    }

    public Long getBolumId() {
        return bolumId;
    }

    public void setBolumId(Long bolumId) {
        this.bolumId = bolumId;
    }

    public String getDoktorTipi() {
        return doktorTipi;
    }

    public void setDoktorTipi(String doktorTipi) {
        this.doktorTipi = doktorTipi;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getSoyad() {
        return soyad;
    }

    public void setSoyad(String soyad) {
        this.soyad = soyad;
    }

    // YENİ EKLENEN GETTER/SETTER METOTLARI
    
    public String getHastaneAd() {
        return hastaneAd;
    }

    public void setHastaneAd(String hastaneAd) {
        this.hastaneAd = hastaneAd;
    }

    public String getBolumAd() {
        return bolumAd;
    }

    public void setBolumAd(String bolumAd) {
        this.bolumAd = bolumAd;
    }
}