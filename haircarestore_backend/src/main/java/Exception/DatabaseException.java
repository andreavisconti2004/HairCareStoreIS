package Exception;

/**
 * Eccezione di dominio lanciata dallo strato Control in caso di problemi provenienti dalla comunicazione
 * con il Database.
 * Essendo un'estensione diretta della classe {@link Exception}, si configura come una checked exception: 
 * impone pertanto ai metodi chiamanti l'obbligo di gestione esplicita (tramite blocco try-catch) 
 * o di propagazione nella firma del metodo (clausola throws) verso lo strato Boundary, 
 * dove verrà mappata sul rispettivo codice di stato HTTP 500 (Internal Server Error).
 */
public class DatabaseException extends Exception{

    /**
     * Costruttore privo di un messaggio di errore esplicito.
     */
    public DatabaseException() {
    }

    /**
     * Costruttore con messaggio di errore esplicito.
     * @param message
     */
    public DatabaseException(String message) {
        super(message);
    }
    
}
