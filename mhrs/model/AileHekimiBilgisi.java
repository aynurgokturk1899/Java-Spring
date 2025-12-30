package tr.mhrs.model;

public class AileHekimiBilgisi {
    private String doktorAdSoyad;
    private String birimAd;
    private String adres;
    private String telefon;

    
    public AileHekimiBilgisi(String doktorAdSoyad, String birimAd, String adres, String telefon) {
        this.doktorAdSoyad = doktorAdSoyad;
        this.birimAd = birimAd;
        this.adres = adres;
        this.telefon = telefon;
    }

    public String getDoktorAdSoyad() { return doktorAdSoyad; }
    public String getBirimAd() { return birimAd; }
    public String getAdres() { return adres; }
    public String getTelefon() { return telefon; }
}