package src.projet;

import java.util.Objects;

public class Couple<A, B> {
    private A pointA;
    private B pointB;

    public Couple(A pointA, B pointB) {
        this.pointA = pointA;
        this.pointB = pointB;
    }

    public A getA() {
        return pointA;
    }

    public void setA(A pointA) {
        this.pointA = pointA;
    }

    public B getB() {
        return pointB;
    }

    public void setB(B pointB) {
        this.pointB = pointB;
    }

    @Override
    public String toString() {
        return "Duo{" +
                "pointA=" + pointA +
                ", pointB=" + pointB +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Couple<?, ?> duo = (Couple<?, ?>) o;
        return Objects.equals(pointA, duo.pointA) &&
                Objects.equals(pointB, duo.pointB);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pointA, pointB);
    }
}