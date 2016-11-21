package semestralka;

/**
 * Created by Martinek on 13.10.2016.
 *
 */
class Levenstein
{
    /**
     * Calculates Levenstein distance of two strings without recursion
     * @param s1 First string
     * @param s2 Second string
     * @return distance of strings
     */
    static int distance(final String s1, final String s2)
    {
        final int len1 = s1.length();
        final int len2 = s2.length();

        int[] col = new int[len2+1];
        int[] prevCol = new int[len2+1];

        for (int i = 0; i < prevCol.length; ++i) {
            prevCol[i] = i;
        }

        for (int i = 0; i < len1; ++i)
        {
            col[0] = i+1;
            for (int j = 0; j < len2; ++j) {
                col[j + 1] = Math.min(Math.min(1 + col[j], 1 + prevCol[1 + j]), prevCol[j] + (s1.charAt(i) == s2.charAt(j) ? 0 : 1));
            }

            int[] tmp = col;
            col = prevCol;
            prevCol = tmp;
        }

        return prevCol[len2];
    }
}
