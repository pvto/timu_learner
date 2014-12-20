package my.f;

/**
 * @author pvto https://github.com/pvto
 */
public class Doub {

    
    static public double[] range(double start, double end, double step) {
        double off = start;
        int n = (int)Math.floor((end - start) / step);
        double[] res = new double[n];
        for (int i = 0; i < res.length; i++) {
            res[i] = off;
            off += step;
        }
        return res;
    }

    static public double[] div(double[] divident, double[] divisor) {
        double[] res = new double[divident.length];
        for(int i = 0; i < res.length; i++)
            res[i] = divident[i] / divisor[i];
        return res;
    }

    static public double sum(double[] L) {
        double sum = 0.0;
        for (int i = 0; i < L.length; i++) {
            sum += L[i];
        }
        return sum;
    }

    static public double avg(double[] L) {
        return sum(L) / L.length;
    }
    
    static public double[] ndy(double[] y) {
        double[] res = new double[y.length - 1];
        for(int i = 0; i < res.length; i++)
            res[i] = y[i + 1] - y[i];
        return res;
    }
    
}
