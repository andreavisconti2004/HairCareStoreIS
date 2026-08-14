/**
 * - Component: trasforma una classe TypeScript in un componente web, agganciando HTML e CSS.
 * - OnInit: Per l'inizializzazione del component.
 * - Signal: aggiorna la pagina web solo quando il valore interno alla signal muta.
 */
import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
/**
 * - ActivatedRoute: Consente di ispezionare l'URL del browser per estrarre variabili dinamiche (il codice).
 * - Router: Fornisce le API per reindirizzare l'utente su altre pagine (tornare alla lista).
 */
import { ActivatedRoute, Router } from '@angular/router';
import { StoreService } from '../../services/hairCareStore.service';
import { Prodotto } from '../../models/prodotto';
import { AcquistoBannerComponent } from '../acquisto/acquisto';

@Component({
  selector: 'app-prodotto', // Nome del tag HTML per istanziare la vista nel browser.
  standalone: true,         // Specifica che il componente è autonomo.
  //AcquistoBannerComponent consente nel file HTML di poter chiamare la costruzione del component acquisto nel momento
  //in cui viene eseguito il metodo apriBannerAcquisto{}.
  imports: [CommonModule, AcquistoBannerComponent],
  templateUrl: './prodotto.html', // Collegamento al file HTML.
  styleUrl: './prodotto.css'      // Collegamento al foglio di stile CSS.
})
export class ProdottoComponent implements OnInit {
  
  /**
   * prodotto ospita l'oggetto con le informazioni complete estratte dal Database.
   * Essendo un Signal, quando cambia il suo valore tramite il metodo set(), Angular aggiorna istantaneamente
   * solo i punti dell'HTML che lo leggono.
   */
  prodotto = signal<Prodotto | null>(null);
  /**
   * Flag booleano per controllare la visualizzazione del messaggio di caricamento di rete.
   */
  loading = signal<boolean>(true);
  /**
   * Contiene la stringa di errore da stampare a schermo in caso di anomalie HTTP.
   */
  errorMsg = signal<string>('');
  /**
   * mostraBannerAcquisto controlla se mostrare o nascondere la schermata d'acquisto.
   */
  mostraBannerAcquisto = signal<boolean>(false);

  constructor(
    private route: ActivatedRoute,        // Meccanismo per estrarre la Path Variable dall'URL attivo.
    private storeService: StoreService,   // Servizio per effettuare chiamate HTTP.
    private router: Router                // Motore di navigazione tra le rotte dell'applicazione definite nel routes.ts
  ) {}

  /**
   * ngOnInit: viene eseguito in automatico dopo che il component è stato agganciato alla pagina browser.
   */
  ngOnInit(): void {
    // Estrae il parametro configurato come ':codice' nell'URL
    const codice = this.route.snapshot.paramMap.get('codice');
    
    if (codice) {
      // Innesca la chiamata HTTP GET verso l'endpoint (/api/store/catalogo/{codice})
      this.storeService.getProdottoByCodice(codice).subscribe({
        //Se il back-end risponde con Status Code 200 OK:
        next: (prodottoBackend) => {
          this.prodotto.set(prodottoBackend);       // Sovrascrivo il Signal con l'oggetto JSON ricevuto.
          this.loading.set(false);                  // Disattivo lo stato di caricamento.
        },
        // Se il back-end restituisce uno status code di errore:
        error: (err) => {
          // Estraggo il messaggio personalizzato generato dal blocco catch di Spring Boot.
          // Per sicurezza se ricevo una risposta HTTP con un status code di errore senza alcun messaggio specifico
          // stampo la stringa 'Impossibile caricare i dettagli del prodotto'
          this.errorMsg.set(err.error?.message || 'Impossibile caricare i dettagli del prodotto');
          this.loading.set(false);    // Nascondo lo stato di caricamento.
        }
      });
    } else {
      //Se nel caso il codice non viene prelevato dal Path dell'URL definisco l'errore nel corrispettivo signal
      //e disattivo lo stato di caricamento.
      this.errorMsg.set('Codice prodotto non rilevato nell\'URL');
      this.loading.set(false);
    }
  }

  /**
   * getImmagineProdotto: Mappa la stringa del codice univoco del Database al rispettivo file grafico PNG
   * posizionato all'interno della cartella public. Nel caso in cui non è stata fatta la mappatura di un prodotto
   * all'intenro del dizionario chiave:valore viene restituito una copertina di default.
   */
  getImmagineProdotto(codice: string): string {
    const mappaImmagine: { [key: string]: string } = {
      'KER-SHAM250-DRY': '/Shampoo.png',
      'MSK-REPAIR300-C': '/Maschera.png',
      'OIL-ARGAN100-ST': '/Olio.png'
    };
    return mappaImmagine[codice] || '/Shampoo.png';
  }

  /**
   * Permette all'utente di ritornare alla visualizzazione del catalogo principale
   */
  tornaAlCatalogo(): void {
    this.router.navigate(['/prodotti']);
  }

  /** apriBannerAcquisto e  chiudiBannerAcquisto sono innescati dai click dell'HTML emutano lo stato del 
   * Signal, controllando la comparsa del banner di acquisto */
  apriBannerAcquisto(): void {
    this.mostraBannerAcquisto.set(true);
  }

  chiudiBannerAcquisto(): void {
    this.mostraBannerAcquisto.set(false);
  }
}