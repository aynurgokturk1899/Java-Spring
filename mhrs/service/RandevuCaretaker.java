package tr.mhrs.service;

import tr.mhrs.model.RandevuMemento;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class RandevuCaretaker {
    private final Map<Long, RandevuMemento> mementoHistory = new ConcurrentHashMap<>();

    public void mementoEkle(RandevuMemento m) {
        if (m != null) {
            mementoHistory.put(m.getRandevuId(), m);
        }
    }

    public RandevuMemento mementoGetir(Long randevuId) {
        return mementoHistory.get(randevuId);
    }
}