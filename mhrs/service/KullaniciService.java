package tr.mhrs.service;

import tr.mhrs.data.DoktorDAO; 
import tr.mhrs.data.KullaniciDAO;
import tr.mhrs.model.Kullanici;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.util.Base64;

public class KullaniciService {
    private final KullaniciDAO kullaniciDAO = new KullaniciDAO();
    private final DoktorDAO doktorDAO = new DoktorDAO(); 

    private String hashSifre(String sifre) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return Base64.getEncoder().encodeToString(md.digest(sifre.getBytes()));
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    public boolean doktorKayitOl(String tc, String sifre, String eposta, String ad, String soyad, Long hId, Long bId) {
        try {
            if (kullaniciDAO.kullaniciAdiVarMi(tc) || kullaniciDAO.epostaVarMi(eposta)) return false;
            
            Kullanici d = new Kullanici(tc, hashSifre(sifre), eposta, 3L);
            if (kullaniciDAO.kullaniciKaydet(d)) {
                Long id = d.getKullaniciId();
                return kullaniciDAO.profilKaydet(id, ad, soyad) && doktorDAO.doktorKaydet(id, hId, bId);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean kayitOl(String tc, String sifre, String eposta, String ad, String soyad, Long rolId) {
        try {
            if (kullaniciDAO.kullaniciAdiVarMi(tc) || kullaniciDAO.epostaVarMi(eposta)) return false;
            Kullanici k = new Kullanici(tc, hashSifre(sifre), eposta, rolId);
            if (kullaniciDAO.kullaniciKaydet(k)) {
                Long id = k.getKullaniciId();
                boolean profilOk = kullaniciDAO.profilKaydet(id, ad, soyad);
                if (rolId == 2L) return profilOk && kullaniciDAO.hastaKaydet(id);
                return profilOk;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public Kullanici girisYap(String tc, String sifre) {
        try {
            Kullanici k = kullaniciDAO.kullaniciGetir(tc);
            if (k != null && hashSifre(sifre).equals(k.getSifreHash())) return k;
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
}