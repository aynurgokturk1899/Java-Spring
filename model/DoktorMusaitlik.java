package yonetici_app.model;

import java.time.LocalTime;
import java.time.LocalDate;

public class DoktorMusaitlik {
    
    private Long doktorId;
    private LocalDate mesaiTarihi; 
    private LocalTime baslangicSaati;
    private LocalTime bitisSaati;
    private int randevuSuresiDk;

    public DoktorMusaitlik(Long doktorId, LocalDate mesaiTarihi, LocalTime baslangicSaati, LocalTime bitisSaati, int randevuSuresiDk) {
        this.doktorId = doktorId;
        this.mesaiTarihi = mesaiTarihi;
        this.baslangicSaati = baslangicSaati;
        this.bitisSaati = bitisSaati;
        this.randevuSuresiDk = randevuSuresiDk;
    }

    public Long getDoktorId() { return doktorId; }
    public LocalDate getMesaiTarihi() { return mesaiTarihi; } 
    public LocalTime getBaslangicSaati() { return baslangicSaati; }
    public LocalTime getBitisSaati() { return bitisSaati; }
    public int getRandevuSuresiDk() { return randevuSuresiDk; }
}