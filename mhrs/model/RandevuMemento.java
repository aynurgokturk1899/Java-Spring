package tr.mhrs.model;

public class RandevuMemento {
    private final Long randevuId;
    private final String eskiDurum;

    public RandevuMemento(Long randevuId, String eskiDurum) {
        this.randevuId = randevuId;
        this.eskiDurum = eskiDurum;
    }

    public Long getRandevuId() { return randevuId; }
    public String getEskiDurum() { return eskiDurum; }
}