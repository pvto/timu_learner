
package my.f;

import java.util.ArrayList;
import java.util.List;
import my.Attribute;

/**
 * @author pvto https://github.com/pvto
 */
public class Attr {

    static public Object val(Attribute a) {
        if (isd(a)) return dval(a);
        if (isi(a)) return ival(a);
        return sval(a);
    }
    static public Attribute min(List<Attribute> L) {
        Attribute min = L.get(0);
        for(Attribute a : L)
            if (a.compareTo(min) < 0)
                min = a;
        return min;
    }
    
    static public Attribute max(List<Attribute> L) {
        Attribute max = L.get(0);
        for(Attribute a : L)
            if (a.compareTo(max) > 0)
                max = a;
        return max;
    }

    static public int imin(List<Attribute> L) { return ival(min(L)); }
    static public int imax(List<Attribute> L) { return ival(max(L)); }
    
    static public double dmin(List<Attribute> L) { return dval(min(L)); }
    static public double dmax(List<Attribute> L) { return dval(max(L)); }
    
    static public boolean isd(Attribute a) { return Attribute.ask.isd(a); }
    static public boolean isi(Attribute a) { return Attribute.ask.isi(a); }
    static public boolean iss(Attribute a) { return Attribute.ask.iss(a); }

    static public double dval(Attribute a) {
        if (isd(a)) return ((Attribute.DAttribute)a).value;
        if (isi(a)) return ((Attribute.BIAttribute)a).value;
        return 0.0;
    }
    static public int ival(Attribute a) {
        if (isd(a)) return (int)((Attribute.DAttribute)a).value;
        if (isi(a)) return ((Attribute.BIAttribute)a).value;
        return 0;
    }
    static public String sval(Attribute a) {
        if (isd(a)) return Double.toString(((Attribute.DAttribute)a).value);
        if (isi(a)) return Integer.toString(((Attribute.BIAttribute)a).value);
        if (iss(a)) return ((Attribute.BSAttribute)a).value;
        return null;
    }  

    static public List<Integer> ivals(List<Attribute> L) {
        List<Integer> res = new ArrayList<>(L.size());
        for(Attribute a : L) res.add(ival(a));
        return res;
    }
    static public List<Double> dvals(List<Attribute> L) {
        List<Double> res = new ArrayList<>(L.size());
        for(Attribute a : L) res.add(dval(a));
        return res;
    }
    static public List<String> svals(List<Attribute> L) {
        List<String> res = new ArrayList<>(L.size());
        for(Attribute a : L) res.add(sval(a));
        return res;
    }

    public static List<Attribute> icol(int[] I) {
        List<Attribute> res = new ArrayList<>(I.length);
        for(Integer i : I)
            res.add(new Attribute.IAttribute(i));
        return res;
    }
    public static List<Attribute> dcol(double[] D) {
        List<Attribute> res = new ArrayList<>(D.length);
        for(Double d : D)
            res.add(new Attribute.DAttribute(d));
        return res;
    }
    public static List<Attribute> scol(String[] S) {
        List<Attribute> res = new ArrayList<>(S.length);
        for(String s : S)
            res.add(new Attribute.SAttribute(s));
        return res;
    }

}
