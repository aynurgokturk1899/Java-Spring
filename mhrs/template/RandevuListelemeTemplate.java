package tr.mhrs.template;

import tr.mhrs.model.RandevuDetay;
import javax.swing.*;
import java.util.List;
import java.util.function.Function;
import java.awt.Component;

public abstract class RandevuListelemeTemplate {

    public final void listele(List<RandevuDetay> tumRandevular, JPanel panel, Function<RandevuDetay, JPanel> cardCreator) {
        panel.removeAll();
        
        List<RandevuDetay> filtrelenmis = filtrele(tumRandevular);
        
        if (filtrelenmis == null || filtrelenmis.isEmpty()) {
            JLabel lblEmpty = new JLabel("Bu kategoride randevu bulunmuyor.");
            lblEmpty.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(lblEmpty);
        } else {
            for (RandevuDetay r : filtrelenmis) {
                panel.add(cardCreator.apply(r));
                panel.add(Box.createVerticalStrut(15));
            }
        }
        
        panel.revalidate();
        panel.repaint();
    }

    protected abstract List<RandevuDetay> filtrele(List<RandevuDetay> liste);
}