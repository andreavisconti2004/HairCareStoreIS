package Boundary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Control.GestioneStore;
import DTO.ProdottoDTO;
import DTO.RequestAcquistoDTO;
import DTO.RequestAnnullamentoDTO;
import DTO.RequestEmissioneDTO;
import DTO.ResponseAcquistoDTO;
import Entity.EntityProdotto;
import Exception.AutenticazioneFallitaException;
import Exception.DatabaseException;
import Exception.DisponibilitàEsauritaException;
import Exception.RisorsaNonTrovataException;
import jakarta.validation.Valid;

/**
 * Componente dello strato Boundary dell'architettura BCED, deputato all'esposizione dei servizi REST 
 * e alla gestione esclusiva del protocollo di comunicazione HTTP.
 * Questa classe rappresenta l'unico punto di ingresso del backend che comunica direttamente con il Front-End
 * Angular. Riceve i payload JSON transanti sulla rete, ne delega la validazione sintattica al framework 
 * tramite l'annotazione {@code @Valid} e mappa i dati nei Data Transfer Object preposti, garantendo l'isolamento
 * della business logic dalle specifiche del protocollo di trasporto.
 * La registrazione dei componenti all'interno del container IoC avviene in modalità dichiarativa 
 * sfruttando gli stereotipi standard forniti dal framework. Per poter registrare lo strato Boundary
 * al container e dichiararla esplicitamente un Bean, utilizziamo l'annotazione:
 * <p> {@code @RestController}: Combina {@code @Controller} e {@code @ResponseBody}, indicando al container 
 * IoC che ogni metodo restituisce i dati direttamente nel corpo della risposta HTTP serializzati in formato 
 * JSON da Jackson.
 * <p> Inoltre utilizziamo le seguenti annotazioni: 
 * <p> {@code @CrossOrigin(origins = "*")}: Abilita le policy CORS (Cross-Origin Resource Sharing), consentendo
 * le chiamate asincrone provenienti dall'applicazione Angular residente su un porto differente.
 * <p> {@code @RequestMapping("/api/store")}: Definisce la radice uniforme degli URL per tutti gli endpoint
 * esposti dalla Boundary.
 */

@CrossOrigin(origins="*")
@RestController
@RequestMapping("/api/store")
public class BClienteRegistrato {

    /**
     * Riferimento immutabile allo strato Control delegato alla logica di business.
     * La dipendenza viene iniettata a runtime tramite Constructor Injection da Spring.
     */
    private final GestioneStore gestioneStore;

    /**
     * Costruttore unico adibito alla Constructor Dependency Injection dello strato Control.
     * @param gestioneStore Il Bean dello strato Control da accoppiare alla Boundary
     */
    public BClienteRegistrato(GestioneStore gestioneStore) {
        this.gestioneStore = gestioneStore;
    }

    /**
     * Endpoint HTTP POST deputato all'avvio della prima fase del caso d'uso "Acquista Prodotto" (UC07).
     * Il metodo intercetta il payload JSON inviato dal client, attiva il modulo di validazione 
     * (Jakarta Bean Validation) per intercettare gli input malformati prima che impegnino la Control.
     * Qualora il framework rilevi una violazione dei vincoli dichiarati nel DTO, l'esecuzione non viene
     * interrotta dall'eccezione nativa globale {@code MethodArgumentNotValidException}. Al contrario, 
     * l'anomalia viene registrata nel registro degli errori di {@code BindingResult}. 
     * Il metodo estrae programmaticamente il primo {@link FieldError} riscontrato e ne invoca il metodo 
     * {@code getDefaultMessage()} per estrarre l'esatta stringa personalizzata definita nell'attributo 
     * {@code message} delle annotazioni di {@link RequestAcquistoDTO}.
     * Una volta che gli input sono validati, viene chiamato il metodo della Control {@code acquistaProdotto}.
     * L'{@code ArrayList<String>} posizionale restituito viene convertito in un oggetto di risposta 
     * fortemente tipato {@link ResponseAcquistoDTO}.
     * @param request Oggetto DTO contenente le credenziali, la quantità ed il prodotto validati alla Boundary
     * @param validationResult
     * @return Una {@link ResponseEntity} contenente il {@link ResponseAcquistoDTO} con stato HTTP 200 OK 
     * in caso di successo, oppure un JSON contenente il messaggio nativo dell'eccezione di dominio con stato:
     * <p> HTTP 401 UNAUTHORIZED in caso di autenticazione fallita 
     * <p> HTTP 404 NOT_FOUND in caso di risorsa non trovata 
     * <p> HTTP 409 CONFLICT in caso di disponibilità esaurita 
     * <p> HTTP 500 INTERNAL_SERVER_ERROR in caso di errore di comunicazione con il database
     */
    @PostMapping("/acquisto")
    public ResponseEntity<?> acquistaProdotto(@Valid @RequestBody RequestAcquistoDTO request, 
        BindingResult validationResult){
        
        // Controllo esplicito dei vincoli del DTO basato su BindingResult
        if(validationResult.hasErrors()){
            // Estrazione del primo errore specifico che ha violato la validazione formale
            FieldError fieldError = validationResult.getFieldError();
            // Recupero della stringa personalizzata memorizzata nell'attributo 'message'
            String messaggioDiErrore = fieldError.getDefaultMessage();
            // Ritorno del messaggio di errore con stato HTTP 400 Bad Request
            return ResponseEntity.badRequest().body(Map.of("message", messaggioDiErrore));
        }

        try {
            //Chiamata dello Strato Control per l'esecuzione del metodo di acquistaProdotto() per l'esecuzione
            //della logica di Business
            ArrayList<String> result = gestioneStore.acquistaProdotto(request.getNomeUtente(), 
                request.getPassword(), request.getCodiceProdotto(), request.getQuantita());

            // Estrazione posizionale dei dati dall'ArrayList emesso dalla Control
            String costoTotale = result.get(0);
            String cartaDiCredito = result.get(1);
            String indirizzo = result.get(2);
            int codiceOrdine = Integer.parseInt(result.get(3));

            // Costruzione del DTO di risposta tipato speculare al modello del Front-End Angular
            ResponseAcquistoDTO response = new ResponseAcquistoDTO(costoTotale, cartaDiCredito, indirizzo, codiceOrdine);
            //Restituisce un ResponseEntity con status code HTTP 200 OK
            return ResponseEntity.ok(response);
        } 
        catch (AutenticazioneFallitaException ex) {
            //Catturata l'eccezzione AutenticazioneFallita con Status Code HTTP 401 UNAUTHORIZED
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", ex.getMessage()));
        }
        catch(RisorsaNonTrovataException ex){
            //Catturata l'eccezzione RisorsaNonTrovata con Status Code HTTP 404 NOT_FOUND
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
        }
        catch(DisponibilitàEsauritaException ex){
            //Catturata l'eccezzione DisponibilitàEsaurita con Status Code HTTP 409 CONFLICT
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", ex.getMessage()));
        }
        catch(DatabaseException ex){
            //Catturata l'eccezzione DatabaseException con Status Code HTTP 500 INTERNAL_SERVER_ERROR
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", ex.getMessage()));
        }
    }

    /**
     * Endpoint HTTP PUT preposto alla seconda fase del caso d'uso "Acquista Prodotto" (UC07).
     * In totale aderenza con i requisiti di progetto, questo endpoint riceve il payload di ingresso 
     * mappato nel DTO {@code RequestEmissioneDTO}. A differenza della fase di avvio dell'acquisto, 
     * in questo metodo non viene applicata alcuna logica di validazione formale o sintattica.
     * Tale scelta risponde a un preciso criterio di progettazione: l'identificativo dell'ordine 
     * non è un input diretto dell'utente (soggetto a errori di battitura o omissioni), bensì un dato 
     * generato nativamente dal Database MySQL al termine della prima fase. Angular si limita a 
     * conservarlo e a rispedirlo al backend in modalità invisibile per l'utente.
     * Il metodo estrarrà l'{@code int} nativo del codice ordine e invocherà il metodo {@code emettiOrdine} 
     * della Control. L'esito dell'operazione viene gestito controllando il valore booleano di ritorno 
     * o intercettando le eccezioni semantiche sollevate dal livello inferiore:
     * <p> Se la Control restituisce {@code true}, il pagamento è stato approvato dal gateway simulato e lo stato 
     * dell'ordine è avanzato in modo persistente fino a 'Spedito'. Viene restituito un codice HTTP 200 OK. 
     * <p> Se la Control restituisce {@code false}, significa che la simulazione del pagamento ha dato esito negativo, 
     * innescando automaticamente il sotto-flusso di annullamento e rimozione del record transitorio dal DB. 
     * Viene restituito un codice HTTP 400 Bad Request.
     * @param request Il Data Transfer Object contenente esclusivamente la chiave primaria numerica dell'ordine.
     * @return Una {@link ResponseEntity} contenente una mappa JSON {@code Map.of("message", ...)} con il 
     * feedback testuale dell'esito dell'operazione, associata al codice di stato HTTP corrispondente.
     */
    @PutMapping("/emissione")
    public ResponseEntity<?> emettiOrdine(@RequestBody RequestEmissioneDTO request){
        try {
            // Estrazione dell'ID numerico dal DTO ed esecuzione della logica di business nella Control
            boolean esitoEmissione = gestioneStore.emettiOrdine(request.getCodiceOrdine());

            if(esitoEmissione){
                // Caso standard: transazione approvata, magazzino scaricato e notifiche inviate
                return ResponseEntity.ok(Map.of("message", "Acquisto effettuato con successo!"));
            }
            else{
                // Caso di fallimento della transazione sul servizio di pagamento. La Control ha già ripulito il DB
                return ResponseEntity.badRequest().body(Map.of("message",
                    "Transazione di pagamento respinta dal servizio di pagamento. Ordine annullato."));
            }
        } 
        catch (RisorsaNonTrovataException ex) {
            //Catturata l'eccezzione RisorsaNonTrovata con Status Code HTTP 404 NOT_FOUND
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
        }
        catch(DatabaseException ex){
            //Catturata l'eccezzione DatabaseException con Status Code HTTP 500 INTERNAL_SERVER_ERROR
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", ex.getMessage()));
        }
    }

    /**
     * Endpoint HTTP GET preposto all'esposizione dell'intero catalogo dello store. In aderenza alle
     * specifiche RESTful, l'operazione di sola lettura totale viene associata al verbo HTTP {@code GET}.
     * Lo strato Boundary riceve la collezione di entità di dominio dalla Control, ne esegue il 
     * mappaggio atomico all'interno di una lista di {@link ProdottoDTO} e restituisce il vettore, 
     * ottenendo il contratto dei dati verso il client Angular.
     * @return Una {@link ResponseEntity} contenente la {@link List} dei prodotti configurati in formato DTO 
     * con codice di stato HTTP 200 OK; in alternativa, una mappa anonima JSON contenente la notifica 
     * dell'errore con codice HTTP 500 Internal Server Error.
     */
    @GetMapping("/catalogo")
    public ResponseEntity<?> visualizzaCatalogo(){
        try {
            // Interrogazione dello strato Control per il recupero dei dati di dominio
            List<EntityProdotto> prodotti = gestioneStore.visualizzaCatalogo();

            // Allocazione della lista destinata ad accogliere i DTO di risposta
            ArrayList<ProdottoDTO> catalogo = new ArrayList<>();

            //Inserimento nel catalogo di oggetti di tipo ProdottoDTO
            for(EntityProdotto p:prodotti){
                catalogo.add(new ProdottoDTO(p.getCodice(), p.getNome(), p.getDescrizione(), 
                    p.getCategoria(), p.getPrezzo(), p.getQuantità()));
            }

            //Restituzione del catalogo al Front-End
            return ResponseEntity.ok(catalogo);
        }
        catch(DatabaseException ex){
            //Catturata l'eccezzione DatabaseException con Status Code HTTP 500 INTERNAL_SERVER_ERROR
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", ex.getMessage()));
        }
    }

    /**
     * Endpoint HTTP GET preposto all'esposizione del singolo prodotto dello store. In aderenza alle
     * specifiche RESTful, l'operazione di sola lettura totale viene associata al verbo HTTP {@code GET}.
     * Lo strato Boundary riceve l'entità di dominio dalla Control, e restituisce il prodotto mappato come DTO, 
     * ottenendo il contratto dei dati verso il client Angular.
     * @param codice
     * @return Un {@link ResponseEntity} contenente il {@link ProdottoDTO} con codice di stato HTTP 200 OK; 
     * in alternativa, una mappa anonima JSON contenente la notifica dell'errore con codice HTTP 500 Internal Server Error,
     * 400 Bad Request oppure 404 NOT_FOUND.
     */
    @GetMapping("/catalogo/{codice}")
    public ResponseEntity<?> visualizzaProdotto(@PathVariable String codice){
        // 1. Validazione formale dell'input
        if (codice == null || codice.trim().isEmpty()) {
            // Se l'input è malformato, restituisce un 400 Bad Request
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            // Interrogazione dello strato Control per il recupero dei dati di dominio
            EntityProdotto prodotto = gestioneStore.visualizzaProdotto(codice);

            //Creazione di un ProdottoDTO da restituire al Front-end
            ProdottoDTO prodottoDTO = new ProdottoDTO(prodotto.getCodice(), prodotto.getNome(), prodotto.getDescrizione(),
                prodotto.getCategoria(), prodotto.getPrezzo(), prodotto.getQuantità());

            //Restituzione del prodotto al Front-End
            return ResponseEntity.ok(prodottoDTO);
        }
        catch (RisorsaNonTrovataException ex) {
            //Catturata l'eccezzione RisorsaNonTrovata con Status Code HTTP 404 NOT_FOUND
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
        }
        catch(DatabaseException ex){
            //Catturata l'eccezzione DatabaseException con Status Code HTTP 500 INTERNAL_SERVER_ERROR
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", ex.getMessage()));
        }
    }

    /**
     * Endpoint HTTP DELETE preposto all'annullamento di un acquisto in corso tra la prima e seconda fase
     * del caso d'uso acquistaProdotto().
     * In totale aderenza con i requisiti di progetto, questo endpoint riceve il payload di ingresso 
     * mappato nel DTO {@code RequestAnnullamentoDTO}.
     * @param request
     * @return Un {@link ResponseEntity} contenente l'esito dell'operazione di annullamento. In caso di esito positivo
     * restituisce il codice di stato HTTP 200 OK; 
     * in alternativa, una mappa anonima JSON contenente la notifica dell'errore con codice HTTP 500 Internal Server Error.
     */
    @DeleteMapping("/annulla")
    public ResponseEntity<?> annullaOrdine(@RequestBody RequestAnnullamentoDTO request) {
        try {
            // Chiamata del metodo di annullaOrdine() della Control
            gestioneStore.annullaOrdine(request.getCodiceOrdine());
            //Restituzione del messaggio di risposta al Front-end
            return ResponseEntity.ok(Map.of("message", "Ordine temporaneo annullato e rimosso con successo dal Database."));
        } 
        catch(DatabaseException ex){
            //Catturata l'eccezzione DatabaseException con Status Code HTTP 500 INTERNAL_SERVER_ERROR
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", ex.getMessage()));
        }
    }
}
