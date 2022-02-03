package org.firstinspires.ftc.teamcode.main.utils.geometry;

public class Length extends Number {
    public double value;
    public LengthUnit unit;

    public Length(double value, LengthUnit unit) {
        this.value = value;
        this.unit = unit;
    }

    public Number as(LengthUnit unit) {
        double temp = 0;

        switch (this.unit) {
            case MM:
                temp = value/10;
                break;
            case CM:
                temp = value;
                break;
            case M:
                temp = value * 100;
                break;
            case IN:
                temp = value * 2.54;
                break;
            case FT:
                temp = (value*12)*2.54;
                break;
            case TILE:
                temp = (value*24)*2.54;
                break;
        }

        switch (unit) {
            case MM:
                return 10 * temp;
            case CM:
                return temp;
            case M:
                return temp/100;
            case IN:
                return temp/2.54;
            case FT:
                return (temp/2.54)/12;
            case TILE:
                return (temp/2.54)/24;
        }

        return null;
    }

    public void convert(LengthUnit unit) {
        this.value = (double) as(unit);
        this.unit = unit;
    }

    @Override
    public int intValue() {
        return (int) value;
    }

    @Override
    public long longValue() {
        return (long) value;
    }

    @Override
    public float floatValue() {
        return (float) value;
    }

    @Override
    public double doubleValue() {
        return value;
    }

    public enum LengthUnit {
        MM, CM, M, FT, IN, TILE;
    }
}
