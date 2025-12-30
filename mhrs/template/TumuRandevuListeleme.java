package tr.mhrs.template;

import tr.mhrs.model.RandevuDetay;
import java.util.List;

public class TumuRandevuListeleme extends RandevuListelemeTemplate {
    @Override
    protected List<RandevuDetay> filtrele(List<RandevuDetay> liste) {
        return liste;
    }
}