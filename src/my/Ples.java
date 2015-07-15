
package my;

import static my.f.Obj.nullSafeEquals;
import static my.f.Obj.nullSafeHashCode;

/**
 * @author pvto https://github.com/pvto
 */
public class Ples {

    
    
    public static class Tuple<A,B> {
        public A a;
        public B b;
        public Tuple(A a, B b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Tuple))
                return false;
            Tuple t = (Tuple)obj;
            return nullSafeEquals(a, t.a) && nullSafeEquals(b, t.b);
        }

        @Override
        public int hashCode() {
            return nullSafeHashCode(a) * 21 + nullSafeHashCode(b);
        }
        
    }
    
    
    public static class Triple<A,B,C> {
        public A a;
        public B b;
        public C c;
        public Triple(A a, B b, C c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Triple))
                return false;
            Triple t = (Triple)obj;
            return nullSafeEquals(a, t.a) 
                    && nullSafeEquals(b, t.b)
                    && nullSafeEquals(c, t.c);
        }

        @Override
        public int hashCode() {
            return (nullSafeHashCode(a) * 21 
                    + nullSafeHashCode(b)) * 21 
                    + nullSafeHashCode(c);
        }
    }
}
