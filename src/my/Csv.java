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

public class Csv {

    static public final String[] BINSTRS = {"1","0","true","false","yes","no"};
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
                int PREVTYP = 0;
                if (last != null) {
                    Attribute i_attr = last.attributes.get(i);
                    if (i_attr instanceof Attribute.BIAttribute) {
                        PREVTYP = 1;
                    } else if (i_attr instanceof Attribute.IAttribute) {
                        PREVTYP = 1 | 2;
                    } else if (i_attr instanceof Attribute.BSAttribute) {
                        PREVTYP = 4;
                    } else if (i_attr instanceof Attribute.SAttribute) {
                        PREVTYP = 4 | 8;
                    }
                }
                if ("".equals(s)) {
                    item.attributes.add(Attribute.MISSING);
                } else if (PREVTYP == 4 && in(BINSTRS, s.toLowerCase())) {
                    item.attributes.add(new Attribute.BSAttribute(s));
                } else if (PREVTYP == 4) {
                    for(Item previ : ds.items) {
                        Attribute.BSAttribute bs = (Attribute.BSAttribute) previ.attributes.get(i);
                        previ.attributes.set(i, new Attribute.SAttribute(bs.value));
                    }
                    item.attributes.add(new Attribute.SAttribute(s));
                } else if (PREVTYP == (4 | 8)) {
                    item.attributes.add(new Attribute.SAttribute(s));
                } else {
                    try {
                        Double d = Double.parseDouble(s);
                        int iv = d.intValue();
                        boolean isi = new Double(iv).equals(d);
                        if ((last == null || (PREVTYP & 1) == 1) && isi) {
                            boolean bisi = iv == 0 || iv == 1;
                            if (bisi && (last == null || PREVTYP == 1)) {
                                item.attributes.add(new Attribute.BIAttribute(iv));
                            } else {
                                if (PREVTYP == 1) {
                                    for (Item previ : ds.items) {
                                        Attribute.BIAttribute ia = (Attribute.BIAttribute) previ.attributes.get(i);
                                        previ.attributes.set(i, new Attribute.IAttribute(ia.value));
                                    }
                                }
                                item.attributes.add(new Attribute.IAttribute(iv));
                            }
                        } else {
                            item.attributes.add(new Attribute.DAttribute(d));
                            if ((PREVTYP & 1) == 1) {
                                for (Item previ : ds.items) {
                                    Attribute.IAttribute ia = (Attribute.IAttribute) previ.attributes.get(i);
                                    previ.attributes.set(i, new Attribute.DAttribute(ia.value));
                                }
                            }
                        }
                    } catch (NumberFormatException e) {
                        item.attributes.add(new Attribute.SAttribute(s));
                        if ((PREVTYP & 4) != 4) {
                            for (Item previ : ds.items) {
                                Attribute a = previ.attributes.get(i);
                                previ.attributes.set(i, new Attribute.SAttribute(
                                        a instanceof Attribute.IAttribute ? String.valueOf(((Attribute.IAttribute) a).value)
                                                : String.valueOf(((Attribute.IAttribute) a).value)));
                            }
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
