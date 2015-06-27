
package my.f;

/**
 * @author pvto https://github.com/pvto
 */
public final class Fn {

    public interface F1D<A>             { double eval(A a); }
    public interface F2D<A,B>           { double eval(A a, B b); }
    public interface F3D<A,B,C>         { double eval(A a, B b, C c); }
    public interface F4D<A,B,C,D>       { double eval(A a, B b, C c, D d); }
    public interface F5D<A,B,C,D,E>     { double eval(A a, B b, C c, D d, E e); }
    public interface F6D<A,B,C,D,E,F>   { double eval(A a, B b, C c, D d, E e, F f); }

    public interface FnD<A>     { double eval(A ... a); }

    
    public interface F1Db<A>            { double evbl(A a); }
    public interface F2Db<A,B>          { double evbl(A a, B b); }
    public interface F3Db<A,B,C>        { double evbl(A a, B b, C c); }
    public interface F4Db<A,B,C,D>      { double evbl(A a, B b, C c, D d); }
    public interface F5Db<A,B,C,D,E>    { double evbl(A a, B b, C c, D d, E e); }
    public interface F6Db<A,B,C,D,E,F>  { double evbl(A a, B b, C c, D d, E e, F f); }

    public interface FnDb<A>    { double evbl(A ... a); }
    
    
    public interface F1Dc<A>            { double evcl(A a); }
    public interface F2Dc<A,B>          { double evcl(A a, B b); }
    public interface F3Dc<A,B,C>        { double evcl(A a, B b, C c); }
    public interface F4Dc<A,B,C,D>      { double evcl(A a, B b, C c, D d); }
    public interface F5Dc<A,B,C,D,E>    { double evcl(A a, B b, C c, D d, E e); }
    public interface F6Dc<A,B,C,D,E,F>  { double evcl(A a, B b, C c, D d, E e, F f); }

    public interface FnDc<A>    { double evcl(A ... a); }
}
