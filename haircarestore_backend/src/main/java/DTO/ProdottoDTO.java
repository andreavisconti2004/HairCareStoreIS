package DTO;

/**
 * Data Transfer Object (DTO) deputato all'incapsulamento e alla serializzazione dei dati del singolo
 * prodotto destinati alla visualizzazione nel catalogo del Front-End. In questo modo le entity
 * legate tramite JPA al Database sono disaccoppiate al DTO visualizzato e trasferito in rete.
 */
public class ProdottoDTO {

    /** Codice identificativo univoco del prodotto. */
    private String codice;
    /** Denominazione commerciale del prodotto per la cura dei capelli. */
    private String nome;
    /** Descrizione estesa delle specifiche tecniche del prodotto. */
    private String descrizione;
    /** Categoria di appartenenza del prodotto. */
    private String categoria;
    /** Prezzo unitario di listino per la vendita del prodotto. */
    private float prezzo;
    /** Numero di unità del prodotto attualmente disponibili nel magazzino. */
    private int quantita;
    
    /**
     * Costruttore vuoto.
     * Richiesto dalla libreria Jackson per consentire l'allocazione dell'oggetto a runtime tramite i 
     * meccanismi di riflessione Java durante la deserializzazione del JSON.
     */
    public ProdottoDTO() {
    }

    /**
     * Costruttore con argomenti.
     * @param codice    
     * @param nome      
     * @param descrizione 
     * @param categoria
     * @param prezzo
     * @param quantità
     */
    public ProdottoDTO(String codice, String nome, String descrizione, String categoria, float prezzo, int quantità) {
        this.codice = codice;
        this.nome = nome;
        this.descrizione = descrizione;
        this.categoria = categoria;
        this.prezzo = prezzo;
        this.quantita = quantità;
    }

    /** Getter della variabile di istanza codice 
     * @return codice
    */
    public String getCodice() {return codice;}
    /** Getter della variabile di istanza nome 
     * @return nome
    */
    public String getNome() {return nome;}
    /** Getter della variabile di istanza descrizione 
     * @return descrizione
    */
    public String getDescrizione() {return descrizione;}
    /** Getter della variabile di istanza categoria 
     * @return categoria
    */
    public String getCategoria() {return categoria;}
    /** Getter della variabile di istanza prezzo 
     * @return prezzo
    */
    public float getPrezzo() {return prezzo;}
    /** Getter della variabile di istanza quantità 
     * @return quantita
    */
    public int getQuantita() {return quantita;}

    /** Setter della variabile di istanza codice 
     * @param codice
    */
    public void setCodice(String codice) {this.codice = codice;}
    /** Setter nome variabile di istanza nome 
     * @param nome
    */
    public void setNome(String nome) {this.nome = nome;}
    /** Setter della variabile di istanza descrizione 
     * @param descrizione
    */
    public void setDescrizione(String descrizione) {this.descrizione = descrizione;}
    /** Setter della variabile di istanza categoria 
     * @param categoria
    */
    public void setCategoria(String categoria) {this.categoria = categoria;}
    /** Setter della variabile di istanza prezzo 
     * @param prezzo
    */
    public void setPrezzo(float prezzo) {this.prezzo = prezzo;}
    /** Setter della variabile di istanza quantità 
     * @param quantità
    */
    public void setQuantita(int quantità) {this.quantita = quantità;}

    
}
