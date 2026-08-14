/**
 * - Component: trasforma una classe TypeScript in un componente web, agganciando HTML e CSS.
 * - Input: permette il passaggio unidirezionale di dati dal componente padre (prodotto.ts) a questo componente figlio.
 * - Output: consente al figlio di inviare notifiche asincrone verso il componente padre.
 * - EventEmitter: classe utilizzata in combinazione con Output per istanziare e lanciare eventi custom.
 * - Signal: aggiorna la pagina web solo quando il valore interno alla signal muta.
 */
import { Component, Input, Output, EventEmitter, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
/**
 * - FormsModule: consente di realizzare all'interno dell HTML il form per l'inserimento dei dati necessari ad 
 * avviare il caso d'uso di acquisto del prodotto.
 */
import { FormsModule } from '@angular/forms';
import { StoreService } from '../../services/hairCareStore.service';
import { ResponseAcquisto, RequestEmissione, RequestAcquisto, RequestAnnullamento } from '../../models/acquisto';

@Component({
  // Nome del tag HTML per istanziare la vista nel browser. Verrà utilizzato nell'HTML del component prodotto.
  selector: 'app-acquisto-banner',
  standalone: true,                       // Specifica che il componente è autonomo.
  imports: [CommonModule, FormsModule],
  templateUrl: './acquisto.html',         // Collegamento al file HTML.
  styleUrl: './acquisto.css'              // Collegamento al foglio di stile CSS.
})
export class AcquistoBannerComponent {
  
  /**
   * Input: codice del prodotto da acquistare ricevuto dal component prodotto.
   */
  @Input({ required: true }) codiceProdotto!: string;
  
  /**
   * Istanzio un generatore di eventi asincroni. Quando invocato tramite il metodo emit(), 
   * invia un segnale di interruzione al padre (prodotto.ts), spingendolo ad eseguire la funzione 
   * di chiusura grafica `(chiudi)="chiudiBannerAcquisto()"` impostata nell' HTML.
   */
  @Output() chiudi = new EventEmitter<void>();

  /**
   * fase: modella lo stato logico del Caso d'Uso. L'HTML reagisce leggendo questo Signal tramite le direttive 
   * *ngIf="fase() === 1", *ngIf="fase() === 2", scambiando le diverse schermate. 
   * Inizializzato al valore 1 per consentire l'esecuzione immediata della prima fase. Le fasi sono le seguenti:
   * Fase 1: Inserimento Nome Utente, Password e Quantità.
   * Fase 2: Mostra il riepilogo (Costo, Carta, Indirizzo) con il tasto di Conferma.
   * Fase 3: Schermata di successo ad operazione conclusa.
   */
  fase = signal<number>(1);
  
  /**
   * I tre Signals sottostanti contengono lo stato dei campi di input della prima fase.
  */
  nomeUtente = signal<string>('');
  password = signal<string>('');
  quantita = signal<number>(1);

  /**
   * riepilogo: è tipizzato sul DTO ResponseAcquisto e verrà sovrascritto con l'oggetto JSON inviato da Spring Boot
   * in caso di successo della prima fase.
   */
  riepilogo = signal<ResponseAcquisto | null>(null);

  /**
   * Flag booleano per controllare la visualizzazione del messaggio di caricamento di rete.
   */
  loading = signal<boolean>(false);
  /**
   * Contiene la stringa di errore da stampare a schermo in caso di anomalie HTTP.
   */
  errorMsg = signal<string>('');
  /**
   * Contiene la stringa di successo da stampare a schermo alla conclusione della terza fase del caso d'uso.
   */
  successMsg = signal<string>('');

  constructor(private storeService: StoreService) {}

  /**
   * Prima fase: Avvio del processo di acquisto
   * Invia le credenziali e le specifiche dell'ordine alla boundary di Spring Boot.
   * Controllo già dal front-end che tutti i campi siano effettivamente pieni e che la quantità richiesta sia positiva.
   */
  avviaAcquisto(): void {
    if (!this.nomeUtente() || !this.password() || this.quantita() <= 0) {
      this.errorMsg.set('Compila tutti i campi inserendo una quantità valida.');
      return; //Interrompo l'esecuzione del metodo se rilevo un errore.
    }

    this.loading.set(true);   //l'HTML mostra la schermata di attesa, attendendo la risposta del back-end.
    this.errorMsg.set('');    //Ripristino di sicurezza della stringa di errore.

    //Costruzione del DTO di richiesta da spedire al back-end tramite JSON.
    const payloadAcquisto : RequestAcquisto = {
      nomeUtente: this.nomeUtente(),
      password: this.password(),
      codiceProdotto: this.codiceProdotto,
      quantita: this.quantita()
    };

    // Innesca la richiesta POST verso l'endpoint (/api/store/acquisto)
    this.storeService.acquistaProdotto(payloadAcquisto).subscribe({
      //Se il back-end risponde con Status Code 200 OK:
      next: (rispostaBackend) => {
        // Salviamo il DTO ResponseAcquistoDTO che contiene il codiceOrdine
        this.riepilogo.set(rispostaBackend);
        this.loading.set(false);  //Disattivo la schermata di caricamento
        this.fase.set(2);         //Set della fase a 2 consentendo all'HTML di cambiare schermata
      },
      // Se il back-end restituisce uno status code di errore:
      error: (err) => {
        this.loading.set(false);        //Disattivo la schermata di caricamento
          // Estraggo il messaggio personalizzato generato dal blocco catch di Spring Boot.
          // Per sicurezza se ricevo una risposta HTTP con un status code di errore senza alcun messaggio specifico
          // stampo la stringa 'Errore durante la richiesta di acquisto.'
        this.errorMsg.set(err.error?.message || 'Errore durante la richiesta di acquisto.');
      }
    });
  }

  /**
   * Seconda fase: finalizzazione ed emissione dell'Ordine
   * Invia il codice numerico generato dal database MySQL per completare la transazione.
   */
  confermaEmissione(): void {
    const datiRiepilogo = this.riepilogo();   //Recupero dei dati di riepilogo dal DTO.
    if (!datiRiepilogo) return; // Controllo di consistenza: impedisce l'esecuzione se l'oggetto di riepilogo è vuoto.

    this.loading.set(true);   
    this.errorMsg.set('');

    // Costruiamo il DTO RequestEmissioneDTO contenente il codice identificativo dell'ordine
    const payloadEmissione: RequestEmissione = {
      codiceOrdine: datiRiepilogo.codiceOrdine
    };

    // Innesca la richiesta POST mappata sull'endpoint (/api/store/emissione)
    this.storeService.emettiOrdine(payloadEmissione).subscribe({
      //Se il back-end risponde con Status Code 200 OK:
      next: (risposta) => {
        this.loading.set(false);                  //Disattivo la schermata di caricamento
        this.successMsg.set(risposta.message);    // Catturo il messaggio di successo inviato dal server
        this.fase.set(3);                         // Set della fase a 3 consentendo all'HTML di cambiare schermata
      },
      // Se il back-end restituisce uno status code di errore:
      error: (err) => {
        this.loading.set(false);         //Disattivo la schermata di caricamento
        // Estraggo il messaggio personalizzato generato dal blocco catch di Spring Boot.
        // Per sicurezza se ricevo una risposta HTTP con un status code di errore senza alcun messaggio specifico
        // stampo la stringa 'Errore durante l'emissione finale dell'ordine.'
        this.errorMsg.set(err.error?.message || 'Errore durante l\'emissione finale dell\'ordine.');
      }
    });
  }

  /**
   * Nell'ipotesi in cui mi trovo nella seconda fase, dunque il back-end ha salvato l'ordine sul Database
   * con stato in_corso, e l'utente non conferma l'emissione dell'acquisto, chiamo il metodo di annullaOrdine()
   * sul back-end, eliminando in modo permanente l'ordine dal database.
   */
  annullaOperazione(): void {
    const datiRiepilogo = this.riepilogo();

    // Se l'ordine temporaneo è stato registrato e l'utente decide di abbandonare durante la seconda fase
    if (datiRiepilogo && datiRiepilogo.codiceOrdine && this.fase() === 2) {
      
      const payloadAnnullamento: RequestAnnullamento = {
        codiceOrdine: datiRiepilogo.codiceOrdine
      };

      
      this.loading.set(true);
      
      // Chiamata al servizio annullaOrdine passando il numero dell'ordine, che verrà inviato come DTO
      this.storeService.annullaOrdine(payloadAnnullamento).subscribe({
        next: (res) => {
          this.loading.set(false);
          this.chiudi.emit(); // Richiede la chiusura della grafica al component prodotto
        },
        // Se il back-end restituisce uno status code di errore:
        error: (err) => {
          this.loading.set(false);
          // Estraggo il messaggio personalizzato generato dal blocco catch di Spring Boot.
          // Per sicurezza se ricevo una risposta HTTP con un status code di errore senza alcun messaggio specifico
          // stampo la stringa 'Errore durante la cancellazione dell'ordine.'
          this.errorMsg.set(err.error?.message || 'Errore durante la cancellazione dell\'ordine.');
          this.chiudi.emit();   // Richiede la chiusura della grafica al component prodotto
        }
      });

    }
    else {
      // Se l'abbandono avviene in fase 1 o dopo la conclusione formale della fase 3, chiudo solo la grafica
      this.chiudi.emit();
    }
  }
}