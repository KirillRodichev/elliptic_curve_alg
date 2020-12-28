package ec.encryption.model;

import java.math.BigInteger;

import static ec.encryption.utils.ConvertHelper.*;
import static ec.encryption.utils.TernaryHelper.getTernaryExpansion;
import static ec.encryption.utils.TernaryHelper.pushNull;

public class Point {
    private BigInteger m;
    private static BigInteger a;

    private BigInteger x;
    private BigInteger y;
    private int z;

    public Point getNull() {
        return new Point(toBigInteger(0), toBigInteger(1), 0, this.m);
    }

    public static void initCoefficientA(BigInteger a) {
        Point.a = a;
    }

    public Point(BigInteger x, BigInteger y, BigInteger m) {
        this(x, y, 1, m);
    }

    public Point(BigInteger x, BigInteger y, int z, BigInteger m) {
        this.m = m;
        this.x = x.mod(m);
        this.y = y.mod(m);
        this.z = z;
    }

    public Point negative() {
        return new Point(this.x, this.y.negate(), this.z, this.m);
    }

    public Point doublePoint() {
        return this.add(this);
    }

    public Point add(Point p) {
        BigInteger slope;
        BigInteger x3;
        BigInteger y3;
        if (this.z == 0) return p;
        if (p.z == 0) return this;
        if (this.x.equals(p.x)) {
            if (this.y.equals(p.y.negate())) {
                return this.getNull();
            }
            slope = this.x
                    .pow(2).mod(this.m)
                    .multiply(toBigInteger(3)).mod(this.m)
                    .add(a).mod(this.m)
                    .multiply(this.y.multiply(toBigInteger(2)).pow(-1)).mod(this.m);
        } else {
            slope = p.y
                    .add(this.y.negate()).mod(this.m)
                    .multiply(p.x.add(this.x.negate())).pow(-1).mod(this.m);
        }
        x3 = slope
                .pow(2).mod(this.m)
                .add(this.x.negate()).mod(this.m)
                .add(p.x.negate()).mod(this.m);
        y3 = slope
                .multiply(this.x.add(x3.negate()).mod(this.m)).mod(this.m)
                .add(this.y.negate()).mod(this.m);
        return new Point(x3, y3, 1, this.m);
    }

    public Point mul(int multiplier) {
        Point q = new Point(this.x, this.y, this.z, this.m);
        Point r = this.getNull();
        while (multiplier > 0) {
            if (toBigInteger(multiplier).mod(toBigInteger(2)).equals(toBigInteger(1))) {
                r.add(q);
            }
            q = q.doublePoint();
            multiplier /= 2;
        }
        return r;
    }

    public Point ternaryMul(int multiplier) {
        Point q = new Point(this.x, this.y, this.z, this.m);
        Point r = this.getNull();
        String sBinMultiplier = pushNull(reverseString(intToBinStr(multiplier)));
        int[] iBinMultiplier = getTernaryExpansion(strToIntegersArray(sBinMultiplier));
        for (int value : iBinMultiplier) {
            if (value == 1) {
                r.add(q);
            } else if (value == -1) {
                r.add(q.negative());
            }
            q = q.doublePoint();
        }
        return r;
    }

    public boolean equals(Point p) {
        return this.x.equals(p.getX()) && this.y.equals(p.getY()) && (this.z == p.getZ());
    }

    public Point sub(Point p) {
        return add(p.negative());
    }

    public BigInteger getX() {
        return x;
    }

    public void setX(BigInteger x) {
        this.x = x;
    }

    public BigInteger getY() {
        return y;
    }

    public void setY(BigInteger y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public BigInteger getA() {
        return a;
    }

    public BigInteger getModulus() {
        return this.m;
    }
}