package tr.mhrs.template;

import tr.mhrs.model.RandevuDetay;
import java.util.List;
import java.util.stream.Collectors;

public class IptalRandevuListeleme extends RandevuListelemeTemplate {
    @Override
    protected List<RandevuDetay> filtrele(List<RandevuDetay> liste) {
        return liste.stream()
            .filter(r -> "iptal".equalsIgnoreCase(r.getDurum()))
            .collect(Collectors.toList());
    }
}