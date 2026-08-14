package Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entità JPA per la tabella CLIENTI_REGISTRATI.
 * JPA (Jakarta Persistence API) e' lo standard Java per la persistenza
 * relazionale. Hibernate ne e' l'implementazione piu' diffusa e quella
 * usata da Spring Boot per default.
 * Utilizziamo le annotazioni:
 * {@code @Entity}: specifica ad Hibernate di essere una classe persistente.
 * {@code @Table}: mappa la classe sulla tabella del Database.
*/
@Entity
@Table(name="CLIENTI_REGISTRATI")
public class EntityClienteRegistrato {

    /**
     * Il nome utente univoco scelto dal cliente in fase di registrazione.
     * Funge da Chiave Primaria della tabella (CONSTRAINT PK_CLIENTI_REGISTRATI). 
     * Mappato come VARCHAR(20).
     * Sottostà al vincolo DDL di validazione formale tramite espressione regolare (CHK_NOME_UTENTE).
     * Utilizzate le annotazioni:
     * {@code @Id}: per identificare la chiave primaria dell'entità.
     * {@code Column}: mappa il campo Java alla colonna SQL.
     */
    @Id
    @Column(name="nome_utente")
    private String nomeUtente;

    /**
     * La stringa di autenticazione associata all'account del cliente registrato.
     * Mappato come VARCHAR(20).
     * Vincolo DDL: Campo obbligatorio (NOT NULL).
     * Sottostà al vincolo CHK_PASSWORD che ne impone una lunghezza compresa tra 8 e 20 caratteri 
     * e la presenza di almeno un carattere speciale.
     * Utilizzata l'annotazione:
     * {@code Column}: mappa il campo Java alla colonna SQL.
     */
    @Column(name="password")
    private String password;

    /**
     * L'indirizzo fisico di spedizione del cliente registrato.
     * Mappato come VARCHAR(50).
     * Vincolo DDL: Campo obbligatorio (NOT NULL).
     * Utilizzata l'annotazione:
     * {@code Column}: mappa il campo Java alla colonna SQL.
     */
    @Column(name="indirizzo")
    private String indirizzo;

    /**
     * Numero di telefono del cliente registrato.
     * Mappato come VARCHAR(12).
     * Vincolo DDL: Campo obbligatorio (NOT NULL).
     * Utilizzata l'annotazione:
     * {@code Column}: mappa il campo Java alla colonna SQL.
     */
    @Column(name="telefono")
    private String telefono;

    /**
     * Carta di credito del cliente registrato.
     * Mappato come VARCHAR(16).
     * Vincolo DDL: Campo obbligatorio (NOT NULL).
     * Utilizzata l'annotazione:
     * {@code Column}: mappa il campo Java alla colonna SQL.
     */
    @Column(name="carta_di_credito")
    private String cartaDiCredito;

    /**
     * Costruttore senza argomenti.
     * Richiesto obbligatoriamente dalle specifiche JPA per permettere all'ORM di istanziare
     * l'entita a runtime tramite i meccanismi di reflection.
     */
    public EntityClienteRegistrato(){}

    /**
     * Costruttore con argomenti.
     * @param nomeUtente     Username univoco del cliente
     * @param password       La password di sicurezza
     * @param indirizzo      L'indirizzo di spedizione dell'ordine
     * @param telefono       Il recapito telefonico
     * @param cartaDiCredito Il numero della carta di pagamento
     */
    public EntityClienteRegistrato(String nomeUtente, String password, String indirizzo, String telefono,
            String cartaDiCredito) {
        this.nomeUtente = nomeUtente;
        this.password = password;
        this.indirizzo = indirizzo;
        this.telefono = telefono;
        this.cartaDiCredito = cartaDiCredito;
    }

    /** Getter della variabile di istanza nomeUtente 
     * @return nomeUtente
    */
    public String getNomeUtente() {return nomeUtente;}
    /** Getter della variabile di istanza password 
     * @return password
    */
    public String getPassword() {return password;}
    /** Getter della variabile di istanza indirizzo
     * @return indirizzo
     */
    public String getIndirizzo() {return indirizzo;}
    /** Getter della variabile di istanza telefono
     * @return telefono
     */
    public String getTelefono() {return telefono;}
    /** Getter della variabile di istanza cartaDiCredito 
     * @return cartaDiCredito
    */
    public String getCartaDiCredito() {return cartaDiCredito;}

    /** Setter della variabile di istanza nomeUtente
     * @param nomeUtente
     */
    public void setNomeUtente(String nomeUtente) {this.nomeUtente = nomeUtente;}
    /** Setter della variabile di istanza password 
     * @param password
    */
    public void setPassword(String password) {this.password = password;}
    /** Setter della variabile di istanza indirizzo 
     * @param indirizzo
    */
    public void setIndirizzo(String indirizzo) {this.indirizzo = indirizzo;}
    /** Setter della variabile di istanza telefono 
     * @param telefono
    */
    public void setTelefono(String telefono) {this.telefono = telefono;}
    /** Setter della variabile di istanza cartaDiCredito 
     * @param cartaDiCredito
    */
    public void setCartaDiCredito(String cartaDiCredito) {this.cartaDiCredito = cartaDiCredito;}
}
