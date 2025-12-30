package tr.mhrs.factory;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import tr.mhrs.model.Kullanici;
import tr.mhrs.view.HastaAnaMenu;
import javax.swing.JFrame;


public class HastaMenuFactory implements MenuFactory {
    @Override
    public JFrame createMenu(Kullanici kullanici) {
        try {
            return new HastaAnaMenu(kullanici);
        } catch (SQLException ex) {
            Logger.getLogger(HastaMenuFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}