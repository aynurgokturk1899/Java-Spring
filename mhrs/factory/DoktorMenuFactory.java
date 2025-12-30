package tr.mhrs.factory;

import tr.mhrs.model.Kullanici;
import tr.mhrs.view.DoktorAnaMenu; 
import javax.swing.JFrame;

public class DoktorMenuFactory implements MenuFactory {
    @Override
    public JFrame createMenu(Kullanici kullanici) {
        return new DoktorAnaMenu(kullanici);
    }
}