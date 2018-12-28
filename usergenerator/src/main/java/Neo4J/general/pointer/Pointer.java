/*
 * Pointer.java
 * This file is part of databaseMappings
 *
 * Copyright (C) 2016 - Giacomo Bergami
 *
 * databaseMappings is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * databaseMappings is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with databaseMappings. If not, see <http://www.gnu.org/licenses/>.
 */



package Neo4J.general.pointer;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.Apint;

/**
 * Defines a pointer as a comparable object. This interface implements the comparison operations that allow to
 * compare longs, Apints and Pairs altogether.
 */
public interface Pointer extends Comparable<Pointer> {

    PointerGeneral type();

    default int compareTo(Pointer o) {
        switch (type()) {
            case LongPointer: {
                long l = ((GraphIdPointerLong)this).pointer;
                switch (o.type()) {
                    case LongPointer:
                        return Long.compare(l,((GraphIdPointerLong)o).pointer);
                    case ApintPointer:
                        return new Apint(l).compareTo(((GraphIdPointerApint)o).apint);
                    case PairPointer:
                        return compareTo(((GraphBulkBinPointer)o).degradate());
                    default:
                        throw new UnsupportedClassVersionError("The pointer type of the right operand has not been recognized");
                }
            }
            case ApintPointer: {
                Apint l = ((GraphIdPointerApint)this).apint;
                switch (o.type()) {
                    case LongPointer:
                        return l.compareTo(new Apint(((GraphIdPointerLong)o).pointer));
                    case ApintPointer:
                        return l.compareTo(((GraphIdPointerApint)o).apint);
                    case PairPointer:
                        return compareTo(((GraphBulkBinPointer)o).degradate());
                    default:
                        throw new UnsupportedClassVersionError("The pointer type of the right operand has not been recognized");
                }
            }
            case PairPointer:
                switch (o.type()) {
                    case PairPointer: {
                        int leftCmp = ((GraphBulkBinPointer)this).left.compareTo(((GraphBulkBinPointer)o).left);
                        int rightCmp = ((GraphBulkBinPointer)this).right.compareTo(((GraphBulkBinPointer)o).right);
                        return leftCmp != 0 ? leftCmp : rightCmp;
                    }
                    default:
                    return (((GraphBulkBinPointer)this).degradate()).compareTo(o);
                }

            default:
                throw new UnsupportedClassVersionError("The pointer type of the left operand has not been recognized");
        }
    }

    /**
     * Pointer arithmetics: multiplies two pointers
     * @param left
     * @param right
     * @return
     */
    default Pointer multiply(Pointer left, Pointer right) {
        switch (left.type()) {
            case LongPointer: {
                long l = ((GraphIdPointerLong)left).pointer;
                switch (right.type()) {
                    case LongPointer:
                    {
                        long r = ((GraphIdPointerLong)right).pointer;
                        try {
                            return new GraphIdPointerLong(Math.multiplyExact(l,r));
                        } catch (ArithmeticException e) {
                            return new GraphIdPointerApint(new Apint(l).multiply(new Apint(r)));
                        }
                    }
                    case ApintPointer:
                        return new GraphIdPointerApint(((GraphIdPointerApint)right).apint.multiply(new Apint(l)));
                    case PairPointer:
                        return multiply(((GraphBulkBinPointer)left).degradate(),right);
                    default:
                        throw new UnsupportedClassVersionError("The pointer type of the right operand has not been recognized");
                }
            }
            case ApintPointer: {
                Apint l = ((GraphIdPointerApint)left).apint;
                switch (right.type()) {
                    case LongPointer:
                    {
                        return new GraphIdPointerApint(l.multiply((((GraphIdPointerApint)right).apint)));
                    }
                    case ApintPointer:
                        return new GraphIdPointerApint(((GraphIdPointerApint)right).apint.multiply(l));
                    case PairPointer:
                        return multiply(((GraphBulkBinPointer)left).degradate(),right);
                    default:
                        throw new UnsupportedClassVersionError("The pointer type of the right operand has not been recognized");
                }
            }
            case PairPointer:
                return multiply(left,((GraphBulkBinPointer)right).degradate());
            default:
                throw new UnsupportedClassVersionError("The pointer type of the left operand has not been recognized");
        }
    }

    Apint TWO = Apint.ONE.add(Apint.ONE);
    Apfloat TWOF = Apfloat.ONE.add(Apfloat.ONE);
    Apint EIGHT = new Apint(8);

    /**
     * Pointer arithmetics: divides the number by two
     * @param d
     * @return
     */
    default Pointer div2(Pointer d) {
        switch (d.type()) {
            case LongPointer:
                return new GraphIdPointerLong(((GraphIdPointerLong)d).pointer / 2);
            case ApintPointer:
                return new GraphIdPointerApint(((GraphIdPointerApint)d).apint.divide(TWO));
            case PairPointer:
                return div2(((GraphBulkBinPointer)d).degradate());
            default:
                throw new UnsupportedClassVersionError("The pointer type has not been recognized");
        }
    }

    /**
     * Pointer arithmetics: Adds two pointers together
     * @param left
     * @param right
     * @return
     */
    default Pointer add(Pointer left, Pointer right) {
        switch (left.type()) {
            case LongPointer: {
                long l = ((GraphIdPointerLong)left).pointer;
                switch (right.type()) {
                    case LongPointer:
                    {
                        long r = ((GraphIdPointerLong)right).pointer;
                        try {
                            return new GraphIdPointerLong(Math.addExact(l,r));
                        } catch (ArithmeticException e) {
                            return new GraphIdPointerApint(new Apint(l).add(new Apint(r)));
                        }
                    }
                    case ApintPointer:
                        return new GraphIdPointerApint(((GraphIdPointerApint)right).apint.add(new Apint(l)));
                    case PairPointer:
                        return add(((GraphBulkBinPointer)left).degradate(),right);
                    default:
                        throw new UnsupportedClassVersionError("The pointer type of the right operand has not been recognized");
                }
            }
            case ApintPointer: {
                Apint l = ((GraphIdPointerApint)left).apint;
                switch (right.type()) {
                    case LongPointer:
                    {
                        return new GraphIdPointerApint(l.add((((GraphIdPointerApint)right).apint)));
                    }
                    case ApintPointer:
                        return new GraphIdPointerApint(((GraphIdPointerApint)right).apint.add(l));
                    case PairPointer:
                        return add(((GraphBulkBinPointer)left).degradate(),right);
                    default:
                        throw new UnsupportedClassVersionError("The pointer type of the right operand has not been recognized");
                }
            }
            case PairPointer:
                return add(left,((GraphBulkBinPointer)right).degradate());
            default:
                throw new UnsupportedClassVersionError("The pointer type of the left operand has not been recognized");
        }
    }

    /**
     * Uses the inverse function of the dovetailing one
     * @param d
     * @return
     */
    default GraphBulkBinPointer splitPointer(Pointer d) {
        switch (d.type()) {
            case LongPointer: {
                long c = ((GraphIdPointerLong)d).pointer;
                try {
                    long dmax = Math.addExact(1,Math.multiplyExact(8,c));
                    dmax = Double.valueOf(Math.floor((-1.0+Math.sqrt(dmax))/2.0)).longValue();
                    long left = Math.addExact(dmax,1);
                    left = dmax - (Math.multiplyExact(left,dmax)/2);
                    long right = c - left;
                    return new GraphBulkBinPointer(left,right);
                } catch (ArithmeticException e) {
                    return splitPointer(new GraphIdPointerApint(new Apint(c)));
                }
            }
            case ApintPointer: {
                Apint c = ((GraphIdPointerApint)d).apint;
                Apint dmax = c.multiply(EIGHT).add(Apint.ONE);
                dmax = ApfloatMath.sqrt(dmax).subtract(Apfloat.ONE).divide(TWOF).floor();
                Apint left = dmax.add(Apint.ONE);
                left = dmax.subtract(left.multiply(dmax).divide(TWO));
                Apint right = c.subtract(left);
                return new GraphBulkBinPointer(left,right);
            }
            case PairPointer:
                return ((GraphBulkBinPointer)d); // it is already splitted
            default:
                throw new UnsupportedClassVersionError("The pointer type of the left operand has not been recognized");
        }
    }

}
