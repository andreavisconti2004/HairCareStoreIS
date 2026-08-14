package DTO;

/**
 * Data Transfer Object (DTO) preposto all'isolamento e alla validazione del payload JSON richiesto per 
 * la seconda fase del caso d'uso "Acquista Prodotto" (UC07), nello specifico nel caso in cui non venga confermato
 * l'emissione dell'ordine. Il DTO è uguale a quello di richiesta per l'emissione, ma con due significati ed usi
 * differenti. 
 * La scelta di non inserire vincoli sintattici all'interno della classe è legata al principio di tracciabilità 
 * dei requisiti e alla natura del dato trattato. Mentre i campi della prima fase dell'acquisto sono input diretti 
 * dell'utente, l'ID dell'ordine è un dato strutturato di sistema. Esso viene generato nativamente dal Database e
 * gestito in modalità invisibile dall'applicazione Angular, che lo rispedisce al backend per finalizzare il pagamento.
 */
public class RequestAnnullamentoDTO {

    /**
     * L'identificativo unico dell'ordine generato dal Database.
     */
    private int codiceOrdine;

    /**
     * Costruttore vuoto.
     * Richiesto dalla libreria Jackson per consentire l'allocazione dell'oggetto a runtime tramite i 
     * meccanismi di riflessione Java durante la deserializzazione del JSON.
     */
    public RequestAnnullamentoDTO() {
    }

    /**
     * Costruttore con argomenti.
     * @param codiceOrdine Il codice dell'ordine da cancellare dal Database
     */
    public RequestAnnullamentoDTO(int codiceOrdine) {
        this.codiceOrdine = codiceOrdine;
    }

    /** Getter della variabile di istanza codiceOrdine 
     * @return codiceOrdine
    */
    public int getCodiceOrdine() {return codiceOrdine;}
    /** Setter della variabile di istanza codiceOrdine 
     * @param codiceOrdine
    */
    public void setCodiceOrdine(int codiceOrdine) {this.codiceOrdine = codiceOrdine;}
}
