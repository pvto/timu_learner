package my;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import static my.Attribute.ask.*;

public class Csv {

    static public final String[] BINSTRS = {"true","false","yes","no"};
    static public final String ZEROREG = "0|false|no";
    
    public Dataset from(Iterator<String> it, String separator) {
        Dataset ds = new Dataset();
        Item last = null;
        while (it.hasNext()) {
            String li = it.next();
            if (li.trim().startsWith("#")) {
                continue;
            }
            String[] ss = li.split(separator);
            if (ss.length == 1 && ss[0].trim().isEmpty()) {
                continue;
            }
            Item item = new Item();
            item.attributes = new ArrayList<Attribute>(ss.length);
            int i = 0;
            for (String s : ss) {
                s = s.trim();
                int PREVTYP = tCONT;
                if (last != null) {
                    Attribute preva = last.attributes.get(i);
                    PREVTYP = Attribute.ask.type(preva);
                }
                if ("".equals(s)) {
                    item.attributes.add(Attribute.MISSING);
                } else if ((last == null || PREVTYP == tBINSTR) && in(BINSTRS, s.toLowerCase())) {
                    item.attributes.add(new Attribute.BSAttribute(s));
                } else if (PREVTYP == tBINSTR) {
                    conv(ds.items, i, PREVTYP, tSTR);
                    item.attributes.add(new Attribute.SAttribute(s));
                } else if (PREVTYP == tSTR) {
                    item.attributes.add(new Attribute.SAttribute(s));
                } else {
                    try {
                        Double d = Double.parseDouble(s);
                        int iv = d.intValue();
                        boolean isi = new Double(iv).equals(d);
                        if ((last == null || (PREVTYP & tBININT) == tBININT) && isi) {
                            boolean bisi = iv == 0 || iv == 1;
                            if (bisi && (last == null || PREVTYP == tBININT)) {
                                item.attributes.add(new Attribute.BIAttribute(iv));
                            } else {
                                if (PREVTYP == tBININT) {
                                    conv(ds.items, i, PREVTYP, tINT);
                                }
                                item.attributes.add(new Attribute.IAttribute(iv));
                            }
                        } else {
                            item.attributes.add(new Attribute.DAttribute(d));
                            if ((PREVTYP & tBININT) == tBININT) {
                                conv(ds.items, i, PREVTYP, tCONT);
                            }
                        }
                    } catch (NumberFormatException e) {
                        item.attributes.add(new Attribute.SAttribute(s));
                        if ((PREVTYP & tSTR) != tSTR) {
                            conv(ds.items, i, PREVTYP, tSTR);
                        }
                    }
                }
                i++;
            }
            ds.items.add(item);
            last = item;
        }
        return ds;
    }

    static private void conv(List<Item> items, int column, int from, int to) {
        for(Item previ : items) {
            Attribute a = previ.attributes.get(column);
            int oldtype = Attribute.ask.type(a);
            if (from != oldtype) {
                continue;
            }
            switch(from) {
                case 0:
                    if (to == tSTR) { previ.attributes.set(column, new Attribute.SAttribute(Double.toString(((Attribute.DAttribute)a).value))); }
                    break;
                case 1 | 2:
                    if (to == tINT) { previ.attributes.set(column, new Attribute.IAttribute(((Attribute.BIAttribute)a).value)); }
                    else
                    if (to == tCONT) { previ.attributes.set(column, new Attribute.DAttribute(((Attribute.BIAttribute)a).value)); }
                    else
                    if (to == tSTR) { previ.attributes.set(column, new Attribute.SAttribute(Integer.toString(((Attribute.IAttribute)a).value))); }
                    break;
                case 4 | 8: 
                    if (to == tBINSTR) { previ.attributes.set(column, new Attribute.BSAttribute(((Attribute.BSAttribute)a).value)); }
                    else
                    if (to == tSTR) { previ.attributes.set(column, new Attribute.SAttribute(((Attribute.BSAttribute)a).value)); }
                    break;
            }
        }
    }
    
    private static boolean in(String[] ss, String s) {
        for(String x : ss) {
            if (x.equals(s)) {
                return true;
            }
        }
        return false;
    }
    
    public Dataset from(InputStream in, Charset cs, String separator) {
        InputStreamReader ir = new InputStreamReader(in, cs);
        return from(ir, separator);
    }
    
    public Dataset from(Reader r, String separator) {
        final BufferedReader br = ((r instanceof BufferedReader) ? 
                (BufferedReader)r : new BufferedReader(r));
        Iterator<String> it = new Iterator<String>() {
            String cache = null;

            @Override
            public boolean hasNext() {
                if (cache != null) {
                    return true;
                }
                cache = read();
                return cache != null;
            }

            private String read() {
                try {
                    return br.readLine();
                } catch (IOException e) {
                    try {
                        br.close();
                    } catch (IOException c) {
                    }
                }
                return null;
            }

            @Override
            public String next() {
                if (cache != null) {
                    String tmp = cache;
                    cache = null;
                    return tmp;
                }
                return read();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported.");
            }

        };
        return from(it, separator);
    }

    public Dataset from(File f, Charset c, String separator) throws FileNotFoundException {
        FileInputStream fin = new FileInputStream(f);
        return from(fin, c, separator);
    }
}
