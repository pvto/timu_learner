package my;

import static my.Attribute.MISSING;
import my.f.Dist;

/**
 * @author pvto https://github.com/pvto
 */
public interface AttrProxMetric {

    
        double difference(Attribute a, Attribute b);


        
        

    static final public AttrProxMetric DoNotMeasureMetric = new AttrProxMetric() {
        @Override
        public double difference(Attribute a, Attribute b) {
            return 0.0;
        }
    };
    
    static final public AttrProxMetric DMetric = new AttrProxMetric() {

        @Override
        public double difference(Attribute a, Attribute b) {
            if (a == MISSING || b == MISSING) {
                return 1.0;
            }
            return Math.abs(((Attribute.DAttribute) a).value - ((Attribute.DAttribute) b).value);
        }
    };

    static final public AttrProxMetric BIMetric = new AttrProxMetric() {

        @Override
        public double difference(Attribute a, Attribute b) {
            if (a == MISSING || b == MISSING) {
                return 1.0;
            }
            return Math.abs(((Attribute.BIAttribute) a).value - ((Attribute.BIAttribute) b).value);
        }
    };
    
    static final public AttrProxMetric IMetric = BIMetric;

    static final public AttrProxMetric BSMetric = new AttrProxMetric() {

        @Override
        public double difference(Attribute a, Attribute b) {
            if (a == MISSING || b == MISSING) {
                return 1.0;
            }
            String s = ((Attribute.BSAttribute) a).value;
            String t = ((Attribute.BSAttribute) b).value;
            return s.equals(t) ? 0.0 : 1.0;
        }
    };
    
    static final public AttrProxMetric SMetric = BSMetric;
    
    static final public AttrProxMetric ClassMetric  = new AttrProxMetric() { 

        @Override
        public double difference(Attribute a, Attribute b) {
            if (a == MISSING || b == MISSING) {
                return 1.0;
            }
            else if (a == b) {
                return 0.0;
            }
            else if (Attribute.BSAttribute.class.isAssignableFrom(a.getClass()) 
                    && Attribute.BSAttribute.class.isAssignableFrom(b.getClass())) {
                return BSMetric.difference(a, b);
            }
            else if (Attribute.BIAttribute.class.isAssignableFrom(a.getClass()) 
                    && Attribute.BIAttribute.class.isAssignableFrom(b.getClass())) {
                return BIMetric.difference(a, b);
            }
            return DMetric.difference(a, b);

        }
    
    };
    
  
    static final public AttrProxMetric Levenshtein = new AttrProxMetric() {

        @Override
        public double difference(Attribute a, Attribute b) {
            if (a == MISSING || b == MISSING) {
                return 1000.0;  //find a better value
            }
            else if (a == b) {
                return 0.0;
            }
            String A = ((Attribute.BSAttribute)a).value;
            String B = ((Attribute.BSAttribute)b).value;
            return Dist.levenshteinDistance(A, B);
        }
        
    };

}
