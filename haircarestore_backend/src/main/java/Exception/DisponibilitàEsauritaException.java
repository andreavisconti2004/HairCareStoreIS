package Exception;

/**
 * Eccezione di dominio lanciata dallo strato Control in caso di quantità di prodotto disponibile non
 * sufficiente a soddisfare la quantità richiesta dal cliente tramite la boundary.
 * Essendo un'estensione diretta della classe {@link Exception}, si configura come una checked exception: 
 * impone pertanto ai metodi chiamanti l'obbligo di gestione esplicita (tramite blocco try-catch) 
 * o di propagazione nella firma del metodo (clausola throws) verso lo strato Boundary, 
 * dove verrà mappata sul rispettivo codice di stato HTTP 409 (Conflict).
 */
public class DisponibilitàEsauritaException extends Exception{

    /**
     * Costruttore privo di un messaggio di errore esplicito.
     */
    public DisponibilitàEsauritaException() {
    }

    /**
     * Costruttore con messaggio di errore esplicito.
     * @param message
     */
    public DisponibilitàEsauritaException(String message) {
        super(message);
    }

    
}
