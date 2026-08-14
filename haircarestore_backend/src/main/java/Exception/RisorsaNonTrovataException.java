package Exception;

/**
 * Eccezione di dominio lanciata dallo strato Control in caso di fallimento del recupero dal Database 
 * di risorse tramite l'impiego del metodo di findById che restiuisce di default un Optional.
 * Essendo un'estensione diretta della classe {@link Exception}, si configura come una checked exception: 
 * impone pertanto ai metodi chiamanti l'obbligo di gestione esplicita (tramite blocco try-catch) 
 * o di propagazione nella firma del metodo (clausola throws) verso lo strato Boundary, 
 * dove verrà mappata sul rispettivo codice di stato HTTP 404 (Not Found).
 */
public class RisorsaNonTrovataException extends Exception{

    /**
     * Costruttore privo di un messaggio di errore esplicito.
     */
    public RisorsaNonTrovataException() {
    }

    /**
     * Costruttore con messaggio di errore esplicito.
     * @param message
     */
    public RisorsaNonTrovataException(String message) {
        super(message);
    }

    
}
