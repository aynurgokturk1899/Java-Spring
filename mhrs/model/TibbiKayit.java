package tr.mhrs.model;

import java.sql.Timestamp;

public class TibbiKayit {
    private Long kayitId;
    private String kayitTipi;
    private String aciklama;
    private Timestamp olusturma_tarihi;

    public Long getKayitId() { return kayitId; }
    public void setKayitId(Long kayitId) { this.kayitId = kayitId; }

    public String getKayitTipi() { return kayitTipi; }
    public void setKayitTipi(String kayitTipi) { this.kayitTipi = kayitTipi; }

    public String getAciklama() { return aciklama; }
    public void setAciklama(String aciklama) { this.aciklama = aciklama; }

    public Timestamp getOlusturmaTarihi() { return olusturma_tarihi; }
    public void setOlusturma_tarihi(Timestamp olusturma_tarihi) { 
        this.olusturma_tarihi = olusturma_tarihi; 
    }
}