package my.struct;

import java.util.TreeMap;

/**
 *
 * @author Paavo Toivanen https://github.com/pvto
 */
public class FloatIntTree<T> {
        
    public int size = 0;
    public Node0 root = new Node0();

    private static int
            N0,
            N1,
            N2,
            N3,
            N4,
            N5,
            N0shr,
            N1shr,
            N3shr,
            N4shr,
            N0Nbm,
            N0Padd,
            N1bm,
            N2bm,
            N4bm,
            N5bm
            ;
    static{ init(17, 8, 5, 5); }
    
    private static void init(int N0, int N1, int N3, int N4)
    {
        if (N0 + N1 > 28) throw new RuntimeException("must have N0 + N1 <= 28");
        if (N3 + N4 > 12) throw new RuntimeException("must have N3 + N4 <= 12");
        FloatIntTree.N0 = N0;
        FloatIntTree.N1 = N1;
        FloatIntTree.N2 = 32 - N0 + 1 - N1;
        FloatIntTree.N3 = N3;
        FloatIntTree.N4 = N4;
        FloatIntTree.N5 = 15 - N3 - N4;
	N0shr = 32 - N0;
	N1shr = N1;
	N3shr = 15 - N3;
        N4shr = N5;
	N0Nbm = (1 << (N0 - 1)) - 1;
	N0Padd = (1 << (N0 - 1));
	N1bm = (1 << (32 - N0 + 1)) - 1;
	N2bm = (1 << (32 - N0 + 1 - N1)) - 1;
	N4bm = (1 << N4) - 1;
        N5bm = (1 << N5) - 1;
        
    }
    public static long foo = 0L;
    public void put(float rank, T t)
    {
        int 
                i = mapIntPart(rank),
                d = mapDecimalPart(rank)
                ;
        root.put(i, d, rank, t);
        size++;
    }
    
    public static int mapIntPart(float f)
    {
        return (int)f;
    }
    
    public static int mapDecimalPart(float f)
    {
        return (int) ((f - (int)f) * 32768f);
    }

    
    public static class Node0 {
        
        public Node1[] 
                children = new Node1[1<<N0]
                ;
        {foo += children.length;}
        
        public void put(int ihash, int dhash, float rank, Object o)
        {
            int x;
            if ((ihash & 0x80000000) == 0x80000000)
            {
                x = (ihash >> N0shr) & N0Nbm;
            }
            else
            {
                x = (ihash >> N0shr) + N0Padd;
            }
            if (children[x] == null)
                children[x] = new Node1();
            children[x].put(ihash, dhash, rank, o);
        }
    }
    
    public static class Node1 {
        
        public Node2[] children = new Node2[1<<N1];
        {foo += children.length;}
        
        public void put(int ihash, int dhash, float rank, Object o)
        {
            int x = (ihash & N1bm) >> N1shr;
            if (children[x] == null)
                children[x] = new Node2();
            children[x].put(ihash, dhash, rank, o);
        }
    }
    
    public static class Node2 {
        
        public Node3[] children = new Node3[1<<N2];
        {foo += children.length;}
        
        public void put(int ihash, int dhash, float rank, Object o)
        {
            int x = (ihash & N2bm);
            if (children[x] == null)
                children[x] = new Node3();
            children[x].put(ihash, dhash, rank, o);
        }
    }
    
    public static class Node3 {
        
        public Node4[] children = new Node4[1<<N3];
        {foo += children.length;}
        
        public void put(int ihash, int dhash, float rank, Object o)
        {
            int x = (dhash >> N3shr);
            if (children[x] == null)
                children[x] = new Node4();
            children[x].put(ihash, dhash, rank, o);
        }
    }
 
    public static class Node4<T> {
        
        public Node5[] children = new Node5[1<<N4];
        {foo += children.length;}
        
        public void put(int ihash, int dhash, float rank, Object o)
        {
            int x = ((dhash >>> N4shr) & N4bm);
            if (children[x] == null)
                children[x] = new Node5();
            children[x].put(ihash, dhash, rank, o);
        }
    }

    public static class Node5<T> {
        
        public Node6[] children = new Node6[1<<N5];
        {foo += children.length;}
        
        public void put(int ihash, int dhash, float rank, Object o)
        {
            int x = (dhash & N5bm);
            if (children[x] == null)
                children[x] = new Node6();
            children[x].put(ihash, dhash, rank, o);
        }
    }
    
    public static class Node6<T> {
        
        public FUPair<T> firstChild;
        
        public void put(int ihash, int dhash, float rank, Object o)
        {
            
            FUPair<T> x = new FUPair<>(rank, (T)o);
            if (firstChild == null)
            {
                firstChild = x;
                return;
            }
            if (firstChild.t > rank)
            {
                x.next = firstChild;
                firstChild = x;
                return;
            }
            FUPair<T>
                    y0 = firstChild,
                    y = firstChild.next
                    ;
            while(y != null && y.t < rank)
            {
                y0 = y;
                y = y.next;
            }
            y0.next = x;
            x.next = y;
        }
    }

    public static class FUPair <U> implements Comparable<FUPair> {
        public float t;
        public U u;
        public FUPair next;
        
        public FUPair(float t, U u)
        {
            this.t = t;
            this.u = u;
        }
        
        @Override
        public int compareTo(FUPair o) {
            if (t == o.t) return 0;
            if (t > o.t) return 1;
            return -1;
        }
    }
    
    public static void main(String[] args)
    {
        FloatIntTree fit = new FloatIntTree();
        System.out.println(fit.mapDecimalPart(2.0f));
        System.out.println(fit.mapDecimalPart(2.1f));
        System.out.println(fit.mapDecimalPart(2.9f));
        System.out.println(fit.mapDecimalPart(2.9999f));
        System.out.println(fit.mapDecimalPart(2.999999f));
        System.out.println(fit.mapDecimalPart(2.9999999f));
        System.out.println(fit.mapIntPart(2.9999999f));
        System.out.println("");
        fit.put(2.0f, 2);
        fit.put(2.1f, 2);
        fit.put(-1.0f, -1);
        fit.put(65535.1f, 65535);
        System.out.println(fit.size);
        fit.foo = 0;
        
        int N = (int)1e6;
        float MULT = 10000f;
        
        for(int round = 0; round < 2; round++)
        {
            long 
                    start = System.currentTimeMillis(),
                    mid = 0;

            if (round == 0)
            {
                TreeMap tm = new TreeMap();
                for (int i = 0; i < N; i++)
                {
                    float rank = (float) Math.random() * MULT;
                    tm.put(rank, i);
                }
                
                mid = System.currentTimeMillis();
                
                for(Object o : tm.entrySet())
                    ;
            } 
            else if (round == 1)
            {
                fit = new FloatIntTree();
                for (int i = 0; i < N; i++)
                {
                    float rank = (float) Math.random() * MULT;
                    fit.put(rank, i);
                }
                
                mid = System.currentTimeMillis();
                
                for(Node1 n1 : fit.root.children) if (n1 != null)
                    for(Node2 n2 : n1.children) if (n2 != null)
                        for(Node3 n3 : n2.children) if (n3 != null)
                            for(Node4 n4 : n3.children) if (n4 != null)
                                for(Node5 n5 : n4.children) if (n5 != null)
                                    for(Node6 n6 : n5.children) if (n6 != null)
                                    {
                                        FUPair fu = n6.firstChild;
                                        while(fu != null)
                                            fu = fu.next;
                                    }
            }
            
            long end = System.currentTimeMillis();
            System.out.println(N + "/" + MULT + ": " + (mid - start) + "  " + (end - mid) + "  " + (end - start));
            System.out.println(fit.foo * 4 + " bytes reserved");
        }
    }
}
