package yonetici_app.observer;

import java.util.ArrayList;
import java.util.List;

public class RandevuIptalSubject {
    private List<RandevuObserver> gozlemciler = new ArrayList<>();

    public void ekle(RandevuObserver observer) {
        gozlemciler.add(observer);
    }

    public void iptalBildir(Long randevuId, Long hastaId, Long doktorId, String mesaj) {
        for (RandevuObserver observer : gozlemciler) {
            observer.randevuIptalEdildi(randevuId, hastaId, doktorId, mesaj);
        }
    }
}