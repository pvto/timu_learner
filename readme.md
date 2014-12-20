
##Introduction

This is the cover document for a data mining course work.  The main deliverable is a Java implementation of the DBSCAN clustering algorithm.

DBSCAN is a density based algorithm for clustering bounded datasets.  It is based on the notion of neighborhoods that are defined as sets of points within a specified radius from each other.  Within a neighborhood, there are core points and border points, and without, noise points that do not fall in any neighborhood.  Core points are points that have at least minPts points in their neighborhood, and border points are points that belong to a neighborhood but are not core points themselves.

There are slightly different versions of the DBSCAN algorithm, of which one is explained below.  The method runs as follows.  Select a suitable neighborhood size minPts, and a neighborhood radius Eps, as well as a proximity measure suitable for a dataset.  Calculate a distance matrix for the data points.  Classify all points initially as UNCLUSTERED.

1° Start by picking a random point that is yet unclassified.  Classify it as NOISE, a border point or a core point, by checking the appropriate row in the distance matrix.  If the point is NOISE or a border point, a next point is picked by random, until a core point is found.
2° Mark all neighbors of the core point into the current cluster.  If there are any core points within the neighborhood, proceed to 2° recursively with each of them, until the whole cluster is exhausted.
3° Go back to 1° and pick a new random point.  The algorithm ends when there are no more unclassified points to pick.

##References

Ester, Kriegel, Sander, Xu: A density-based algorithm for discovering clusters in large spatial databases with noise, Proceedings of KDD-96, 1996.

Han J., Kamber M.: Data Mining: Concepts and Techniques. Morgan Kaufmann, 2006.

##Course work definition

The aim was to produce an implementation of the DBSCAN algorithm that can handle data of different kinds.  Nominal data, continuous data and mixed data should be handled.  Dataset and attribute handling, as well as proximity measuring, was based on the first course work part of the TIMU course.

A second aim was to implement an algorithm that suggests a good Eps value for a dataset, based on a user supplied minPts value, the latter used as a k-value in k-distance calculation by the said algorithm.

Additionally, an implementation of a classic Levenshtein string distance algorithm was created for testing DBSCAN on groups of similar words.

##The implementation

The implementation was written in Java.  It can be accessed fully from a web interface at https://github.com/pvto/timu_learner.  To clone the code into a local git repository, run:

```
mkdir timu_learner
git clone https://github.com/pvto/timu_learner.git timu_learner
```

To build the project from sources, install Java 8 and Netbeans, run Netbeans, then from “File” menu > “Open project” > [path.to/]timu_learner.  From “Run” menu > “Clean and build project”.
Code is organized in two packages in the following fashion.

```
*Package*
my      dataset support and helper methods
my.clust    clustering algorithms
my.f        generic helper methods for different datatypes and lists
```

Dataset data is organized in the following classes.

```
*Class*
Dataset dataset support and caching of computed statistics (mean, var, …)
Item        one item (pattern) in a dataset
Attribute   one attribute in an item (subclassed, descriptions follow)
Attribute.Missing       a missing value
Attribute.Dattribute    a continuous value (64bit floating point)
Attribute.BIAttribute   an integer based binary value
Attribute.Iattribute    a discreet (integer) value 
Attribute.BSAttribute   a string based binary value
Attribute.Sattribute    a nominal (string) value
```

Single-attribute proximity is datatype based:

```
*Class*
AttrProxMetric.DMetric      distance between two doubles
AttrProxMetric.IMetric      distance between two integers
AttrProxMetric.SMetric      string equality distance {1,0}
AttrProxMetric.Levenshtein  Levenshtein string distance
```

Item distance (proximity) measures are organized as follow.

```
*Class*
ProximityMeasure        abstract interface
ProximityMeasure.EuclideanDistance  euclidean distance
ProximityMeasure.ManhattanDistance  manhattan distance
 . . .
ProximityMeasure.NVDM           mixed NVDM dm
```

##Testing and test results 

The DBSCAN clustering algorithm was unit tested with some hand-written datasets to check that it behaves as expected.  See the class test/my/clust/DBSCANTest.java for examples of running the algorithm.

Unit tests included a hand coded labyrinth and a big dataset of 10000 items, as well as a word list for testing Levenshtein distance based clustering.  A test for suggestEps() function was created and tested over the labyrinth dataset, giving a working Eps value of 0.58, as opposed to a user-estimated 0.6 used in the test class.

Test results were as expected; the implementation of DBSCAN clustered items correctly with euclidean, manhattan, and levenshtein proximities.

To interactively test the code, the following procedure is suggested.

Install Java 8 and Groovy.

```
$ groovysh -cp dist/kmeans.jar 
 > import java.nio.charset.* 
 > import my.* 
 > import my.clust.* 
 > import my.f.*
 > dataset = new Csv().from(new FileInputStream("data/clust.txt"), Charset.forName("UTF-8"), ",")
 > dataset.classAttribute = Dataset.UNSUPERVISED
 > dbscan = new DBSCAN() 
 > dbscan.suggestEps(3, dataset, ProximityMeasure.Euclidean, null)
===> 3.3384776310850235
 > dbscan.dbscan(dataset, 3, 3.34, ProximityMeasure.Euclidean, null)
===> clusters: 3
  [[-10, 0], [-10, -10]]
  [[5, 0], [6, 0], [5, 1], [6, 1], [7, 0]]
  [[1, 0], [0, 0], [1, 1]]
```

##Conclusion

My implementations of DBSCAN and Knn nearest neighbour method algorithms should allow for easy extension to other learning algorithms, because of a good support for proximity measures and other underlying datatype operations.

The course work was not too straining, because the choice of subject was very free.  It took about 8 hours from me to write the DBSCAN code and associated tests, and the cover document, based on the first course work document.