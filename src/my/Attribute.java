package my;

public interface Attribute extends Comparable {

    static public class ask {
        static public int 
                tCONT =      0,
                tBININT =    1,
                tINT =       1|2,
                tBINSTR =    4,
                tSTR =       4|8
                ;
        static public int type(Attribute a) {
            if (a instanceof Attribute.BIAttribute) {          return tBININT; } 
            else if (a instanceof Attribute.IAttribute) {      return tINT; } 
            else if (a instanceof Attribute.BSAttribute) {     return tBINSTR; } 
            else if (a instanceof Attribute.SAttribute) {      return tSTR; }
            else return 0;
        }
        static public boolean isd(Attribute a) { return a instanceof DAttribute; }
        static public boolean isi(Attribute a) { return (type(a) & tBININT) == tBININT; }
        static public boolean iss(Attribute a) { return (type(a) & tBINSTR) == tBINSTR; }
        
    }
    
    static public class clone {
        static public Attribute a(Attribute o) {
            if (o == MISSING) { return MISSING; }
            else if (o instanceof DAttribute) { return new DAttribute(((DAttribute)o).value); }
            else if (o instanceof BIAttribute) { return new BIAttribute(((BIAttribute)o).value); }
            else if (o instanceof IAttribute) { return new IAttribute(((IAttribute)o).value); }
            else if (o instanceof BSAttribute) { return new BSAttribute(((BSAttribute)o).value); }
            else if (o instanceof SAttribute) { return new SAttribute(((SAttribute)o).value); }
            throw new UnsupportedOperationException("unhandled Attribute type " + o.getClass());
        }
    };

    static public class as {
        static public double doublee(Attribute o) {
                if (o == MISSING) { return 0.0; }
                else if (o instanceof DAttribute) { return ((DAttribute)o).value; }
                else if (o instanceof BIAttribute) { return ((BIAttribute)o).value; }
                else if (o instanceof IAttribute) { return ((IAttribute)o).value; }
                throw new UnsupportedOperationException("toDouble:  unhandled Attribute type " + o.getClass());
        }
    }

    
    
//---attributes---
    static public final Attribute MISSING = new Attribute() {
  
        @Override
        public int compareTo(Object o) {
            if (o == MISSING) {
                return 0;
            }
            return -1;
        }

        @Override
        public boolean equals(Object obj) {
            return obj == MISSING;
        }

        @Override
        public int hashCode() {
            return -10101010; //To change body of generated methods, choose Tools | Templates.
        }
        
        
        @Override
        public String toString() {
            return "MISSING";
        }
    };

    static public class DAttribute implements Attribute {

        public DAttribute(double d) {
            this.value = d;
        }
        public double value;

        @Override
        public int compareTo(Object o) {
            if (!(o instanceof DAttribute)) {
                return -1;
            }
            double ov = ((DAttribute)o).value;
            if (value > ov) { return 1; }
            else if (value < ov) { return -1; }
            else { return 0; }
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof DAttribute && ((DAttribute)obj).value == value;
        }

        @Override
        public int hashCode() {
            return (int)Double.doubleToLongBits(value);
        }

        
        @Override
        public String toString() {
            return ""+value;
        }
    }

    static public class BIAttribute implements Attribute {

        public BIAttribute(int i) {
            this.value = i;
        }
        public int value;
        
        static boolean isZero(Attribute a) {
            if (BIAttribute.class.isAssignableFrom(a.getClass())
                        && ((BIAttribute)a).value == 0) {
                return true;
            }
            return BSAttribute.isZero(a);
        }

        @Override
        public int compareTo(Object o) {
            if (!(BIAttribute.class.isAssignableFrom(o.getClass()))) {
                return -1;
            }
            int ov = ((BIAttribute)o).value;
            if (value > ov) { return 1; }
            else if (value < ov) { return -1; }
            else { return 0; }
        }

        @Override
        public boolean equals(Object obj) {
            return BIAttribute.class.isAssignableFrom(obj.getClass()) 
                    && ((BIAttribute)obj).value == value;
        }

        @Override
        public int hashCode() {
            return value;
        }
        
        @Override
        public String toString() {
            return ""+value;
        }
    }

    static public class IAttribute extends BIAttribute {

        public IAttribute(int i) {
            super(i);
        }
    }

    static public class BSAttribute implements Attribute {

        public BSAttribute(String s) {
            this.value = s;
        }
        public String value;

        private static boolean isZero(Attribute a) {
            return BSAttribute.class.isAssignableFrom(a.getClass())
                        && ((BSAttribute)a).value.matches(Csv.ZEROREG);
        }
        
        @Override
        public int compareTo(Object o) {
            if (!(BSAttribute.class.isAssignableFrom(o.getClass()))) {
                return -1;
            }
            String ov = ((BSAttribute)o).value.toLowerCase();
            return value.toLowerCase().compareTo(ov); // happens to work for "0"/"1", "no"/"yes", "false"/"true" ...
        }

        @Override
        public boolean equals(Object obj) {
            return BSAttribute.class.isAssignableFrom(obj.getClass()) 
                    && ((BSAttribute)obj).value.equals(value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public String toString() {
            return value;
        }
    }

    static public class SAttribute extends BSAttribute {

        public SAttribute(String s) {
            super(s);
        }
    }


    
}
