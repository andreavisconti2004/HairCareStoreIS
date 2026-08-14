package Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entità JPA per la tabella FATTORINI.
 * JPA (Jakarta Persistence API) e' lo standard Java per la persistenza
 * relazionale. Hibernate ne e' l'implementazione piu' diffusa e quella
 * usata da Spring Boot per default.
 * Utilizziamo le annotazioni:
 * {@code @Entity}: specifica ad Hibernate di essere una classe persistente.
 * {@code @Table}: mappa la classe sulla tabella del Database.
*/
@Entity
@Table(name="FATTORINI")
public class EntityFattorino {

    /**
     * Codice intero identificativo univoco del fattorino, generato dal Database.
     * Funge da Chiave Primaria della tabella (CONSTRAINT PK_FATTORINI). 
     * Mappato come intero (INT).
     * Utilizzate le annotazioni:
     * {@code @Id}: per identificare la chiave primaria dell'entità.
     * {@code Column}: mappa il campo Java alla colonna SQL.
     * {@code @GeneratedValue(strategy = IDENTITY)}: La strategia IDENTITY indica a Hibernate che il valore viene generato 
     * automaticamente dal motore MySQL tramite la clausola AUTO_INCREMENT.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    /**
     * Nome del fattorino.
     * Mappato come VARCHAR(20).
     * Vincolo DDL: Campo obbligatorio (NOT NULL).
     * Utilizzata l'annotazione:
     * {@code Column}: mappa il campo Java alla colonna SQL.
     */
    @Column(name="nome")
    private String nome;

    /**
     * Cognome del fattorino.
     * Mappato come VARCHAR(20).
     * Vincolo DDL: Campo obbligatorio (NOT NULL).
     * Utilizzata l'annotazione:
     * {@code Column}: mappa il campo Java alla colonna SQL.
     */
    @Column(name="cognome")
    private String cognome;

    /**
     * Numero di telefono del fattorino.
     * Mappato come VARCHAR(12).
     * Vincolo DDL: Campo obbligatorio (NOT NULL).
     * Utilizzata l'annotazione:
     * {@code Column}: mappa il campo Java alla colonna SQL.
     */
    @Column(name="telefono")
    private String telefono;

    /**
     * Email del fattorino.
     * Mappato come VARCHAR(50).
     * Vincolo DDL: Campo obbligatorio (NOT NULL).
     * Utilizzata l'annotazione:
     * {@code Column}: mappa il campo Java alla colonna SQL.
     */
    @Column(name="email")
    private String email;

    /**
     * Costruttore senza argomenti.
     * Richiesto obbligatoriamente dalle specifiche JPA per permettere all'ORM di istanziare
     * l'entita a runtime tramite i meccanismi di reflection.
     */
    public EntityFattorino(){}

    /**
     * Costruttore con argomenti.
     * L'id viene omesso perché se ne occuperà il Database tramite AUTO_INCREMENT.
     * @param nome      Il nome
     * @param cognome   Il cognome
     * @param telefono  Il numero di telefono
     * @param email     L'indirizzo email
     */
    public EntityFattorino(String nome, String cognome, String telefono, String email) {
        this.nome = nome;
        this.cognome = cognome;
        this.telefono = telefono;
        this.email = email;
    }

    /** Getter della variabile di istanza id 
     * @return id
    */
    public int getId() {return id;}
    /** Getter della variabile di istanza nome 
     * @return nome
    */
    public String getNome() {return nome;}
    /** Getter della variabile di istanza cognome 
     * @return cognome
    */
    public String getCognome() {return cognome;}
    /** Getter della variabile di istanza telefono
     * @return telefono
     */
    public String getTelefono() {return telefono;}
    /** Getter della variabile di istanza email 
     * @return email
    */
    public String getEmail() {return email;}

    /** Setter della variabile di istanza nome
     * @param nome
     */
    public void setNome(String nome) {this.nome = nome;}
    /** Setter della variabile di istanza cognome 
     * @param cognome
    */
    public void setCognome(String cognome) {this.cognome = cognome;}
    /** Setter della variabile di istanza telefono 
     * @param telefono
    */
    public void setTelefono(String telefono) {this.telefono = telefono;}
    /** Setter della variabile di istanza email 
     * @param email
    */
    public void setEmail(String email) {this.email = email;}
}
