package yonetici_app.model;

public class Hastane {
    private Long hastaneId;
    private String ad;
    

    public Hastane(Long hastaneId, String ad) {
        this.hastaneId = hastaneId;
        this.ad = ad;
    }
    
    public Long getHastaneId() { return hastaneId; }
    public String getAd() { return ad; }
    
    @Override
    public String toString() {
        return ad;
    }
}


