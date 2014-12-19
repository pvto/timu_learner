package my.f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author pvto https://github.com/pvto
 */
public class Str {

    
    static public String replicate(char c, int n) {
        char[] cc = new char[n];
        Arrays.fill(cc, c);
        return new String(cc);
    }
    static public String pad(String s, int n, char c) {
        if (s.length() > n) { return s.substring(0, n); }
        return s + replicate(c, n - s.length());
    }
    
    
    static public List<String> pad(List<String> L, int n, char c) {
        List<String> res = new ArrayList<>(L.size());
        for(String s : L) res.add(pad(s, n, c));
        return res;
    } 

    static public int maxlen(List<String> prints) {
        int maxlen = 0;
        for(String s : prints) if (s.length() > maxlen) maxlen = s.length();
        return maxlen;
    }
}
