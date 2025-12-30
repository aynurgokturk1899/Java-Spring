package yonetici_app.observer;

import yonetici_app.data.YoneticiDAO;

public class DoktorBildirimObserver implements RandevuObserver {
    private YoneticiDAO dao = new YoneticiDAO();

    @Override
public void randevuIptalEdildi(Long randevuId, Long hastaId, Long doktorId, String mesaj) {
    try {
        String[] detaylar = dao.getRandevuDetaylariForBildirim(randevuId);
        
        if (detaylar != null) {
            String tarihSaat = detaylar[3];
            String doktorIcinMesaj = "BİLGİ: " + tarihSaat + " tarihindeki randevunuz yönetici tarafından iptal edildi.";
            
            dao.bildirimKaydet(doktorId, null, "push", doktorIcinMesaj);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}}