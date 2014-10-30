
package me.arnaud.procterrain;

/**
 *
 * @author Arnaud
 */
public class BigByte {
    
    public static int MAX_INT = toInt((byte)0xff);
    public static int MAX_POSITIVE_INT = toInt((byte)0x7f);
    
    public static byte toByte (int in) {
        int ei = in >>> 5;
        int exp = 0;
        while (ei > 0) {
            ei >>>= 1;
            exp++;
        }
        int shift = exp > 0 ? exp-1 : 0;
        int mant = (in >> shift) & 0x1f;
        int res = mant | exp << 5;
        return (byte)res;
    }
    
    public static int toInt (byte in) {
        int i = in & 0xff;
        int exp = (i & 0xff) >> 5;
        int mant = (i & 0x1f);
        mant |= exp > 0 ? 0x20 : 0;
        exp--;
        exp = exp < 0 ? 0 : exp;
        int res = mant << exp;
        return res;
    }
}
