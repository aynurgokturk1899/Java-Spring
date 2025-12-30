package yonetici_app.observer;

public interface RandevuObserver {
    void randevuIptalEdildi(Long randevuId, Long hastaId, Long doktorId, String mesaj);
}