/**
 * Created by Stepan on 13.10.2016.
 */
public class Levenshtein
{
    public static int distance(String s, String t)
    {
        int cost;
        int len_s = s.length();
        int len_t = t.length();

  /* base case: empty strings */
        if (len_s == 0) return len_t;
        if (len_t == 0) return len_s;

  /* test if last characters of the strings match */
        if (s.charAt(len_s-1) == t.charAt(len_t-1))
            cost = 0;
        else
            cost = 1;

  /* return minimum of delete char from s, delete char from t, and delete char from both */
        return Math.min(Math.min(distance(s.substring(0,len_s-1), t) + 1,
                distance(s, t.substring(0, len_t - 1)) + 1),
                distance(s.substring(0, len_s - 1), t.substring(0, len_t - 1)) + cost);
    }
}
