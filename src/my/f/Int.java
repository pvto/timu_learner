package my.f;

/**
 * @author pvto https://github.com/pvto
 */
public class Int {

    static public int min(int a, int b, int c) {
        if (a < b) {
            return java.lang.Math.min(a, c);
        }
        return java.lang.Math.min(b, c);
    };

    static public int min(int[] A) {
        int min = Integer.MAX_VALUE;
        for(int i = 0; i < A.length; i++) {
            if (A[i] < min) { min = A[i]; }
        }
        return min;
    }

    static public int max(int[] A) {
        int max = Integer.MIN_VALUE;
        for(int i = 0; i < A.length; i++) {
            if (A[i] > max) { max = A[i]; }
        }
        return max;
    }
    
    static public int count(int[] A, int val) {
        int count = 0;
        for(int i = 0; i < A.length; i++) {
            if (A[i] == val) { count++; }
        }
        return count;
    }
}
