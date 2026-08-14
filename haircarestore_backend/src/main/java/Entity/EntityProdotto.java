package Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entità JPA per la tabella PRODOTTI.
 * JPA (Jakarta Persistence API) e' lo standard Java per la persistenza
 * relazionale. Hibernate ne e' l'implementazione piu' diffusa e quella
 * usata da Spring Boot per default.
 * Utilizziamo le annotazioni:
 * {@code @Entity}: specifica ad Hibernate di essere una classe persistente.
 * {@code @Table}: mappa la classe sulla tabella del Database.
*/
@Entity
@Table(name="PRODOTTI")
public class EntityProdotto {
    
    /**
     * Codice alfanumerico identificativo univoco del prodotto.
     * Funge da Chiave Primaria della tabella (CONSTRAINT PK_PRODOTTI). 
     * Mappato come stringa a lunghezza fissa CHAR(15).
     * Sottostà al vincolo DDL di validazione formale tramite espressione regolare (CHK_CODICE).
     * Utilizzate le annotazioni:
     * {@code @Id}: per identificare la chiave primaria dell'entità.
     * {@code Column}: mappa il campo Java alla colonna SQL.
     */
    @Id
    @Column(name="codice")
    private String codice;

    /**
     * Denominazione commerciale del prodotto per la cura dei capelli.
     * Mappato come VARCHAR(50).
     * Vincolo DDL: Campo obbligatorio (NOT NULL).
     * Utilizzata l'annotazione:
     * {@code Column}: mappa il campo Java alla colonna SQL.
     */
    @Column(name="nome")
    private String nome;

    /**
     * Descrizione estesa delle specifiche tecniche del prodotto.
     * Mappato come VARCHAR(100).
     * Utilizzata l'annotazione:
     * {@code Column}: mappa il campo Java alla colonna SQL.
     */
    @Column(name="descrizione")
    private String descrizione;

    /**
     * Categoria di appartenenza del prodotto.
     * Mappato come VARCHAR(15).
     * Utilizzata l'annotazione:
     * {@code Column}: mappa il campo Java alla colonna SQL.
     */
    @Column(name="categoria")
    private String categoria;

    /**
     * Prezzo unitario di listino per la vendita del prodotto.
     * Mappato come valore numerico DECIMAL(6,2).
     * Vincolo DDL: Campo obbligatorio (NOT NULL) vincolato a valori non negativi (CHK_PREZZO).
     * Utilizzata l'annotazione:
     * {@code Column}: mappa il campo Java alla colonna SQL.
     */
    @Column(name="prezzo")
    private float prezzo;

    /**
     * Numero di unita del prodotto attualmente disponibili nel magazzino.
     * Mappato come intero (INT).
     * Vincolo DDL: Campo obbligatorio (NOT NULL) vincolato a valori superiori o uguali a zero (CHK_QUANTITA).
     * Utilizzata l'annotazione:
     * {@code Column}: mappa il campo Java alla colonna SQL.
     */
    @Column(name="quantita")
    private int quantità;

    /**
     * Costruttore senza argomenti.
     * Richiesto obbligatoriamente dalle specifiche JPA per permettere all'ORM di istanziare
     * l'entita a runtime tramite i meccanismi di reflection.
     */
    public EntityProdotto(){}

    /**
     * Costruttore con argomenti.
     * @param codice      Il codice alfanumerico univoco a lunghezza fissa
     * @param nome        La denominazione del prodotto
     * @param descrizione La descrizione del prodotto
     * @param categoria   La categoria di classificazione
     * @param prezzo      Il prezzo di vendita
     * @param quantita    Le unita fisiche disponibili in magazzino
     */
    public EntityProdotto(String codice, String nome, String descrizione, String categoria, float prezzo, int quantita){
        this.codice=codice;
        this.nome=nome;
        this.descrizione=descrizione;
        this.categoria=categoria;
        this.prezzo=prezzo;
        this.quantità=quantita;
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
     * @return quantità
    */
    public int getQuantità() {return quantità;}

    /** Setter della variabile di istanza codice 
     * @param codice
    */
    public void setCodice(String codice) {this.codice = codice;}
    /** Setter della variabile di istanza nome 
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
    public void setQuantità(int quantità) {this.quantità = quantità;}
}
