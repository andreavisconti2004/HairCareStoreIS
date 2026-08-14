package Entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entità JPA per la tabella ORDINI.
 * JPA (Jakarta Persistence API) e' lo standard Java per la persistenza
 * relazionale. Hibernate ne e' l'implementazione piu' diffusa e quella
 * usata da Spring Boot per default.
 * Utilizziamo le annotazioni:
 * {@code @Entity}: specifica ad Hibernate di essere una classe persistente.
 * {@code @Table}: mappa la classe sulla tabella del Database.
*/
@Entity
@Table(name="ordini")
public class EntityOrdine {

    /**
     * Codice intero identificativo univoco dell'ordine, generato dal Database.
     * Funge da Chiave Primaria della tabella (CONSTRAINT PK_ORDINI). 
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
     * La data in cui è stato effettuato l'ordine da parte del cliente registrato.
     * Mappato come DATE.
     * Vincolo DDL: Campo obbligatorio (NOT NULL).
     * Utilizzata l'annotazione:
     * {@code Column}: mappa il campo Java alla colonna SQL.
     */
    @Column(name="data")
    private LocalDate data;

    /**
     * L'orario in cui è stato effettuato l'ordine da parte del cliente registrato.
     * Mappato come TIME.
     * Vincolo DDL: Campo obbligatorio (NOT NULL).
     * Utilizzata l'annotazione:
     * {@code Column}: mappa il campo Java alla colonna SQL.
     */
    @Column(name="ora")
    private LocalTime ora;

    /**
     * Il costo totale dell'ordine.
     * Mappato come DECIMAL(10,2).
     * Vincolo DDL: Campo obbligatorio (NOT NULL).
     * Utilizzata l'annotazione:
     * {@code Column}: mappa il campo Java alla colonna SQL.
     */
    @Column(name="costo_totale")
    private float costoTotale;

    /**
     * Lo stato dell'ordine.
     * Mappato come ENUM.
     * Vincolo DDL: Campo obbligatorio (NOT NULL). Il valore predefinito è 'In_corso'.
     * Utilizzate le annotazioni:
     * {@code Column}: mappa il campo Java alla colonna SQL.
     * {@code @Enumerated}: mappa l'enum Java alla colonna SQL come stringa testuale.
     */
    @Enumerated(EnumType.STRING)
    @Column(name="stato")
    private Stato stato;

    /**
     * Numero di unita del prodotto ordinate.
     * Mappato come intero (INT).
     * Vincolo DDL: Campo obbligatorio (NOT NULL) vincolato a valori superiori a zero (CHK_QUANTITA).
     * Utilizzata l'annotazione:
     * {@code Column}: mappa il campo Java alla colonna SQL.
     */
    @Column(name="quantita")
    private int quantità;

    /**
     * La data in cui il fattorino ha completato la consegna a domicilio.
     * Mappato come DATE.
     * Vincolo DDL: di default valore pari a NULL, finchè l'ordine è in viaggio.
     * Utilizzata l'annotazione:
     * {@code Column}: mappa il campo Java alla colonna SQL.
     */
    @Column(name="data_consegna")
    private LocalDate dataConsegna;

    /**
     * L'orario in cui il fattorino ha completato la consegna a domicilio.
     * Mappato come TIME.
     * Vincolo DDL: di default valore pari a NULL, finchè l'ordine è in viaggio.
     * Utilizzata l'annotazione:
     * {@code Column}: mappa il campo Java alla colonna SQL.
     */
    @Column(name="ora_consegna")
    private LocalTime oraConsegna;

    /**
     * Associazione molti-a-uno verso il prodotto acquistato.
     * Utilizzate le annotazioni:
     * {@code @ManyToOne}: indica che molti ordini possono fare riferimento allo stesso prodotto.
     * Mappata tramite {FetchType.LAZY} per ottimizzare le prestazioni e ridurre le query JOIN.
     * {@code @JoinColumn}: definisce il nome della colonna fisica di foreign key sul Database (FK_PRODOTTO).
     */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="prodotto")
    private EntityProdotto prodotto;

    /**
     * Associazione molti-a-uno verso il fattorino incaricato della spedizione.
     * Utilizzate le annotazioni:
     * {@code @ManyToOne: indica che molti ordini possono fare riferimento allo stesso fattorino.
     * Mappata tramite {FetchType.LAZY} per ottimizzare le prestazioni e ridurre le query JOIN.
     * @JoinColumn: definisce il nome della colonna fisica di foreign key sul Database (FK_FATTORINO).
     */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="fattorino")
    private EntityFattorino fattorino;

    /**
     * Associazione molti-a-uno verso il cliente che ha effettuato l'acquisto.
     * Utilizzate le annotazioni:
     * {@code @ManyToOne}: indica che molti ordini possono fare riferimento allo stesso cliente registrato.
     * Mappata tramite {FetchType.LAZY} per ottimizzare le prestazioni e ridurre le query JOIN.
     * {@code @JoinColumn}: definisce il nome della colonna fisica di foreign key sul Database (FK_CLIENTI_REGISTRATI).
     */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="cliente_registrato")
    private EntityClienteRegistrato clienteRegistrato;

    /**
     * Costruttore senza argomenti.
     * Richiesto obbligatoriamente dalle specifiche JPA per permettere all'ORM di istanziare
     * l'entita a runtime tramite i meccanismi di reflection.
     */
    public EntityOrdine(){}

    /**
     * Costruttore con argomenti.
     * L'id viene omesso perché se ne occuperà il Database tramite AUTO_INCREMENT.
     * @param data              La data di sottomissione dell'ordine
     * @param ora               L'orario di sottomissione dell'ordine
     * @param costoTotale       Il costo complessivo dell'ordine
     * @param stato             Lo stato dell'ordine 
     * @param quantità          La quantità di prodotti acquistati
     * @param dataConsegna      La data di consegna
     * @param oraConsegna       L'orario di consegna
     * @param prodotto          Il riferimento al prodotto acquistato
     * @param fattorino         Il riferimento al fattorino incaricato
     * @param clienteRegistrato Il riferimento al cliente registrato
     */
    public EntityOrdine(LocalDate data, LocalTime ora, float costoTotale, Stato stato, int quantità,
            LocalDate dataConsegna, LocalTime oraConsegna, EntityProdotto prodotto, EntityFattorino fattorino, 
                EntityClienteRegistrato clienteRegistrato) {
        this.data = data;
        this.ora = ora;
        this.costoTotale = costoTotale;
        this.stato = stato;
        this.quantità = quantità;
        this.dataConsegna = dataConsegna;
        this.oraConsegna = oraConsegna;
        this.prodotto = prodotto;
        this.fattorino = fattorino;
        this.clienteRegistrato = clienteRegistrato;
    }

    /**
     * Costruttore con argomenti specifico per la creazione iniziale dell'ordine.
     * L'id viene omesso perché se ne occuperà il Database tramite AUTO_INCREMENT.
     * La data e l'ora di consegna vengono omesse poiché l'ordine non è ancora stato consegnato.
     * Lo stato viene omesso, affinchè si trovi al suo valore di default 'In_corso'.
     * @param data              La data di sottomissione dell'ordine
     * @param ora               L'orario di sottomissione dell'ordine
     * @param costoTotale       Il costo complessivo dell'ordine
     * @param quantità          La quantità di prodotti acquistati
     * @param prodotto          Il riferimento al prodotto acquistato
     * @param fattorino         Il riferimento al fattorino incaricato
     * @param clienteRegistrato Il riferimento al cliente registrato
     */
    public EntityOrdine(LocalDate data, LocalTime ora, float costoTotale, int quantità, EntityProdotto prodotto,
            EntityFattorino fattorino, EntityClienteRegistrato clienteRegistrato) {
        this.data = data;
        this.ora = ora;
        this.costoTotale = costoTotale;
        this.stato=Stato.In_corso;
        this.quantità = quantità;
        this.prodotto = prodotto;
        this.fattorino = fattorino;
        this.clienteRegistrato = clienteRegistrato;
    }

    /** Getter della variabile di istanza id 
     * @return id
    */
    public int getId() {return id;}
    /** Getter della variabile di istanza data 
     * @return data
    */
    public LocalDate getData() {return data;}
    /** Getter della variabile di istanza ora 
     * @return ora
    */
    public LocalTime getOra() {return ora;}
    /** Getter della variabile di istanza costoTotale 
     * @return costoTotale
    */
    public float getCostoTotale() {return costoTotale;}
    /** Getter della variabile di istanza stato
     * @return stato
     */
    public Stato getStato() {return stato;}
    /** Getter della variabile di istanza quantità 
     * @return quantità
    */
    public int getQuantità() {return quantità;}
    /** Getter della variabile di istanza dataConsegna 
     * @return dataConsegna
    */
    public LocalDate getDataConsegna() {return dataConsegna;}
    /** Getter della variabile di istanza oraConsegna 
     * @return oraConsegna
    */
    public LocalTime getOraConsegna() {return oraConsegna;}
    /** Getter del riferimento a prodotto 
     * @return prodotto
    */
    public EntityProdotto getProdotto() {return prodotto;}
    /** Getter del riferimento a fattorino 
     * @return fattorino
    */
    public EntityFattorino getFattorino() {return fattorino;}
    /** Getter del riferimento a clienteRegistrato 
     * @return clienteRegistrato
    */
    public EntityClienteRegistrato getClienteRegistrato() {return clienteRegistrato;}


    /** Setter della variabile di istanza data 
     * @param data
    */
    public void setData(LocalDate data) {this.data = data;}
    /** Setter della variabile di istanza ota 
     * @param ora
    */
    public void setOra(LocalTime ora) {this.ora = ora;}
    /** Setter della variabile di istanza costoTotale 
     * @param costoTotale
    */
    public void setCostoTotale(float costoTotale) {this.costoTotale = costoTotale;}
    /** Setter della variabile di istanza stato 
     * @param stato
    */
    public void setStato(Stato stato) {this.stato = stato;}
    /** Setter della variabile di istanza quantità 
     * @param quantità
    */
    public void setQuantità(int quantità) {this.quantità = quantità;}
    /** Setter della variabile di istanza dataConsegna 
     * @param dataConsegna
    */
    public void setDataConsegna(LocalDate dataConsegna) {this.dataConsegna = dataConsegna;}
    /** Setter della variabile di istanza oraConsegna 
     * @param oraConsegna
    */
    public void setOraConsegna(LocalTime oraConsegna) {this.oraConsegna = oraConsegna;}
    /** Setter del riferimento a prodotto 
     * @param prodotto
    */
    public void setProdotto(EntityProdotto prodotto) {this.prodotto = prodotto;}
    /** Setter del riferimento a fattorino 
     * @param fattorino
    */
    public void setFattorino(EntityFattorino fattorino) {this.fattorino = fattorino;}
    /** Setter del riferimento a clienteRegistrato 
     * @param clienteRegistrato
    */
    public void setClienteRegistrato(EntityClienteRegistrato clienteRegistrato) {this.clienteRegistrato = clienteRegistrato;}
}
