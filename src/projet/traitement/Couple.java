package src.projet.traitement;

import java.util.Objects;

/**
 * Classe formant des couples d'objet
 */
public class Couple<A, B> {
    private A pointA;
    private B pointB;

    /**
     * Constructeur de la classe Couple
     * @param pointA Premier objet
     * @param pointB Second objet
     */
    public Couple(A pointA, B pointB) {
        this.pointA = pointA;
        this.pointB = pointB;
    }

    /**
     * Getteur du premier objet
     * @return Le premier objet du couple
     */
    public A getA() {
        return pointA;
    }

    /**
     * Setteur du premier objet
     * @param pointA Premier objet
     */
    public void setA(A pointA) {
        this.pointA = pointA;
    }

    /**
     * Getteur du second objet
     * @return Le second objet du couple
     */
    public B getB() {
        return pointB;
    }

    /**
     * Setteur du second objet
     * @param pointB Second objet du couple
     */
    public void setB(B pointB) {
        this.pointB = pointB;
    }

    /**
     * Methode toString implémenté à la classe Couple
     * @return (A,B)
     */
    @Override
    public String toString() {
        return "(" + pointA +"," + pointB +')';
    }

    /**
     * Méthode equals implémenté à la classe Couple
     * @param o Object
     * @return True si les couples sont identique
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Couple<?, ?> couple = (Couple<?, ?>) o;
        return Objects.equals(pointA, couple.pointA) &&
                Objects.equals(pointB, couple.pointB);
    }
}
