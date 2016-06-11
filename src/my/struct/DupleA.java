package my.struct;

import my.f.Obj;


public class DupleA<T extends Comparable,U> implements Comparable<DupleA> {

    public T t;
    public U u;
    public DupleA<T,U> next;
    
    public DupleA(T t, U u) { this.t = t; this.u = u; }

    
    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof DupleA)) return false;
        DupleA<T,U> b = (DupleA<T,U>) obj;
        return Obj.nullSafeEquals(t, b.t)
                && Obj.nullSafeEquals(u, b.u);
    }

    @Override
    public int hashCode()
    {
        return Obj.nullSafeHashCode(t) * 31 
                + Obj.nullSafeHashCode(u);
    }
    
    
    
    @Override public String toString() { return String.format("<%s:%s>", t, u); }

    @Override
    public int compareTo(DupleA o) {
        return t.compareTo(o.t);
    }
}