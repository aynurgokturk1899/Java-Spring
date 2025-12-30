package yonetici_app.observer;

import yonetici_app.strategy.*;
import java.awt.Component;

public class HastaBildirimObserver implements RandevuObserver {
    private yonetici_app.data.YoneticiDAO dao;
    private BildirimContext context;

    public HastaBildirimObserver(Component parent) {
        this.dao = new yonetici_app.data.YoneticiDAO();
        this.context = new BildirimContext(parent);
    }

    @Override
    public void randevuIptalEdildi(Long randevuId, Long hastaId, Long doktorId, String mesaj) {
        try {
            String info = dao.getHastaEmailVeAd(randevuId); 
            if (info != null) {
                String[] p = info.split(";");
                String ad = p[0];
                String iletisim = p[1];

                String secilenTur = context.bildirimiGonderSorarak(ad, iletisim, "Randevu İptali", mesaj);
                
                if (secilenTur != null) {
                    dao.bildirimKaydet(hastaId, randevuId, secilenTur, mesaj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}