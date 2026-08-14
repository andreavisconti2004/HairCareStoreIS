package Exception;

/**
 * Eccezione di dominio lanciata dallo strato Control in caso di fallimento della procedura
 * di verifica delle credenziali utente.
 * Modella un evento eccezionale specifico legato al caso d'uso di inclusione "UC13: Autenticazione". 
 * Essendo un'estensione diretta della classe {@link Exception}, si configura come una checked exception: 
 * impone pertanto ai metodi chiamanti l'obbligo di gestione esplicita (tramite blocco try-catch) 
 * o di propagazione nella firma del metodo (clausola throws) verso lo strato Boundary, 
 * dove verrà mappata sul rispettivo codice di stato HTTP 401 (Unauthorized).
 */
public class AutenticazioneFallitaException extends Exception{

    /**
     * Costruttore privo di un messaggio di errore esplicito.
     */
    public AutenticazioneFallitaException() {
    }

    /**
     * Costruttore con messaggio di errore esplicito.
     * @param message
     */
    public AutenticazioneFallitaException(String message) {
        super(message);
    }
    
}
