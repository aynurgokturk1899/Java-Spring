package tr.mhrs.model;

import java.time.LocalDateTime;

public class Randevu {
    
    private Long randevuId;
    
    private Long hastaId;
    
    private Long doktorId;
    
    private Long bolumId;
    
    private LocalDateTime randevuSaati;
    
    private String durum; 

    public Randevu() {
    }
    public Randevu(Long hastaId, Long doktorId, Long bolumId, LocalDateTime randevuSaati, String durum) {
        this.hastaId = hastaId;
        this.doktorId = doktorId;
        this.bolumId = bolumId;
        this.randevuSaati = randevuSaati;
        this.durum = durum;
    }

    public Long getRandevuId() {
        return randevuId;
    }

    public void setRandevuId(Long randevuId) {
        this.randevuId = randevuId;
    }

    public Long getHastaId() {
        return hastaId;
    }

    public void setHastaId(Long hastaId) {
        this.hastaId = hastaId;
    }

    public Long getDoktorId() {
        return doktorId;
    }

    public void setDoktorId(Long doktorId) {
        this.doktorId = doktorId;
    }

    public Long getBolumId() {
        return bolumId;
    }

    public void setBolumId(Long bolumId) {
        this.bolumId = bolumId;
    }

    public LocalDateTime getRandevuSaati() {
        return randevuSaati;
    }

    public void setRandevuSaati(LocalDateTime randevuSaati) {
        this.randevuSaati = randevuSaati;
    }

    public String getDurum() {
        return durum;
    }

    public void setDurum(String durum) {
        this.durum = durum;
    }
}