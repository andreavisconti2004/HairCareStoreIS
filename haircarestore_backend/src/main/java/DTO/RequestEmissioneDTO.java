package DTO;

/**
 * Data Transfer Object (DTO) preposto all'isolamento e alla validazione del payload JSON richiesto per 
 * la seconda fase del caso d'uso "Acquista Prodotto" (UC07). In questo modo è possibie ottenere una
 * corrispondenza completa con il Model definito sul Front-End, evitando di dover effettuare cast e
 * conversioni esplicite del payload. Infatti, il payload JSON in ingresso, alternativamente a questa 
 * strategia, sarebbe stato mappato con un Conteiner Map. Ovvero avremmo ottenuto ad una struttura chiave:valore,
 * dove le chiavi sarebbero state stringhe e i valori Object.
 * La scelta di non inserire vincoli sintattici all'interno della classe è legata al principio di tracciabilità 
 * dei requisiti e alla natura del dato trattato. Mentre i campi della prima fase dell'acquisto sono input diretti 
 * dell'utente, l'ID dell'ordine è un dato strutturato di sistema. Esso viene generato nativamente dal Database e
 * gestito in modalità invisibile dall'applicazione Angular, che lo rispedisce al backend per finalizzare il pagamento.
 */
public class RequestEmissioneDTO {

    /**
     * L'identificativo unico dell'ordine generato dal Database.
     */
    private int codiceOrdine;

    /**
     * Costruttore vuoto.
     * Richiesto dalla libreria Jackson per consentire l'allocazione dell'oggetto a runtime tramite i 
     * meccanismi di riflessione Java durante la deserializzazione del JSON.
     */
    public RequestEmissioneDTO() {
    }

    /**
     * Costruttore con argomenti.
     * @param codiceOrdine Il codice dell'ordine di cui completare l'acquisto
     */
    public RequestEmissioneDTO(int codiceOrdine) {
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
