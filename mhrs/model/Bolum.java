package tr.mhrs.model;

public class Bolum {
    private Long bolumId;
    private String ad;
    

    public Bolum(Long bolumId, String ad) {
        this.bolumId = bolumId;
        this.ad = ad;
    }
    
    public Long getBolumId() { return bolumId; }
    public String getAd() { return ad; }

    @Override
    public String toString() {
        return ad;
    }
}