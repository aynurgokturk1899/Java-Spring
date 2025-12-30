package tr.mhrs.factory;

import tr.mhrs.model.Kullanici;
import javax.swing.JFrame;

public interface MenuFactory {
   
    JFrame createMenu(Kullanici kullanici);
}