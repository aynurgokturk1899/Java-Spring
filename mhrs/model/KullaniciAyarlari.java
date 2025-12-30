package tr.mhrs.model;

import java.sql.Timestamp;

public class KullaniciAyarlari {
    private Long ayarId;
    private Long kullaniciId;
    private String tema;
    private String dil;
    private String bildirimTercihi;
    private Timestamp guncellenmeTarihi;

    public KullaniciAyarlari(Long kullaniciId, String tema, String dil, String bildirimTercihi) {
        this.kullaniciId = kullaniciId;
        this.tema = tema;
        this.dil = dil;
        this.bildirimTercihi = bildirimTercihi;
    }

    public Long getKullaniciId() { return kullaniciId; }
    public String getTema() { return tema; }
    public void setTema(String tema) { this.tema = tema; }
    public String getDil() { return dil; }
    public void setDil(String dil) { this.dil = dil; }
    public String getBildirimTercihi() { return bildirimTercihi; }
    public void setBildirimTercihi(String bildirimTercihi) { this.bildirimTercihi = bildirimTercihi; }
}