package my.f;

/**
 * @author pvto https://github.com/pvto
 */
public class Obj {

    static public Object first(Object... A) {
        for(Object a : A) if (a != null) return a;
        return null;
    }
}
