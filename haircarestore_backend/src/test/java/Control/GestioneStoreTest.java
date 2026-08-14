package Control;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import App.HairCareStoreApplication;
import Database.ClienteRegistratoRepository;
import Database.FattorinoRepository;
import Database.ProdottoRepository;
import Entity.EntityClienteRegistrato;
import Entity.EntityFattorino;
import Entity.EntityProdotto;
import Exception.AutenticazioneFallitaException;
import Exception.DatabaseException;
import Exception.DisponibilitàEsauritaException;
import Exception.RisorsaNonTrovataException;

/**
 * Suite di test di unità per la classe {@link GestioneStore}.
 * Questa classe convalida il comportamento della logica di business associata al caso d'uso "Acquista Prodotto" (UC07).
 * La verifica copre l'adeguatezza della copertura dei cammini di base ricavati dall'analisi del Control Flow Graph (CFG)
 * dell'elaborato.
 * Utilizzo le annotazioni {@code @SpringBootTest} e {@code @Transactional} per testare i moduli su un database di test.
 * L'esecuzione tramite Transactional garantisce il rollback automatico dello stato del DB al termine di ogni singolo
 * test case, prevenendo la duplicazione tra esecuzioni consecutive.
 * Utilizziamo l'annotazione {@code @Autowired} in quanto subito dopo che JUnit ha creato la classe di test, interviene il
 * modulo SpringExtension (attivato da @SpringBootTest) che scansiona la classe di test e individuando i campi contrassegnati
 * con {@code @Autowired} vi inietta i bean registrati nel IoC container.
 */
@SpringBootTest(classes=HairCareStoreApplication.class)
@Transactional
public class GestioneStoreTest {

    /** Il componente dell'architettura BCE sotto test, iniettato dall'IoC Container. */
    @Autowired
    private GestioneStore gestioneStore;

    /** Accesso al Data Access Object per la gestione dei profili dei Clienti Registrati. */
    @Autowired
    private ClienteRegistratoRepository clienteRegistratoRepository;

    /** Accesso al Data Access Object per l'aggiornamento dei prodotti. */
    @Autowired
    private ProdottoRepository prodottoRepository;

    /** Accesso al Data Access Object per la gestione delle anagrafiche dei fattorini. */
    @Autowired
    private FattorinoRepository fattorinoRepository;

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
            "Shampoo idratante arricchito con cheratina", "SHAMPOO", 10, 10);
        prodotto = prodottoRepository.save(prodotto);

        // 3. Inserimento di un fattorino.
        fattorino = new EntityFattorino("Anna", "Pagano", "3334567890", "anna.pagano@gmail.com");
        fattorino = fattorinoRepository.save(fattorino);
    }

    /**
     * Caso di test del cammino di base 1 individuato nel CFG (Fallimento Autenticazione).
     * {@code @ParameterizedTest}:È un'annotazione che indica al Test Runner che questo metodo non è un test singolo statico,
     * bensì un test guidato dai dati. Il framework eseguirà questo identico metodo tante volte quanti sono gli input forniti
     * dalla sorgente dati, isolando ogni singola esecuzione.
     * @param username Stringa di input che JUnit preleva dal ValueSource ed inietta automaticamente come argomento all'avvio
     * di ogni ciclo di test.
     */
    @ParameterizedTest
    @ValueSource(strings={
        "Utente",
        "Hendrix!"
    })
    public void testCammino1_AutenticazioneFallita(String username){
        /*
         * {@code assertThrows}:
         * È un'asserzione fondamentale di JUnit usata per verificare il comportamento del codice in scenari d'errore.
         * 1° Parametro (AutenticazioneFallitaException.class): Specifica il tipo di eccezione atteso.
         * 2° Parametro (() -> gestioneStore.acquistaProdotto(...)): È un'espressione Lambda che specifica il metodo da eseguire.
         * 3° Parametro: È la stringa di log personalizzata. Verrà stampata nel tab 'Testing' se il test fallisce.
         * Se l'eccezione viene lanciata correttamente, assertThrows la intercetta e la restituisce come oggetto.
         */
        AutenticazioneFallitaException ex = assertThrows(AutenticazioneFallitaException.class,
            () -> gestioneStore.acquistaProdotto(username, "N46007557!", "KER-SHAM250-DRY", 1),
            "Il sistema avrebbe dovuto sollevare un eccezzione di tipo AutenticazioneFallitaException");

            /*
            * {@code assertEquals()}:
            * Effettua una verifica sulla consistenza testuale dell'eccezione.
            * COnfronta il messaggio atteso con quello effettivamente inserito nell'eccezione intercettata.
            * Se le due stringhe non coincidono, il test fallisce.
            */
            assertEquals("Autenticazione fallita. Nome utente o password errati!", ex.getMessage());
    }

    /**
     * Caso di test del cammino di base 2 individuato nel CFG (Risorsa non trovata, codice del prodotto non valido).
     * {@code @ParameterizedTest}:È un'annotazione che indica al Test Runner che questo metodo non è un test singolo statico,
     * bensì un test guidato dai dati. Il framework eseguirà questo identico metodo tante volte quanti sono gli input forniti
     * dalla sorgente dati, isolando ogni singola esecuzione.
     * @param codiceProdotto Stringa di input che JUnit preleva dal ValueSource ed inietta automaticamente come argomento all'avvio
     * di ogni ciclo di test.
     */
    @ParameterizedTest
    @ValueSource(strings={
        "KER-SHAM250-DRI",
        "KER-SHAM250-DRY0",
        "KER-DRY"
    })
    public void testCammino2_ProdottoNonTrovato(String codiceProdotto){
        RisorsaNonTrovataException ex = assertThrows(RisorsaNonTrovataException.class,
            () -> gestioneStore.acquistaProdotto("Hendrix", "N46007557!", codiceProdotto, 1),
            "Il sistema avrebbe dovuto sollevare un eccezzione di tipo RisorsaNonTrovataException"
        );

        assertEquals("Prodotto non trovato nel catalogo!", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {
        11,  // Valore di boundary
        15,  // Valore superiore
        100  // Caso fuori scala
    })
    public void testCammino3_QuantitaInsufficiente(int quantita){
        DisponibilitàEsauritaException ex = assertThrows(DisponibilitàEsauritaException.class,
            () -> gestioneStore.acquistaProdotto("Hendrix", "N46007557!", "KER-SHAM250-DRY", quantita),
            "Il sistema avrebbe dovuto sollevare un eccezzione di tipo DisponibilitàEsauritaException"
        );

        assertEquals("Quantità di prodotto non disponibile!", ex.getMessage());
    }

    @Test
    public void testCammino4_FlussoNormale() throws AutenticazioneFallitaException, RisorsaNonTrovataException,
            DisponibilitàEsauritaException, DatabaseException{
        int quantita = 1;
        float prezzoAtteso = prodotto.getPrezzo() * quantita;

        ArrayList<String> result = gestioneStore.acquistaProdotto("Hendrix", "N46007557!",
            "KER-SHAM250-DRY", quantita);

        //Verifica posizionale accurata dell'output strutturato dell'ArrayList
        assertNotNull(result, "L'istanza dell'ArrayList restituita non deve essere null");
        assertEquals(4, result.size(), "La Control impone un array di esattamente 4 elementi");
        
        // Verifica dei dati scambiati tra Control e Boundary
        assertEquals(String.valueOf(prezzoAtteso), result.get(0), "Il calcolo economico della transazione è errato");
        assertEquals(clienteRegistrato.getCartaDiCredito(), result.get(1), "La carta di credito estratta dal cliente non coincide");
        assertEquals(clienteRegistrato.getIndirizzo(), result.get(2), "L'indirizzo di spedizione per la consegna non coincide");
        
        // Verifica che l'ID dell'ordine sia stato realmente autogenerato dal database (Auto-increment di MySQL).
        // Se restituisse "null" o "0", significherebbe che il salvataggio sul DB tramite repository ha fallito l'operazione di inserimento.
        assertNotEquals("null", result.get(3), "Errore di persistenza: la chiave primaria numerica dell'ordine è rimasta null");
    
    }

    @Test
    public void testCammino5_FattorinoNonDisponibile(){
        // Cancelliamo temporaneamente il fattorino inserito dal BeforeEach per simulare la carenza totale di fattorini
        fattorinoRepository.deleteAll();

        RisorsaNonTrovataException ex = assertThrows(RisorsaNonTrovataException.class,
            () -> gestioneStore.acquistaProdotto("Hendrix", "N46007557!", "KER-SHAM250-DRY", 1),
            "Il sistema avrebbe dovuto sollevare un eccezzione di tipo RisorsaNonTrovataException"
        );

        assertEquals("Nessun fattorino disponibile nel sistema!", ex.getMessage());
    }
}
