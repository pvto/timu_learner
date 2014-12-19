package my.plot;

import java.io.PrintStream;
import java.util.List;
import my.Attribute;
import my.f.Attr;
import my.f.Obj;
import my.f.Str;

/**
 * @author pvto https://github.com/pvto
 */
public class Cout {

    static public void plot(PrintStream out, List<Attribute> x, List<Attribute> y, List<Attribute> feature) {

        List<String> prints = Attr.svals(feature);
        int maxlen = Str.maxlen(prints);
        prints = Str.pad(prints, maxlen, ' ');
        String empty = Str.pad("", maxlen, ' ');
        
        int v0 = Attr.imin(y),
            u0 = Attr.imin(x),
            h = Attr.imax(y) - v0 + 1,
            w = Attr.imax(x) - u0 + 1;
        Object[][] pr = new Object[h][w];
        for(int i = 0; i < feature.size(); i++) {
            int v = Attr.ival(y.get(i)),
                u = Attr.ival(x.get(i));
            pr[v][u] = prints.get(i);
        }
        
        for (Object[] pr1 : pr) {
            for (Object pr11 : pr1) {
                out.print(Obj.first(pr11, empty));
            }
            out.println("");
        }

    }
}
