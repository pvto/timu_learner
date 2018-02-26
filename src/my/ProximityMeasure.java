package my;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import my.Attribute.BIAttribute;
import static my.AttrProxMetric.BIMetric;
import static my.AttrProxMetric.BSMetric;
import static my.AttrProxMetric.DMetric;
import static my.AttrProxMetric.DoNotMeasureMetric;
import static my.AttrProxMetric.IMetric;

public interface ProximityMeasure {
//
//    static enum Metrics {
//
//        Manhattan(DistanceMetric.Manhattan),
//        Euclidean(DistanceMetric.Euclidean),
//        SimpleMatchingCoefficient(DistanceMetric.SimpleMatchingCoefficient),
//        JackardSimilarityCoefficient(DistanceMetric.JackardSimilarityCoefficient),
//        DiceSimilarityCoefficient(DistanceMetric.DiceSimilarityCoefficient),
//        QualitativeSimilarity(DistanceMetric.QualitativeSimilarity),
//        HEOM(DistanceMetric.HEOM),
//        ;
//        public final DistanceMetric distanceMetric;
//        private Metrics(DistanceMetric m) {
//            this.distanceMetric = m;
//        }
//    }


    double distance(List<AttrProxMetric> metrics, Item a, Item b, Dataset ds);


//implementations follow...

//---proximity measures---


//---quantitative distances---
    static final public ProximityMeasure Manhattan = new ProximityMeasure() {

        @Override
        public double distance(List<AttrProxMetric> metrics, Item a, Item b, Dataset ds) {
            double d = 0.0;
            int n = a.attributes.size();
            for (int i = n - 1; i >= 0; i--) {
                if (Dataset.classAttribute(ds) == n) {
                    continue;
                }
                AttrProxMetric m = metrics.get(i);
                d += m.difference(a.attributes.get(i), b.attributes.get(i));
            }
            return d / n;
        }

    };

    static final public ProximityMeasure Euclidean = new ProximityMeasure() {

        @Override
        public double distance(List<AttrProxMetric> metrics, Item a, Item b, Dataset ds) {
            double d = 0.0;
            int n = a.attributes.size();
            for (int i = n - 1; i >= 0; i--) {
                if (Dataset.classAttribute(ds) == n) {
                    continue;
                }
                AttrProxMetric m = metrics.get(i);
                double adist = m.difference(a.attributes.get(i), b.attributes.get(i));
                d += adist * adist;
            }
            return Math.sqrt(d);
        }
    };


    static public class NDimDist implemets ProximityMeasure {

        public double exp = 1.0;

        @Override
        public double distance(List<AttrProxMetric> metrics, Item a, Item b, Dataset ds) {
            double d = 0.0;
            int n = a.attributes.size();
            for (int i = n - 1; i >= 0; i--) {
                if (Dataset.classAttribute(ds) == n) {
                    continue;
                }
                AttrProxMetric m = metrics.get(i);
                double adist = m.difference(a.attributes.get(i), b.attributes.get(i));
                d += Math.pow(adist, exp);
            }
            return Math.pow(d, 1.0 / exp);
        }
    };

    static final public ProximityMeasure CubicDist = new NDimDist() {{{ this.exp = 3.0; }}};

    static final public ProximityMeasure QuadraticDist = new NDimDist() {{{ this.exp = 4.0; }}};

//---binary similarities---

    static final public ProximityMeasure SimpleMatchingCoefficient = new ProximityMeasure() {

        @Override
        public double distance(List<AttrProxMetric> metrics, Item a, Item b, Dataset ds) {
            double num = 0.0;
            int n = a.attributes.size();
            for (int i = n - 1; i >= 0; i--) {
                if (Dataset.classAttribute(ds) == n) {
                    continue;
                }
                AttrProxMetric m = metrics.get(i);
                double adist = m.difference(a.attributes.get(i), b.attributes.get(i));
                if (adist == 0) {
                    num++;
                }
            }
            return num / Dataset.nonClassAttributeCount(ds);
        }
    };

    static final public ProximityMeasure JackardSimilarityCoefficient = new ProximityMeasure() {

        @Override
        public double distance(List<AttrProxMetric> metrics, Item a, Item b, Dataset ds) {
            double num = 0.0, den = 0.0;
            int n = a.attributes.size();
            for (int i = n - 1; i >= 0; i--) {
                if (Dataset.classAttribute(ds) == n) {
                    continue;
                }
                AttrProxMetric m = metrics.get(i);
                double adist = m.difference(a.attributes.get(i), b.attributes.get(i));
                if (adist == 0) {
                    if (!BIAttribute.isZero(a.attributes.get(i))) {
                        num++;
                        den++;
                    }
                } else {
                    den++;
                }
            }
            return num / den;
        }
    };

    static final public ProximityMeasure DiceSimilarityCoefficient = new ProximityMeasure() {

        @Override
        public double distance(List<AttrProxMetric> metrics, Item a, Item b, Dataset ds) {
            double num = 0.0, den = 0.0;
            int n = a.attributes.size();
            for (int i = n - 1; i >= 0; i--) {
                if (Dataset.classAttribute(ds) == n) {
                    continue;
                }
                AttrProxMetric m = metrics.get(i);
                double adist = m.difference(a.attributes.get(i), b.attributes.get(i));
                if (adist == 0) {
                    if (!BIAttribute.isZero(a.attributes.get(i))) {
                        num+=2;
                        den+=2;
                    }
                } else {
                    den++;
                }
            }
            return num / den;
        }
    };

//---qualitative similarities---

    static final public ProximityMeasure QualitativeSimilarity = Manhattan;


//---mixed similarities

    static final public ProximityMeasure HEOM = new ProximityMeasure() {

        @Override
        public double distance(List<AttrProxMetric> metrics, Item a, Item b, Dataset ds) {
            double d = 0.0;
            int n = a.attributes.size();
            for (int i = n - 1; i >= 0; i--) {
                if (Dataset.classAttribute(ds) == i) {
                    continue;
                }
                AttrProxMetric m = metrics.get(i);
                double adist = m.difference(a.attributes.get(i), b.attributes.get(i));
                if (m == DMetric || m == IMetric) {
                    //|a-b|/range
                    double[] range = ds.range(i);
                    d = d + Math.abs(adist) / (range[1] - range[0]);
                } else {
                    d = d + adist*adist;
                }
            }
            return Math.sqrt(d);
        }
    };


    static final public ProximityMeasure HVDM_1 = new HVDM(1.0);
    static final public ProximityMeasure HVDM_2 = new HVDM(2.0);

    static public class HVDM implements ProximityMeasure {
        public final double NOMEXP;
        public HVDM(double nomExp) { this.NOMEXP = nomExp; }
        @Override
        public double distance(List<AttrProxMetric> metrics, Item a, Item b, Dataset ds) {
            double d = 0.0;
            int n = a.attributes.size();
            int clz = Dataset.classAttribute(ds);
            Set<Attribute> C = Dataset.distinctAttributeSet(ds, clz);
            for (int i = n - 1; i >= 0; i--) {
                if (Dataset.classAttribute(ds) == i) {
                    continue;
                }
                AttrProxMetric m = metrics.get(i);
                Attribute aa = a.attributes.get(i);
                Attribute ba = b.attributes.get(i);
                double adist = m.difference(aa, ba);
                if (aa == Attribute.MISSING || ba == Attribute.MISSING) {
                    d = d + 1.0;
                } else if (m == DMetric || m == IMetric) {
                    d = d + Math.abs(adist) / (4.0 * ds.std(i));
                } else {    // HVDM for nominal attributes:  sqrt(sum[c](P[xa,c] - P[ya,c])^n)
                    for(Attribute c : C) {
                        double Pa = Dataset.conditionalProb(ds, i, aa, clz, c);
                        double Pb = Dataset.conditionalProb(ds, i, ba, clz, c);
                        d += Math.pow(Math.abs(Pa - Pb), NOMEXP);
                    }
                }
            }
            return Math.sqrt(d);
        }

    };



    /**
     * a validity check for items
     */
    static public class check {

        static public void correspondence(Item a, Item b) {
            int aS = a.attributes.size();
            int bS = b.attributes.size();
            if (aS != bS) {
                throw new RuntimeException("attribute lists not of same size: " + aS + " and " + bS);
            }
            for (int i = a.attributes.size() - 1; i >= 0; i--) {
                Class cA = a.attributes.get(i).getClass();
                Class cB = b.attributes.get(i).getClass();
                if (cA != cB) {
                    throw new RuntimeException("different attribute types " + cA + " and " + cB);
                }
            }
        }
    }
}
