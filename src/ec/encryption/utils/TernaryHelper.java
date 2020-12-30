package ec.encryption.utils;

public class TernaryHelper {
    private static final String NULL_STR = "0";

    private static final String STATE_ONE = "state1";
    private static final String STATE_TWO = "state2";
    private static final String STATE_THREE = "state3";
    private static final String STATE_FOUR = "state4";
    private static final String STATE_FIVE = "state5";
    private static final String STATE_SIX = "state6";
    private static final String STATE_SEVEN = "state7";

    public static String pushNull(String str) {
        return str + NULL_STR;
    }

    private static void nextState(String state, int pos, int[] bits) {
        switch (state) {
            case STATE_ONE:
                if (pos == bits.length - 1) return;
                pos++;
                nextState(STATE_TWO, pos, bits);
                break;
            case STATE_TWO:
                if (bits[pos] == 1) {
                    if (pos == bits.length - 1) return;
                    pos++;
                    nextState(STATE_THREE, pos, bits);
                } else {
                    if (pos == bits.length - 1) return;
                    pos++;
                    nextState(STATE_TWO, pos, bits);
                }
                break;
            case STATE_THREE:
                if (bits[pos] == 1) {
                    if (pos == bits.length - 1) return;
                    pos++;
                    nextState(STATE_FOUR, pos, bits);
                } else {
                    if (pos == bits.length - 1) return;
                    pos++;
                    nextState(STATE_TWO, pos, bits);
                }
                break;
            case STATE_FOUR:
                if (bits[pos] == 1) {
                    if (pos == bits.length - 1) return;
                    pos++;
                    nextState(STATE_FOUR, pos, bits);
                } else {
                    bits[pos] = 1;
                    pos--;
                    nextState(STATE_FIVE, pos, bits);
                }
                break;
            case STATE_FIVE:
                if (bits[pos] == 1) {
                    bits[pos] = 0;
                    if (pos == 0) {
                        nextState(STATE_SIX, pos, bits);
                    } else {
                        pos--;
                        nextState(STATE_FIVE, pos, bits);
                    }
                } else {
                    if (pos == bits.length - 1) return;
                    pos++;
                    nextState(STATE_SIX, pos, bits);
                }
                break;
            case STATE_SIX:
                bits[pos] = -1;
                nextState(STATE_SEVEN, pos, bits);
                break;
            case STATE_SEVEN:
                if (bits[pos] == 1) {
                    nextState(STATE_TWO, pos, bits);
                } else {
                    if (pos == bits.length - 1) return;
                    pos++;
                    nextState(STATE_SEVEN, pos, bits);
                }
                break;
            default:
                break;
        }
    }

    public static int[] getTernaryExpansion(int[] bits) {
        nextState(STATE_ONE, 0, bits);
        return bits;
    }
}
