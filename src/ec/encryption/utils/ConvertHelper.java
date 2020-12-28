package ec.encryption.utils;

import java.math.BigInteger;

public class ConvertHelper {
    private static final String NULL = "0";
    private static final String SPACE = " ";
    private static final String EMPTY_STRING = "";

    public static String intTo32BinStr(int val) {
        return String
                .format("%32s", Integer.toBinaryString(val))
                .replaceAll(SPACE, NULL);
    }

    public static String intToBinStr(int val) {
        return Integer.toBinaryString(val);
    }

    public static String reverseString(String str) {
        return new StringBuilder(str).reverse().toString();
    }

    public static BigInteger toBigInteger(int integer) {
        return BigInteger.valueOf(integer);
    }

    public static int[] strToIntegersArray(String src) {
        int[] res = new int[src.length()];
        String[] bits = src.split(EMPTY_STRING);
        for (int i = 0; i < src.length(); i++) {
            res[i] = Integer.parseInt(bits[i]);
        }
        return res;
    }
}
