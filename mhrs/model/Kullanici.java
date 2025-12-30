package tr.mhrs.model;

public class Kullanici {
    private Long kullaniciId;
    private String tcKimlikNo;
    private String sifreHash;
    private String eposta;
    private Long rolId;
    private String ad;    
    private String soyad; 
    
    public Kullanici() {}

    public Kullanici(String tcKimlikNo, String sifreHash, String eposta, Long rolId) {
        this.tcKimlikNo = tcKimlikNo;
        this.sifreHash = sifreHash;
        this.eposta = eposta;
        this.rolId = rolId;
    }
    
    public Long getKullaniciId() { return kullaniciId; }
    public void setKullaniciId(Long kullaniciId) { this.kullaniciId = kullaniciId; }
    public String getTcKimlikNo() { return tcKimlikNo; }
    public void setTcKimlikNo(String tcKimlikNo) { this.tcKimlikNo = tcKimlikNo; }
    public String getSifreHash() { return sifreHash; }
    public void setSifreHash(String sifreHash) { this.sifreHash = sifreHash; }
    public String getEposta() { return eposta; }
    public void setEposta(String eposta) { this.eposta = eposta; }
    public Long getRolId() { return rolId; }
    public void setRolId(Long rolId) { this.rolId = rolId; }

    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }
    public String getSoyad() { return soyad; }
    public void setSoyad(String soyad) { this.soyad = soyad; }
}