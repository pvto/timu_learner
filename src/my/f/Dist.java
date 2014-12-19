package my.f;

import java.util.Arrays;

/**
 * @author pvto https://github.com/pvto
 */
public class Dist {

        static public int levenshteinDistance(String s, String t) {

            int     m = s.length(),
                    n = t.length()
                ;
            int[][] d = new int[m + 1][n + 1];
            for(int[] d0 : d)
                Arrays.fill(d0, 0);

            for (int i = 0; i <= m; i++) {
                d[i][0] = i;
            }

            for(int j = 0; j <= n; j++) {
                d[0][j] = j;
            }

            for(int j = 1; j <= n; j++) {
                for (int i = 1; i <= m; i++) {
                    if (s.charAt(i-1) == t.charAt(j-1)) {
                      d[i][j] = d[i-1][j-1];       // no operation required
                    } else {
                        d[i][j] = Int.min
                        (
                            d[i-1][j] + 1,  // a deletion
                            d[i][j-1] + 1,  // an insertion
                            d[i-1][j-1] + 1 // a substitution
                        );
                    }
                }
            }

            return d[m][n];
            
        }

}
