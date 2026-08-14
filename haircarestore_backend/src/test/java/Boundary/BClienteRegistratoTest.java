package Boundary;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import App.HairCareStoreApplication;
import DTO.RequestAcquistoDTO;
import DTO.RequestAnnullamentoDTO;
import DTO.RequestEmissioneDTO;
import DTO.ResponseAcquistoDTO;
import Database.ClienteRegistratoRepository;
import Database.FattorinoRepository;
import Database.OrdineRepository;
import Database.ProdottoRepository;
import Entity.EntityClienteRegistrato;
import Entity.EntityFattorino;
import Entity.EntityOrdine;
import Entity.EntityProdotto;
import Entity.Stato;

/**
 * Questa classe implementa l'intera pianificazione dei casi di test funzionali progettati nella sottosezione 4.1
 * dell'elaborato per il caso d'uso "Acquista Prodotto" (UC07). La test Suite, essendo progettata sulla base di una
 * tecnica di test black-box, è realizzata interagendo con lo strato Boundary. Infatti, l'obiettivo è quello di rilevare
 * malfunzionamenti generati durante il passaggio dei parametri di input, confrontare l'output ottenuto con quello
 * atteso specificato nell'oracolo e che la consistenza del database sia mantenuta.
 * Utilizzo le annotazioni {@code @SpringBootTest} e {@code @Transactional} per testare i moduli su un database di test.
 * L'esecuzione tramite Transactional garantisce il rollback automatico dello stato del DB al termine di ogni singolo
 * test case, prevenendo la duplicazione tra esecuzioni consecutive.
 * Utilizziamo l'annotazione {@code @Autowired} in quanto subito dopo che JUnit ha creato la classe di test, interviene il
 * modulo SpringExtension (attivato da @SpringBootTest) che scansiona la classe di test e individuando i campi contrassegnati
 * con {@code @Autowired} vi inietta i bean registrati nel IoC container.
 */
@SpringBootTest(classes=HairCareStoreApplication.class)
@Transactional
@SuppressWarnings("unchecked")
public class BClienteRegistratoTest {

    /** Il componente dell'architettura BCE sotto test, iniettato dall'IoC Container. */
    @Autowired
    private BClienteRegistrato bClienteRegistrato;

    /**
     * Nel metodo di acquistaProdotto(), la boundary si aspetta che il framework esegua automaticamente la validazione sintattica
     * del DTO e popoli l'oggetto BindingResult prima di entrare nel metodo. Tuttavia, alla scrittura dei test, eseguendo una chiamata
     * Java diretta al metodo della classe, stiamo aggirando l'infrastruttura di rete di Spring Boot. Di conseguenza, il controllo
     * automatico delle annotazioni sul DTO non parte da solo. Dobbiamo quindi riprodurre manualmente nel test quello che Spring
     * fa normalmente a runtime.
     */
    @Autowired
    private Validator validator;

    /** Accesso al Data Access Object per la gestione dei profili dei Clienti Registrati. */
    @Autowired
    private ClienteRegistratoRepository clienteRegistratoRepository;

    /** Accesso al Data Access Object per l'aggiornamento dei prodotti. */
    @Autowired
    private ProdottoRepository prodottoRepository;

    /** Accesso al Data Access Object per la gestione delle anagrafiche dei fattorini. */
    @Autowired
    private FattorinoRepository fattorinoRepository;

    /** Accesso al Data Access Object per la gestione degli ordini. */
    @Autowired
    private OrdineRepository ordineRepository;

    private EntityClienteRegistrato clienteRegistrato;
    private EntityProdotto prodotto;
    private EntityFattorino fattorino;

    /**
     * {@code @BeforeEach}: Identifica il metodo di Setup della test Suite.
     * Il metodo viene eseguito obbligatoriamente prima di ogni singolo caso di test.
     * Poiché la classe è {@code @Transactional}, ad ogni esecuzione il DB parte vuoto. Questo metodo inserisce i 3 record,
     * i test eseguono la loro logica, ed infine avviene il rollback, azzerando la duplicazione dei dati.
     */
    @BeforeEach
    public void setUp() {
        // 1. Inserimento di un cliente registrato.
        clienteRegistrato = new EntityClienteRegistrato("Hendrix", "N46007557!", "Via Marenola 22, Napoli",
            "3922539102", "1234567890123456");
        clienteRegistrato = clienteRegistratoRepository.save(clienteRegistrato);

        // 2. Inserimento di un prodotto.
        prodotto = new EntityProdotto("KER-SHAM250-DRY", "Shampoo Nutriente Capelli Secchi 250ml",
            "Shampoo idratante arricchito con cheratina", "SHAMPOO", 15, 10);
        prodotto = prodottoRepository.save(prodotto);

        // 3. Inserimento di un fattorino.
        fattorino = new EntityFattorino("Anna", "Pagano", "3334567890", "anna.pagano@gmail.com");
        fattorino = fattorinoRepository.save(fattorino);
    }

    /**
     * Caso di test TC01 (Acquisto completato con successo).
     * Verifica che con dati corretti il sistema restituisca HTTP 200 OK ed elabori l'ordine.
     */
    @Test
    public void testTC01_AcquistoCompletatoConSuccesso() {
        RequestAcquistoDTO requestDto = new RequestAcquistoDTO("Hendrix", "N46007557!",
            "KER-SHAM250-DRY", 2);

        /**
        * 1. BindingResult result: Istanzia un oggetto di Spring preposto alla registrazione degli errori di validazione
        *  associati ad uno specifico DTO.
        * 2. springValidator.validate(requestDto, result) attiva esplicitamente il vero motore di validazione integrato nel framework Spring Boot.
        * Il validatore prende il requestDto, va a leggere le annotazioni di vincolo sintattico scritte nel file e le confronta con i valori
        * inseriti. Se gli input sono errati, il motore inserisce automaticamente dentro result l'esatto messaggio di errore configurato nel DTO.
        */
        BindingResult result = new BeanPropertyBindingResult(requestDto, "requestDto");
        validator.validate(requestDto, result);

        ResponseEntity<?> responseAcquisto = bClienteRegistrato.acquistaProdotto(requestDto, result);

        // Assert: Ispezione dello status code dell'oggetto restituito dalla boundary.
        assertEquals(HttpStatus.OK, responseAcquisto.getStatusCode(), "Dovrebbe restituire lo stato HTTP 200 OK");

        // Verifichiamo che il body corrisponda con l'oggetto DTO di risposta
        ResponseAcquistoDTO bodyAcquisto = (ResponseAcquistoDTO) responseAcquisto.getBody();
        assertNotNull(bodyAcquisto);
        assertEquals("30.0", bodyAcquisto.getCostoTotale());
        assertEquals("1234567890123456", bodyAcquisto.getCartaDiCredito());
        assertEquals("Via Marenola 22, Napoli", bodyAcquisto.getIndirizzo());

        // Recuperiamo il codice generato per passarlo alla fase di emissione dell'ordine
        int codiceOrdine = bodyAcquisto.getCodiceOrdine();

        //L'utente conferma l'acquisto, dunque costruisco il DTO contenente il codice dell'ordine da passare al metodo di emissione
        RequestEmissioneDTO emissioneDto = new RequestEmissioneDTO(codiceOrdine);

        ResponseEntity<?> responseEmissione = bClienteRegistrato.emettiOrdine(emissioneDto);

        // Verifiche finali sulla ricevuta di avvenuta emissione
        assertEquals(HttpStatus.OK, responseEmissione.getStatusCode());
        Map<String, String> bodyEmissione = (Map<String, String>) responseEmissione.getBody();
        assertNotNull(bodyEmissione);
        assertEquals("Acquisto effettuato con successo!", bodyEmissione.get("message"));

        // 1. Verifica decremento nel magazzino: (10 iniziali - 2 acquistati = 8 attesi)
        assertEquals(8, prodotto.getQuantità(), "Post-condizione fallita: Il prodotto non è stato decrementato di 2 unità");

        // 2. Verifica dello stato dell'ordine registrato
        EntityOrdine ordineRegistrato = ordineRepository.findById(codiceOrdine).orElse(null);
        assertNotNull(ordineRegistrato, "L'ordine emesso deve essere registrato nel DB");
        assertEquals(Stato.Spedito, ordineRegistrato.getStato(), "Post-condizione fallita: Lo stato dell'ordine emesso non risulta Spedito");
    }

    /**
     * Caso di test TC02 (Nome utente > 20 caratteri).
     * Verifica che il sistema restituisca HTTP BAD REQUEST ed il messaggio "Formato nome utente non valido, troppo lungo!"
     */
    @Test
    public void testTC02_UsernameTroppoLungo() {
        RequestAcquistoDTO requestDto = new RequestAcquistoDTO("andreavisconti1234567", "N46007557!",
            "KER-SHAM250-DRY", 2);

        BindingResult result = new BeanPropertyBindingResult(requestDto, "requestDto");
        validator.validate(requestDto, result);

        ResponseEntity<?> response = bClienteRegistrato.acquistaProdotto(requestDto, result);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        assertEquals("Formato nome utente non valido, troppo lungo!", body.get("message"));
    }

    /**
     * Caso di test TC03 (Nome utente contiene caratteri non alfanumerici).
     * Verifica che il sistema restituisca HTTP BAD REQUEST ed il messaggio "Formato nome utente non valido, contiene caratteri non consentiti!"
     */
    @Test
    public void testTC03_UsernameConSimboliNonConsentiti() {
        RequestAcquistoDTO requestDto = new RequestAcquistoDTO("Hendrix_", "N46007557!",
            "KER-SHAM250-DRY", 2);

        BindingResult result = new BeanPropertyBindingResult(requestDto, "requestDto");
        validator.validate(requestDto, result);

        ResponseEntity<?> response = bClienteRegistrato.acquistaProdotto(requestDto, result);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        assertEquals("Formato nome utente non valido, contiene caratteri non consentiti!", body.get("message"));
    }

    /**
     * Caso di test TC04 (Nome utente vuoto).
     * Verifica che il sistema restituisca HTTP BAD REQUEST ed il messaggio "Inserire un Nome Utente!
     */
    @Test
    public void testTC04_UsernameVuoto() {
        RequestAcquistoDTO requestDto = new RequestAcquistoDTO("", "N46007557!",
            "KER-SHAM250-DRY", 2);

        BindingResult result = new BeanPropertyBindingResult(requestDto, "requestDto");
        validator.validate(requestDto, result);

        ResponseEntity<?> response = bClienteRegistrato.acquistaProdotto(requestDto, result);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        
        String messaggio = body.get("message");
        assertTrue(
            messaggio.equals("Inserire un Nome Utente!") ||
            messaggio.equals("Formato nome utente non valido, contiene caratteri non consentiti!"),
            "Fallito: Il messaggio ricevuto non corrisponde a nessuna delle violazioni attese. Ricevuto: " + messaggio
        );
    }

    /**
     * Caso di test TC05 (Password con lunghezza che sia < 8 caratteri o > di 20 caratteri).
     * Verifica che il sistema restituisca HTTP BAD REQUEST ed il messaggio "Formato password non valido!"!
     */
    @ParameterizedTest
    @ValueSource(strings={
        "N4600!",
        "N46007557!123456789012"
    })
    public void testTC05_PasswordTroppoCorta(String password) {
        RequestAcquistoDTO requestDto = new RequestAcquistoDTO("Hendrix", password,
            "KER-SHAM250-DRY", 2);

        BindingResult result = new BeanPropertyBindingResult(requestDto, "requestDto");
        validator.validate(requestDto, result);

        ResponseEntity<?> response = bClienteRegistrato.acquistaProdotto(requestDto, result);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        assertEquals("Formato password non valido!", body.get("message"));
    }

    /**
     * Caso di test TC06 (Password senza la presenza di almeno un carattere speciale).
     * Verifica che il sistema restituisca HTTP BAD REQUEST ed il messaggio "Formato password non valido, carattere speciale mancante!"!
     */
    @Test
    public void testTC06_PasswordMancanteCarattereSpeciale() {
        RequestAcquistoDTO requestDto = new RequestAcquistoDTO("Hendrix", "N46007557",
            "KER-SHAM250-DRY", 2);

        BindingResult result = new BeanPropertyBindingResult(requestDto, "requestDto");
        validator.validate(requestDto, result);

        ResponseEntity<?> response = bClienteRegistrato.acquistaProdotto(requestDto, result);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        assertEquals("Formato password non valido, carattere speciale mancante!", body.get("message"));
    }

    /**
     * Caso di test TC07 (Password vuota).
     * Verifica che il sistema restituisca HTTP BAD REQUEST ed il messaggio "Inserire la password!"
     * Il caso di test è fallito. La causa risiede nella gestione delle annotazioni di errore da parte di SpringBoot
     * al momento della chiamata del metodo validate sull'oggetto validator. Infatti dal momento in cui vengono sollevamente
     * contemporaneamente più eccezzioni:
     * 1. lunghezza minima non soddisfatta
     * 2. carattere speciale mancante
     * 3. stringa vuota
     * la boundary raccoglie il primo errore rilevato e registrato nel BindingResult. Nello specifico il primo errore rilevato è
     * quello relativo all'assenza del carattere speciale nel pattern della password. Dunque si è deciso di verificare che almeno
     * uno dei possibili messaggi di errori sia effettivamente sollevato.
     */
    @Test
    public void testTC07_PasswordVuota() {
        RequestAcquistoDTO requestDto = new RequestAcquistoDTO("Hendrix", "",
            "KER-SHAM250-DRY", 2);

        BindingResult result = new BeanPropertyBindingResult(requestDto, "requestDto");
        validator.validate(requestDto, result);

        ResponseEntity<?> response = bClienteRegistrato.acquistaProdotto(requestDto, result);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);

        String messaggio = body.get("message");
        assertTrue(
            messaggio.equals("Inserire la password!") ||
            messaggio.equals("Formato password non valido, carattere speciale mancante!") ||
            messaggio.equals("Formato password non valido!"),
            "Fallito: Il messaggio ricevuto non corrisponde a nessuna delle violazioni attese. Ricevuto: " + messaggio
        );
    }


    /**
     * Caso di test TC08 (Codice prodotto con lunghezza =! 15 caratteri).
     * Verifica che il sistema restituisca HTTP BAD REQUEST ed il messaggio "Prodotto non valido, lunghezza non valida!"
     */
    @Test
    public void testTC08_CodiceProdottoLunghezzaInvalida() {
        RequestAcquistoDTO requestDto = new RequestAcquistoDTO("Hendrix", "N46007557!",
            "KER-SHAM250-DRY1", 2);

        BindingResult result = new BeanPropertyBindingResult(requestDto, "requestDto");
        validator.validate(requestDto, result);

        ResponseEntity<?> response = bClienteRegistrato.acquistaProdotto(requestDto, result);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        assertEquals("Prodotto non valido, lunghezza non valida!", body.get("message"));
    }

    /**
     * Caso di test TC09 (Codice prodotto con carateri non alfanumerici).
     * Verifica che il sistema restituisca HTTP BAD REQUEST ed il messaggio "Prodotto non valido, formattazione errata!"
     */
    @Test
    public void testTC09_CodiceProdottoFormattazioneErrata() {
        RequestAcquistoDTO requestDto = new RequestAcquistoDTO("Hendrix", "N46007557!",
            "KER-SHAM250-DR?", 2);

        BindingResult result = new BeanPropertyBindingResult(requestDto, "requestDto");
        validator.validate(requestDto, result);

        ResponseEntity<?> response = bClienteRegistrato.acquistaProdotto(requestDto, result);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        assertEquals("Prodotto non valido, formattazione errata!", body.get("message"));
    }

    /**
     * Caso di test TC10 (Codice prodotto vuoto).
     * Verifica che il sistema restituisca HTTP BAD REQUEST ed il messaggio "Inserire un Prodotto!"
     */
    @Test
    public void testTC10_CodiceProdottoVuoto() {
        RequestAcquistoDTO requestDto = new RequestAcquistoDTO("Hendrix", "N46007557!",
            "", 2);
            
        BindingResult result = new BeanPropertyBindingResult(requestDto, "requestDto");
        validator.validate(requestDto, result);

        ResponseEntity<?> response = bClienteRegistrato.acquistaProdotto(requestDto, result);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        
        String messaggio = body.get("message");
        assertTrue(
            messaggio.equals("Inserire un Prodotto!") ||
            messaggio.equals("Prodotto non valido, lunghezza non valida!") ||
            messaggio.equals("Prodotto non valido, formattazione errata!"),
            "Fallito: Il messaggio ricevuto non corrisponde a nessuna delle violazioni attese. Ricevuto: " + messaggio
        );
    }


    /**
     * Caso di test TC11 (Quantità = 0).
     * Verifica che il sistema restituisca HTTP BAD REQUEST ed il messaggio "Inserire una quantità positiva!"
     */
    @Test
    public void testTC11_QuantitaPariAZero() {
        RequestAcquistoDTO requestDto = new RequestAcquistoDTO("Hendrix", "N46007557!",
            "KER-SHAM250-DRY", 0);

        BindingResult result = new BeanPropertyBindingResult(requestDto, "requestDto");
        validator.validate(requestDto, result);

        ResponseEntity<?> response = bClienteRegistrato.acquistaProdotto(requestDto, result);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        assertEquals("Inserire una quantità positiva!", body.get("message"));
    }

    /**
     * Caso di test TC12 (Quantità < 0).
     * Verifica che il sistema restituisca HTTP BAD REQUEST ed il messaggio "Inserire una quantità positiva!"
     */
    @Test
    public void testTC12_QuantitaNegativa() {
        RequestAcquistoDTO requestDto = new RequestAcquistoDTO("Hendrix", "N46007557!",
            "KER-SHAM250-DRY", -1);

        BindingResult result = new BeanPropertyBindingResult(requestDto, "requestDto");
        validator.validate(requestDto, result);

        ResponseEntity<?> response = bClienteRegistrato.acquistaProdotto(requestDto, result);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        assertEquals("Inserire una quantità positiva!", body.get("message"));
    }

    /**
     * Caso di test TC13 (Conferma = false).
     * Il cliente ha iniziato il caso d'uso richhiedendo l'acquisto del prodotto ma successivamente non ha confermato procedendo
     * con l'emissione del pagamento, ma ha annullato l'ordine.
     * Verifica che il sistema restituisca HTTP OK ed il messaggio "Ordine temporaneo annullato e rimosso con successo dal Database."
     * Inoltre affinchè sia mantenuta l'integrità del Database la quantità di prodotto non deve essere stata modificata e l'ordine
     * deve essere stato cancellato correttamente dal database.
     */
    @Test
    public void testTC13_AnnullamentoOrdineInCorso() {
        //L'utente inizia il caso d'uso richiedendo l'acquisto di un prodotto dal catalogo
        RequestAcquistoDTO requestDto = new RequestAcquistoDTO("Hendrix", "N46007557!",
            "KER-SHAM250-DRY", 2);

        BindingResult result = new BeanPropertyBindingResult(requestDto, "requestDto");
        validator.validate(requestDto, result);
        
        ResponseEntity<?> responseAcquisto = bClienteRegistrato.acquistaProdotto(requestDto, result);
        assertEquals(HttpStatus.OK, responseAcquisto.getStatusCode());
        
        ResponseAcquistoDTO bodyAcquisto = (ResponseAcquistoDTO) responseAcquisto.getBody();
        assertNotNull(bodyAcquisto);
        int codiceOrdine = bodyAcquisto.getCodiceOrdine();
        
        //L'utente non conferma (False) invocando l'annullamento
        RequestAnnullamentoDTO annullamentoDto = new RequestAnnullamentoDTO(codiceOrdine);

        ResponseEntity<?> responseAnnulla = bClienteRegistrato.annullaOrdine(annullamentoDto);

        assertEquals(HttpStatus.OK, responseAnnulla.getStatusCode());
        Map<String, String> bodyAnnulla = (Map<String, String>) responseAnnulla.getBody();
        assertNotNull(bodyAnnulla);
        assertEquals("Ordine temporaneo annullato e rimosso con successo dal Database.", bodyAnnulla.get("message"));

        // Verifica delle post-condizioni sull'annullamento
        // 1. L'ordine deve essere stato cancellato fisicamente (conteggio ordini = 0)
        assertEquals(0, ordineRepository.count(), "L'ordine non è stato eliminato dal DB a seguito dell'annullamento");

        // 2. Lo stock del prodotto NON deve essere stato modificato, deve rimanere a 10 unità
        assertEquals(10, prodotto.getQuantità(), "L'annullamento dell'ordine ha erroneamente modificato lo stock di magazzino");
    }

    /**
     * Caso di test TC14 (Nome utente valido ma non presente nel database)
     * Verifica che il sistema restituisca HTTP OK ed il messaggio "Autenticazione fallita. Nome utente o password errati!"
     */
    @Test
    public void testTC14_AutenticazioneFallita_UtenteNonPresente() {
        RequestAcquistoDTO requestDto = new RequestAcquistoDTO("Hhendrix", "N46007557!",
            "KER-SHAM250-DRY", 2);

        BindingResult result = new BeanPropertyBindingResult(requestDto, "requestDto");
        validator.validate(requestDto, result);

        ResponseEntity<?> response = bClienteRegistrato.acquistaProdotto(requestDto, result);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        assertEquals("Autenticazione fallita. Nome utente o password errati!", body.get("message"));
    }

    /**
     * Caso di test TC15 (Password valida ma non presente nel database)
     * Verifica che il sistema restituisca HTTP OK ed il messaggio "Autenticazione fallita. Nome utente o password errati!"
     */
    @Test
    public void testTC15_AutenticazioneFallita_PasswordErrata() {
        RequestAcquistoDTO requestDto = new RequestAcquistoDTO("Hendrix", "N4600755!",
            "KER-SHAM250-DRY", 2);

        BindingResult result = new BeanPropertyBindingResult(requestDto, "requestDto");
        validator.validate(requestDto, result);

        ResponseEntity<?> response = bClienteRegistrato.acquistaProdotto(requestDto, result);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        assertEquals("Autenticazione fallita. Nome utente o password errati!", body.get("message"));
    }

    /**
     * Caso di test TC16 (Quantità richiesta maggiore di quella disponibile nel database)
     * Verifica che il sistema restituisca HTTP OK ed il messaggio "Quantità di prodotto non disponibile!"
     */
    @Test
    public void testTC16_QuantitaSelezionataSuperioreAlloStock() {
        RequestAcquistoDTO requestDto = new RequestAcquistoDTO("Hendrix", "N46007557!",
            "KER-SHAM250-DRY", 15);

        BindingResult result = new BeanPropertyBindingResult(requestDto, "requestDto");
        validator.validate(requestDto, result);

        ResponseEntity<?> response = bClienteRegistrato.acquistaProdotto(requestDto, result);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        assertEquals("Quantità di prodotto non disponibile!", body.get("message"));
    }
}
