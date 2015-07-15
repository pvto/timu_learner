package my.f;

/**
 * @author pvto https://github.com/pvto
 */
public class Obj {

    static public Object first(Object... A) {
        for(Object a : A) if (a != null) return a;
        return null;
    }
    
    static public boolean nullSafeEquals(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;
        return a.equals(b);
    }
    
    static public int nullSafeHashCode(Object a) {
        if (a == null) return 0;
        return a.hashCode();
    }
}
