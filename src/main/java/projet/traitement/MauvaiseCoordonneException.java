package projet.traitement;

/**
 * Exception pour des coordonnées non comprises dans l'image
 */
public class MauvaiseCoordonneException extends Exception {
    /**
     * Constructeur de la classe MauvaiseCoordonneException
     * @param message Message renvoié par l'exception
     */
    public MauvaiseCoordonneException(String message) {
        super(message);
    }
}