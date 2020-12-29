package ec.encryption.utils;

public class TernaryHelper {
    private static final String NULL = "0";

    public static String pushNull(String str) {
        return str + NULL;
    }

    public static int[] getTernaryExpansion(int[] bits) {
        for (int i = 0; i < bits.length; ) {
            if (bits[i] == 0) {
                int trueBitsCounter = 0;
                if (i + 1 < bits.length) {
                    for (int j = i + 1; j < bits.length; j++) {
                        if (bits[j] == 1) {
                            if (trueBitsCounter == 1) {
                                bits[j - 1] = -1;
                            }
                            trueBitsCounter++;
                            if (trueBitsCounter >= 2) {
                                bits[j] = 0;
                            }
                        } else {
                            if (trueBitsCounter >= 2) {
                                bits[j] = 1;
                                if (j != bits.length - 1) { // exclude infinite loop when last bit = 1
                                    i = j - 1; // set 'i' so that current '1' can be contained in the chain of '1','1',...
                                } else {
                                    i = j;
                                }
                            } else {
                                i = j;
                            }
                            break;
                        }
                    }
                } else {
                    break;
                }
            } else {
                i++;
            }
        }
        return bits;
    }
}
