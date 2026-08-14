package DTO;

/**
 * Data Transfer Object (DTO) destinato aalla serializzazione del payload di risposta inviato al client 
 * Angular al termine della prima fase del caso d'uso "Acquista Prodotto" (UC07).In questo modo è possibie 
 * ottenere una corrispondenza completa con il Model definito sul Front-End, evitando di dover effettuare 
 * cast e conversioni esplicite del payload. Infatti, il payload JSON in uscita, alternativamente a questa 
 * strategia, sarebbe stato mappato con un Conteiner. Ovvero avremmo ottenuto una struttura chiave:valore, 
 * dove le chiavi sarebbero state stringhe e i valori Object.
 * I campi di questa classe mappano in modo ordinato gli elementi restituiti dalla Control. Non sono necessarie 
 * annotazioni di validazione formale poiché l'oggetto viene originato interamente dal backend.
 */
public class ResponseAcquistoDTO {

    /**
     * Il costo totale calcolato per l'acquisto dei prodotti.
     * Mappa l'indice [0] dell'ArrayList restituito dalla Control nel metodo acquistaProdotto().
     */
    private String costoTotale;

    /**
     * La carta di credito del cliente registrato per procedere con l'acquisto.
     * Mappa l'indice [1] dell'ArrayList restituito dalla Control nel metodo acquistaProdotto().
     */
    private String cartaDiCredito;

    /**
     * Indirizzo di spedizione del cliente registrato.
     * Mappa l'indice [2] dell'ArrayList restituito dalla Control nel metodo acquistaProdotto().
     */
    private String indirizzo;

    /**
     * Il codice univoco che identifica l'ordine.
     * Mappa l'indice [3] dell'ArrayList restituito dalla Control nel metodo acquistaProdotto().
     */
    private int codiceOrdine;

    /**
     * Costruttore vuoto.
     * Richiesto dalla libreria Jackson per consentire l'allocazione dell'oggetto a runtime tramite i 
     * meccanismi di riflessione Java durante la deserializzazione del JSON.
     */
    public ResponseAcquistoDTO() {
    }

    /**
     * Costruttore con argomenti.
     * @param costoTotale         Costo dell'ordine
     * @param cartaDiCredito      Carta di credito del cliente
     * @param indirizzo           Il domicilio per la consegna dell'ordine
     * @param codiceOrdine        L'ID dell'ordine memorizzato sul database
     */
    public ResponseAcquistoDTO(String costoTotale, String cartaDiCredito, String indirizzo, int codiceOrdine) {
        this.costoTotale = costoTotale;
        this.cartaDiCredito = cartaDiCredito;
        this.indirizzo = indirizzo;
        this.codiceOrdine = codiceOrdine;
    }

    /** Getter della variabile di istanza costoTotale 
     * @return costoTotale
    */
    public String getCostoTotale() {return costoTotale;}
    /** Getter della variabile di istanza cartaDiCredito 
     * @return cartaDiCredito
    */
    public String getCartaDiCredito() {return cartaDiCredito;}
    /** Getter della variabile di istanza indirizzo 
     * @return indirizzo
    */
    public String getIndirizzo() {return indirizzo;}
    /** Getter della variabile di istanza codiceOrdine 
     * @return codiceOrdine
    */
    public int getCodiceOrdine() {return codiceOrdine;}

    /** Setter della variabile di istanza costoTotale 
     * @param costoTotale
    */
    public void setCostoTotale(String costoTotale) {this.costoTotale = costoTotale;}
    /** Setter della variabile di istanza cartaDiCredito 
     * @param cartaDiCredito
    */
    public void setCartaDiCredito(String cartaDiCredito) {this.cartaDiCredito = cartaDiCredito;}
    /** Setter della variabile di istanza indirizzo 
     * @param indirizzo
    */
    public void setIndirizzo(String indirizzo) {this.indirizzo = indirizzo;}
    /** Setter della variabile di istanza codiceOrdine 
     * @param codiceOrdine
    */
    public void setCodiceOrdine(int codiceOrdine) {this.codiceOrdine = codiceOrdine;}

    
}
