package Control;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;

import Database.ClienteRegistratoRepository;
import Database.FattorinoRepository;
import Database.OrdineRepository;
import Database.ProdottoRepository;
import Entity.EntityClienteRegistrato;
import Entity.EntityFattorino;
import Entity.EntityOrdine;
import Entity.EntityProdotto;
import Entity.Stato;
import Exception.AutenticazioneFallitaException;
import Exception.DatabaseException;
import Exception.DisponibilitàEsauritaException;
import Exception.RisorsaNonTrovataException;

/**
 * Strato Control della BCED, dedita all'implementazione della logica di business.
 * All'interno del sistema sviluppato, i componenti strutturali non vengono istanziati mediante 
 * l'operatore nativo new del linguaggio Java, bensì la loro gestione è interamente delegata al
 * container IoC (Inversion of Control) di Spring Boot. Un oggetto il cui intero ciclo di vita,
 * inclusi i processi di istanziazione, configurazione, assemblaggio e distruzione, è coordinato 
 * dal framework prende il nome di Spring Bean. 
 * La registrazione dei componenti all'interno del container avviene in modalità dichiarativa 
 * sfruttando gli stereotipi standard forniti dal framework. Per poter registrare lo strato Control
 * al container e dichiararla esplicitamente un Bean, utilizziamo l'annotazione:
 * <p> {@code @Service}: rende esplicito il ruolo della classe all'interno dell'architettura BCED.
 * Per garantire l'efficienza computazionale e la coerenza architetturale, ad ogni Spring Bean del 
 * sistema viene associato lo scope Singleton di default. Sotto questo ciclo di vita, l'IoC Container 
 * crea una sola e unica istanza del componente per l'intero ciclo di vita dell'ApplicationContext.
 * Anche i repository non sono istanziati manualmente, ma ci limitiamo a dichiarare i relativi
 * attributi come final e li accettiamo come parametri di ingresso nel costruttore della classe.
 * Spring provvede a rintracciare i bean candidati all'interno del proprio "ApplicationContext"
 * e li inietta a runtime.
 */
@Service
public class GestioneStore {

    /**
     * Riferimento al Repository per la gestione della persistenza dell'entità cliente registrato.
     * Dichiarato {@code final} per garantire l'immutabilità del riferimento dopo l'inizializzazione.
     * Interagisce direttamente con la tabella CLIENTI_REGISTRATI.
     */
    private final ClienteRegistratoRepository clienteRegistratoRepository;

    /**
     * Riferimento al Repository per la gestione della persistenza dell'entità Fattorino.
     * Dichiarato {@code final} per garantire l'immutabilità del riferimento dopo l'inizializzazione.
     * Interagisce direttamente con la tabella FATTORINI.
     */
    private final FattorinoRepository fattorinoRepository;
    /**
     * Riferimento al Repository per la gestione della persistenza dell'entità Ordini.
     * Dichiarato {@code final} per garantire l'immutabilità del riferimento dopo l'inizializzazione.
     * Interagisce direttamente con la tabella ORDINI.
     */
    private final OrdineRepository ordineRepository;

    /**
     * Riferimento al Repository per la gestione della persistenza dell'entità Prodotto.
     * Dichiarato {@code final} per garantire l'immutabilità del riferimento dopo l'inizializzazione.
     * Interagisce direttamente con la tabella PRODOTTI.
     */
    private final ProdottoRepository prodottoRepository;

    /**
    * Costruttore con argomenti. 
    * Spring genera automaticamente in memoria una classe concreta che implementa le interfacce Repository.
    * Questa classe generata dal framework, chiamata Proxy, contiene l'effettivo codice SQL per
    * comunicare con il database. Una volta creata l'istanza di questa classe generata automaticamente, 
    * Spring la inserisce all'interno dell' IoC Container.
    * @param prodottoRepository          Riferimento al prodotto
    * @param fattorinoRepository         Riferimento al fattorino
    * @param clienteRegistratoRepository Riferimento al cliente registrato
    * @param ordineRepository            Riferimento all'ordine
     */
    public GestioneStore(ClienteRegistratoRepository clienteRegistratoRepository,
            FattorinoRepository fattorinoRepository, OrdineRepository ordineRepository,
            ProdottoRepository prodottoRepository) {
        this.clienteRegistratoRepository = clienteRegistratoRepository;
        this.fattorinoRepository = fattorinoRepository;
        this.ordineRepository = ordineRepository;
        this.prodottoRepository = prodottoRepository;
    }

    /**
     * Funzionalità di inclusione privata adibita al recupero del cliente registrato per l'autenticazione (UC13).
     * Restituisce un {@link Optional} delegando interamente al metodo chiamante la responsabilità di 
     * verificare la presenza del record tramite Optional.isEmpty().
     * @param nomeUtente nomeUtente fornito dalla Boundary
     * @param password   Password fornita dalla Boundary
     * @return Optional contenente l'entità se le credenziali coincidono, altrimenti un Optional vuoto.
     */
    private Optional<EntityClienteRegistrato> autenticazione(String nomeUtente, String password){
        return clienteRegistratoRepository.findByNomeUtenteAndPassword(nomeUtente, password);
    }

    /**
     * Funzionalità di selezione di un fattorino delegato alla consegna dell'ordine.
     * Viene utilizzato un approccio tale per cui la selezione del fattorino è fatta in modo randomico. Si è
     * scelto di realizzare una funzionalità separata al flusso principale di acquistaProdotto in quanto la 
     * selezione potrebbe essere implementata con algoritmi più o meno complessi.
     * @return Il fattorino individuato in modo casuale.
     * @throws RisorsaNonTrovataException In caso di assenza di fattorini nel Database.
     */
    private EntityFattorino selezionaFattorino() throws  RisorsaNonTrovataException{
        //Utilizziamo List in quanto restituita di defaul dal findAll()
        List<EntityFattorino> listaFattorini = fattorinoRepository.findAll();
        if(listaFattorini.isEmpty()){
            throw new RisorsaNonTrovataException("Nessun fattorino disponibile nel sistema!");
        }
        //Generazione di un indice casuale interno alla size della List
        Random random = new Random();
        int indiceCasuale = random.nextInt(listaFattorini.size());
        //Ritorno del fattorino
        return listaFattorini.get(indiceCasuale);
    }


    /**
     * Avvia la prima fase del caso d'uso "Acquista Prodotto" (UC07).
     * La struttura dell'algoritmo è la seguente: 
     * <p> 1. Autenticazione del Cliente Registrato. 
     * <p> 2. Recupero dal Database del Prodotto selezionato.
     * <p> 3. Controllo della quantità disponibile.
     * <p> 4. Calcolo del prezzo totale.
     * <p> 5. Selezione del Fattorino.
     * <p> 6. Costruzione e Salvataggio dell'ordine.
     * Viene restituito in Output alla Boundary un ArrayList. Ricordiamo che la libreria java.util offre
     * un insieme completo di classi contenitore, ovvero oggetti che raggruppano elementi multipli in una
     * singola unità. ArrayList implementa tramite List l'interfaccia Collection, realizzando una raccolta 
     * sequenziale di singoli elementi. ArrayList implementa l'interfaccia List, tale per cui può contenere
     * elementi duplicati.
     * @param nomeUtente         Username del cliente registrato
     * @param password           Password del cliente registrato
     * @param codiceProdotto     L'identificativo alfanumerico del prodotto selezionato
     * @param quantità           Il numero di unità desiderate
     * @return ArrayList contenente: [0] Prezzo Totale, [1] Carta di Credito, [2] Indirizzo di Spedizione
     * [3] L'ordine da confermare.
     * @throws AutenticazioneFallitaException In caso di credenziali errate.
     * @throws DisponibilitàEsauritaException In caso quantità richiesta eccedente quella disponibile.
     * @throws RisorsaNonTrovataException In caso di assenza di risorse cercate nel Database.
     * @throws DatabaseException In caso di errori di comunicazione con il Database.
     * Gli errori di persistenza sono intercettate per tradurle nell' eccezione di dominio DbException, 
     * cosi' da nascondere alla boundary i dettagli interni del livello di persistenza.
     */
    public ArrayList<String> acquistaProdotto(String nomeUtente, String password, String codiceProdotto,
        int quantità) throws AutenticazioneFallitaException, RisorsaNonTrovataException, 
            DisponibilitàEsauritaException, DatabaseException{
        
        ArrayList<String> returnList = new ArrayList<>();
        returnList.add("0.0"); //Costo totale dell'ordine
        returnList.add("null"); //Carta di credito del cliente registrato
        returnList.add("null"); //Indirizzo di spedizione del cliente registrato
        returnList.add("null"); //Id dell'ordine

        try {
            //Autenticazione del Cliente Registrato (UC13):
            Optional<EntityClienteRegistrato> clienteRegistratoOpt = autenticazione(nomeUtente, password);
            if(clienteRegistratoOpt.isEmpty()){
                throw new AutenticazioneFallitaException("Autenticazione fallita. Nome utente o password errati!");
            }
            EntityClienteRegistrato clienteRegistrato = clienteRegistratoOpt.get();

            //Recupero dal Database del Prodotto
            Optional<EntityProdotto> prodottoOpt = prodottoRepository.findById(codiceProdotto);
            if(prodottoOpt.isEmpty()){
                throw new RisorsaNonTrovataException("Prodotto non trovato nel catalogo!");
            }
            EntityProdotto prodotto = prodottoOpt.get();

            //Controllo della quantità disponibile
            if(prodotto.getQuantità()<quantità){
                throw new DisponibilitàEsauritaException("Quantità di prodotto non disponibile!");
            }

            //Calcolo del prezzo Totale
            float prezzoTotale = prodotto.getPrezzo()*quantità;

            //Selezione del fattorino scelto per la consegna dell'ordine
            EntityFattorino fattorino = selezionaFattorino();

            //Recupero e Set all'interno dell'ArrayList del prezzoToale, Indirizzo di spedizione e Carta di credito
            returnList.set(0, String.valueOf(prezzoTotale));
            returnList.set(1, clienteRegistrato.getCartaDiCredito());
            returnList.set(2, clienteRegistrato.getIndirizzo());

            //Costruzione dell'ordine
            EntityOrdine ordine = new EntityOrdine(LocalDate.now(), LocalTime.now(), prezzoTotale, quantità, 
                prodotto, fattorino, clienteRegistrato);
            //Salvataggio dell'ordine sul Database.
            ordine = ordineRepository.save(ordine);

            //Set all'interno dell'ArrayList dell'id dell'ordine appena creato.
            returnList.set(3, String.valueOf(ordine.getId()));
        } 
        catch (DataAccessResourceFailureException ex) {
            //Eccezzione specifica in caso di connessione al Database non disponibile.
            throw new DatabaseException("Connessione al Database non disponibile!");
        }
        catch(DataAccessException ex){
            // Catch-all delle altre eccezioni di persistenza Spring
            throw new DatabaseException("Ops, qualcosa e' andato storto...");
        }

        return returnList;
    }

    /**
     * Funzionaità privata che simula l'interfacciamento con il gateway di pagamento esterno (UC14).
     * Il metodo riceve i parametri finanziari necessari al saldo transazionale. L'algoritmo non contatta 
     * un circuito bancario reale ma restituisce sistematicamente {@code true} per simulare una transazione
     * andata a buon fine.
     * @param cartaDiCredito    Il codice identificativo della carta estratto dal profilo del cliente sul DB
     * @param costoTotale       Il costo dell'ordine da pagare.
     * @return true per simulare l'approvazione della transazione da parte del provider esterno.
     */
    private boolean pagamento(float costoTotale, String cartaDiCredito){
        // Log di tracciamento che simula la fase di contatto con il sistema di pagamento esterno.
        System.out.println("[GATEWAY ESTERNO] Inviata richiesta di addebito di €" + costoTotale + 
            " sulla carta: " + cartaDiCredito);
        // Il metodo restituisce programmaticamente sempre true, confermando l'esito del pagamento.
        return true;
    }

    /**
     * Funzionalità privata che simula l'invio della notifica di riepilogo al cliente (UC15).
     * L'algoritmo non contatta un sistema di messaggistisca esterno reale ma restituisce sistematicamente {@code true}
     * per simulare una notifica andata a buon fine.
     * @param telefono  Numero di telefono del cliente usato per notificarlo tramite il servizio di messaggistica esterno.
     * @param ordine    L'ordine memorizzato sul Database
     * @return true per simulare la corretta ricezione e consegna del messaggio.
     */
    private boolean notificaRiepilogo(String telefono, EntityOrdine ordine){
        System.out.println("[SERVIZIO MESSAGGISTICA] Notifica inviata al numero " 
                + telefono 
                + " | Testo: 'Grazie per aver acquistato su HairCareStore! Il tuo ordine ID " + ordine.getId()
                + "contenente il prodotto: " + ordine.getProdotto().getNome() + " (" + ordine.getQuantità() + "pz.)"
                + " per un totale di €" + ordine.getCostoTotale() + " è in preparazione'.");
        return true;
    }

    /**
     * Funzionalità privata che simula l'invio della notifica di di preparazione del pacco all'impiegato (UC16).
     * L'algoritmo non contatta un sistema di messaggistisca esterno reale ma restituisce sistematicamente {@code true}
     * per simulare una notifica andata a buon fine.
     * @param ordine    L'ordine memorizzato sul Database
     * @return true per simulare la corretta ricezione e consegna del messaggio.
     */
    private boolean notificaPreparazione(EntityOrdine ordine){
        System.out.println("[SERVIZIO MESSAGGISTICA] Notifica inviata alla postazione dell'impiegato "
                + " | Oggetto: Ordine ID " + ordine.getId() + " contenente il prodotto: " + 
                ordine.getProdotto().getNome() + " (code " + ordine.getProdotto().getCodice() + ")." 
                + " Numero di articoli: " + ordine.getQuantità() + ".");
        return true;
    }

    /**
     * Funzionalità privata che simula l'invio della notifica di ritiro merci al fattorino (UC17).
     * L'algoritmo non contatta un sistema di messaggistisca esterno reale ma restituisce sistematicamente {@code true}
     * per simulare una notifica andata a buon fine.
     * @param telefono  Numero di telefono del fattorino usato per notificarlo tramite il servizio di messaggistica esterno.
     * @param ordine    L'ordine memorizzato sul Database
     * @return true per simulare la corretta ricezione e consegna del messaggio.
     */
    private boolean notificaRitiro(String telefono, EntityOrdine ordine){
        System.out.println("[SERVIZIO MESSAGGISTICA] Notifica inviato al numero di telefono del Fattorino: " 
                + telefono
                +  " | Oggetto: 'Prelevare il pacco pronto relativo all'ordine ID " + ordine.getId() + "'."
                + " Da consegnare all'indirizzo: " + ordine.getClienteRegistrato().getIndirizzo());
        return true;
    }

    /**
     * Avvia la seconda fase del caso d'uso "Acquista Prodotto" (UC07), in seguito alla conferma dell'ordine.
     * La struttura dell'algoritmo è la seguente: 
     * <p> 1. Recupero dell'ordine dal Database.
     * <p> 2. Pagamento 
     * <p> 3. In caso di pagamento non andato a buon fine si procede all'annullamento dell'ordine.
     * <p> 4. Aggiornamento della quantità disponibile in magazzino
     * <p> 5. Notifica di riepilogo al cliente registrato
     * <p> 6. Notifica di preparazione all'impiegato
     * <p> 7. Modifica dello stato dell'ordine da In_corso a Confermato
     * <p> 8. Notifica di ritiro al fattorino
     * <p> 9. Modifica dello stato dell'ordine da Confermato a Spedito
     * In un'ottica di ottimizzazione della sicurezza dei dati, il metodo accetta esclusivamente l'identificativo 
     * dell'ordine. Il sistema recupera il record dal database e applica le variazioni logiche basandosi 
     * unicamente sullo stato nativo memorizzato sul DB, azzerando i rischi di Parameter Tampering dal frontend.
     * @param codiceOrdine     L'identificativo intero dell'ordine confermato dal cliente.
     * @throws RisorsaNonTrovataException In caso di assenza di risorse cercate nel Database.
     * @throws DatabaseException In caso di errori di comunicazione con il Database.
     */
    public boolean emettiOrdine(int codiceOrdine) throws RisorsaNonTrovataException, DatabaseException{

        try {
            //Recupero dell'ordine dal Database
            Optional<EntityOrdine> ordineOpt = ordineRepository.findById(codiceOrdine);
            if(ordineOpt.isEmpty()){
                throw new RisorsaNonTrovataException("Ordine non trovato!");
            }
            EntityOrdine ordine = ordineOpt.get();

            //Recupero dall'ordine il prezzo da pagare ed il Cliente Registrato con conseguente numero
            //di carta di credito
            float costoTotale = ordine.getCostoTotale();
            String cartaDiCredito = ordine.getClienteRegistrato().getCartaDiCredito();
            
            //Pagamento
            boolean confermaPagamento = pagamento(costoTotale, cartaDiCredito);
            //In caso di pagamento non andato a buon fine si procede all'annullamento dell'ordine
            if(!confermaPagamento){
                annullaOrdine(codiceOrdine);
                return false;
            }

            //Modifico la quantità di prodotti disponibili in quanto la transazione è andata a buon fine
            EntityProdotto prodotto = ordine.getProdotto();
            prodotto.setQuantità(prodotto.getQuantità()-ordine.getQuantità());
            //Salvataggio delle modifiche sul Database
            prodottoRepository.save(prodotto);

            //Notifica del riepilogo dell'ordine al cliente registrato
            String telefonoCliente = ordine.getClienteRegistrato().getTelefono();
            notificaRiepilogo(telefonoCliente, ordine);
            //Notifica all'impiegato della preparazione del pacco
            notificaPreparazione(ordine);

            //Modifica dello stato dell'ordine da In_corso a Confermato
            ordine.setStato(Stato.Confermato);
            //Salvataggio del nuovo stato dell'ordine sul Database in modo persistente
            ordineRepository.save(ordine);

            //Notifica del ritiro al fattorino delegato alla consegna dell'ordine
            String telefonoFattorino = ordine.getFattorino().getTelefono();
            notificaRitiro(telefonoFattorino, ordine);

            //Modifica dello stato dell'ordine da Confermato a Spedito
            ordine.setStato(Stato.Spedito);
            //Salvataggio del nuovo stato dell'ordine sul Database in modo persistente
            ordineRepository.save(ordine);

            //Restituisce l'esito positivo relativo all'emissione dell'ordine.
            return true;
        } 
        catch (DataAccessResourceFailureException ex) {
            //Eccezzione specifica in caso di connessione al Database non disponibile.
            throw new DatabaseException("Connessione al Database non disponibile!");
        }
        catch(DataAccessException ex){
            // Catch-all delle altre eccezioni di persistenza Spring
            throw new DatabaseException("Ops, qualcosa e' andato storto...");
        }
    }

    /**
     * Flusso alternativo associato al rifiuto o all'annullamento dell'acquisto da parte dell'utente.
     * Rimuove fisicamente il record dell'ordine dalla tabella ORDINI di MySQL.
     * @param codiceOrdine            L'ID dell'ordine da eliminare in formato String
     * @throws DatabaseException      In caso di errori di comunicazione con il Database.
     */
    public boolean annullaOrdine(int codiceOrdine) throws DatabaseException{

        try {
            //Cancellazione fisica dell'ordine dal database
            ordineRepository.deleteById(codiceOrdine);
            //Log di cancellazione
            System.out.println("[DB CLEANUP] Ordine ID " + codiceOrdine + " rimosso con successo dal Database.");
            //Viene restituito l'esito positivo di annullamento dell'ordine.
            return true;

        } 
        catch (DataAccessResourceFailureException ex) {
            //Eccezzione specifica in caso di connessione al Database non disponibile.
            throw new DatabaseException("Connessione al Database non disponibile!");
        }
        catch(DataAccessException ex){
            // Catch-all delle altre eccezioni di persistenza Spring
            throw new DatabaseException("Ops, qualcosa e' andato storto...");
        }
    }

    /**
     * Recupera l'elenco completo di tutti i prodotti presenti nel catalogo, mappando il caso d'uso visualizza
     * catalogo (UC05). Il metodo interroga il database delegando al metodo {@code findAll()} del Repository la
     * restituzione di una List con tutti i prodotti presenti nello schema. 
     * Eventuali anomalie di comunicazione con il DBMS vengono intercettate e incapsulate nella classe d'eccezione
     *  di dominio {@link DatabaseException}.
     * @return Una {@link List} contenente tutte le entità {@link EntityProdotto} memorizzate sul database.
     * @throws DatabaseException In caso di indisponibilità del Database MySQL.
     */
    public List<EntityProdotto> visualizzaCatalogo() throws DatabaseException{
        try {
            // Eseguo la query di selezione totale sulla tabella PRODOTTI
            return prodottoRepository.findAll();
        }
        catch (DataAccessResourceFailureException ex) {
            //Eccezzione specifica in caso di connessione al Database non disponibile.
            throw new DatabaseException("Connessione al Database non disponibile!");
        }
        catch(DataAccessException ex){
            // Catch-all delle altre eccezioni di persistenza Spring
            throw new DatabaseException("Ops, qualcosa e' andato storto...");
        }
    }

    /**
     * Recupera il singolo prodotto presente nel catalogo, mappando il caso d'uso visualizza catalogo (UC05). Verrà utilizzato
     * per visualizzare il dettaglio di ogni prodotto all'interno del catalogo.
     * Il metodo interroga il database delegando al metodo {@code findById()} del Repository la
     * restituzione di un Optional con il prodotto presente nello schema, se trovato. 
     * Eventuali anomalie di comunicazione con il DBMS vengono intercettate e incapsulate nella classe d'eccezione
     *  di dominio {@link DatabaseException}. Se la risorsa non è trovata viene lnciata l'eccezzione {@link RisorsaNonTrovataException}
     * @return Il prodotto memorizzato sul database.
     * @throws RisorsaNonTrovataException In caso di assenza di risorse cercate nel Database.
     * @throws DatabaseException In caso di indisponibilità del Database MySQL.
     */
    public EntityProdotto visualizzaProdotto(String id) throws DatabaseException, RisorsaNonTrovataException{
        try {
            // Eseguo la query di selezione sulla tabella PRODOTTI
            Optional<EntityProdotto> prodottoOPT = prodottoRepository.findById(id);
            //Se il prodotto esiste lancia un'eccezzione
            if(prodottoOPT.isEmpty()){
                throw new RisorsaNonTrovataException("Prodotto non trovato!");
            }
            EntityProdotto prodotto = prodottoOPT.get();
            return prodotto;
        }
        catch (DataAccessResourceFailureException ex) {
            //Eccezzione specifica in caso di connessione al Database non disponibile.
            throw new DatabaseException("Connessione al Database non disponibile!");
        }
        catch(DataAccessException ex){
            // Catch-all delle altre eccezioni di persistenza Spring
            throw new DatabaseException("Ops, qualcosa e' andato storto...");
        }
    }
}
