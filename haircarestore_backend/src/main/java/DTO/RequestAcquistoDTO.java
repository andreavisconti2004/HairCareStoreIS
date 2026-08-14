package DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) destinato alla validazione formale del payload JSON in ingresso per 
 * la prima fase del caso d'uso "Acquista Prodotto" (UC07). In questo modo è possibie ottenere una
 * corrispondenza completa con il Model definito sul Front-End, evitando di dover effettuare cast e
 * conversioni esplicite del payload. Infatti, il payload JSON in ingresso, alternativamente a questa 
 * strategia, sarebbe stato mappato con un Conteiner Map. Ovvero avremmo ottenuto una struttura chiave:valore, 
 * dove le chiavi sarebbero state stringhe e i valori Object.
 * La classe isola la logica di comunicazione di rete dalle entità interne di dominio, agendo come 
 * un filtro sintattico per lo strato Control. Infatti, respinge le richieste sintatticamente non valide
 * tramite eccezioni di tipo {@code MethodArgumentNotValidException}, garantendo l'equivalenza con la Test
 * Suite dell'elaborato, evitando di dover effettuare controlli sintattici e semantivi espliciti sulla Boundary.
 */
public class RequestAcquistoDTO {

    /**
     * Username identificativo del cliente registrato. Utilizziamo le seguenti annotazioni: 
     * {@code @NotBlank}: Intercetta stringhe vuote o sature di spazi, soddisfacendo il test case TC04.
     * {@code @Size}: Impone il limite massimo di 20 caratteri, soddisfacendo il test case TC02.
     * {@code @Pattern}: Applica la regex alfanumerica {@code ^[a-zA-Z0-9]+$}, non accettando input con caratteri
     * non alfanumerici, riflettendo il vincolo SQL {@code CHK_NOME_UTENTE} della tabella {@code CLIENTI_REGISTRATI}
     * ed il test case TC03.
     */
    @NotBlank(message= "Inserire un Nome Utente!")
    @Size(max = 20, message = "Formato nome utente non valido, troppo lungo!")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Formato nome utente non valido, contiene caratteri non consentiti!")
    private String nomeUtente;

    /**
     * Password per l'autenticazione del profilo. Utilizziamo le seguenti annotazioni: 
     * {@code @NotBlank}: Intercetta stringhe vuote o sature di spazi, soddisfacendo il test case TC07.
     * {@code @Size}: Vincola la lunghezza nel range [8, 20], soddisfacendo il test case TC05.
     * {@code @Pattern} Applica la regex {@code [^a-zA-Z0-9]} per imporre la presenza di almeno un carattere
     * speciale, riflettendo il vincolo SQL {@code CHK_PASSWORD} della tabella {@code CLIENTI_REGISTRATI} ed
     * il test case TC06.
     */
    @NotBlank(message = "Inserire la password!")
    @Size(min = 8, max = 20, message = "Formato password non valido!")
    @Pattern(regexp = ".*[^a-zA-Z0-9].*", message = "Formato password non valido, carattere speciale mancante!")
    private String password;

    /**
     * Il codice alfanumerico del prodotto da acquistare. Utilizziamo le seguenti annotazioni:
     * {@code @NotBlank}: Intercetta stringhe vuote o sature di spazi, soddisfacendo il test case TC10.
     * {@code @Size}: Impone una lunghezza fissa di esattamente 15 caratteri, soddisfacendo il test case TC08.
     * {@code @Pattern}: Valida la formattazione con trattino separatore, riflettendo il vincolo SQL
     * {@code CHK_CODICE} della tabella {@code PRODOTTI} ed il test case TC09.
     */
    @NotBlank(message = "Inserire un Prodotto!")
    @Size(min = 15, max = 15, message = "Prodotto non valido, lunghezza non valida!")
    @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "Prodotto non valido, formattazione errata!")
    private String codiceProdotto;

    /**
     * La quantità di articolo richiesta dall'utente.
     * {@code @Min}: Impone un valore strettamente maggiore o uguale a 1. Questa regola intercetta 
     * e respinge sia le richieste di acquisto nulle (TC11) sia l'inserimento di quantità negative (TC12), 
     * riflettendo il vincolo SQL {@code CHK_QUANTITA} presente nella tabella {@code ORDINI}.
     */
    @Min(value = 1, message = "Inserire una quantità positiva!")
    private Integer quantita;

    /**
     * Costruttore vuoto.
     * Richiesto dalla libreria Jackson per consentire l'allocazione dell'oggetto a runtime tramite i 
     * meccanismi di riflessione Java durante la deserializzazione del JSON.
     */
    public RequestAcquistoDTO() {
    }

    /**
     * Costruttore con argomenti.
     * @param nomeUtente     Username del cliente registrato
     * @param password       Password di sicurezza
     * @param codiceProdotto Il codice del prodotto da acquistare
     * @param quantità       Il numero di pezzi richiesti
     */
    public RequestAcquistoDTO(String nomeUtente, String password, String codiceProdotto, Integer quantità) {
        this.nomeUtente = nomeUtente;
        this.password = password;
        this.codiceProdotto = codiceProdotto;
        this.quantita = quantità;
    }

    /** Getter della variabile di istanza nomeUtente 
     * @return nomeUtente
    */
    public String getNomeUtente() {return nomeUtente;}
    /** Getter della variabile di istanza password 
     * @return password
    */
    public String getPassword() {return password;}
    /** Getter della variabile di istanza codiceProdotto 
     * @return codiceProdotto
    */
    public String getCodiceProdotto() {return codiceProdotto;}
    /** Getter della variabile di istanza quantità 
     * @return quantità
    */
    public Integer getQuantita() {return quantita;}

    /** Setter della variabile di istanza nomeUtente 
     * @param nomeUtente
    */
    public void setNomeUtente(String nomeUtente) {this.nomeUtente = nomeUtente;}
    /** Setter della variabile di istanza password 
     * @param password
    */
    public void setPassword(String password) {this.password = password;}
    /** Setter della variabile di istanza codiceProdotto 
     * @param codiceProdotto
    */
    public void setCodiceProdotto(String codiceProdotto) {this.codiceProdotto = codiceProdotto;}
    /** Setter della variabile di istanza quantità 
     * @param quantità
    */
    public void setQuantita(Integer quantità) {this.quantita = quantità;}
}
