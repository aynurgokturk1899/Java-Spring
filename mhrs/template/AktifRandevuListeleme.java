package tr.mhrs.template;

import tr.mhrs.model.RandevuDetay;
import java.util.List;
import java.util.stream.Collectors;

public class AktifRandevuListeleme extends RandevuListelemeTemplate {
    @Override
    protected List<RandevuDetay> filtrele(List<RandevuDetay> liste) {
        return liste.stream()
            .filter(r -> "planlandi".equalsIgnoreCase(r.getDurum()))
            .collect(Collectors.toList());
    }
}