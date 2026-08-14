/**
 * - Component: trasforma una classe TypeScript in un componente web, agganciando HTML e CSS.
 * - OnInit: Per l'inizializzazione del component.
 * - Signal: aggiorna la pagina web solo quando il valore interno alla signal muta.
 */
import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
/**
 * - Router: Fornisce le API per reindirizzare l'utente su altre pagine (procedere alla schermata di dettaglio del prodotto).
 */
import { Router } from '@angular/router';

import { StoreService } from '../../services/hairCareStore.service';
import { Prodotto } from '../../models/prodotto';

@Component({
  selector: 'app-prodotto-list',    // Nome del tag HTML per istanziare la vista nel browser.
  standalone: true,                 // Specifica che il componente è autonomo.
  imports: [CommonModule], 
  templateUrl: './prodotto-list.html', // Collegamento al file HTML.
  styleUrl: './prodotto-list.css'      // Collegamento al foglio di stile CSS.
})
export class ProdottoListComponent implements OnInit {
  
  /**
   * 'prodotti' è un Signal che incapsula un array di oggetti di tipo 'Prodotto'.
   * Inizializzato come array vuoto ([]). Conterrà la lista reale estratta dal database.
   */
  prodotti = signal<Prodotto[]>([]);
  /**
   * loading: flag booleano per controllare la visualizzazione del messaggio di caricamento di rete.
   */
  loading = signal<boolean>(true);
  /**
   * errorMsg: contiene la stringa di errore da stampare a schermo in caso di anomalie HTTP.
   */
  errorMsg = signal<string>('');


  constructor(private storeService: StoreService, private router: Router) {}

  /**
   * ngOnInit: viene eseguito in automatico dopo che il component è stato agganciato alla pagina browser.
   */
  ngOnInit(): void {
    // Innesca la chiamata HTTP GET dverso l'endpoint (/api/store/catalogo)
    this.storeService.getProdotti().subscribe({
      //Se il back-end risponde con Status Code 200 OK:
      next: (rispostaBackend) => {
        this.prodotti.set(rispostaBackend);   // Set del json ricevuto dal back-end nell'array di dto di prodotto
        this.loading.set(false);              // Disattivo lo stato di caricamento.
      },
      // Se il back-end restituisce uno status code di errore:
      error: (err) => {
        // Estraggo il messaggio personalizzato generato dal blocco catch di Spring Boot.
        // Per sicurezza se ricevo una risposta HTTP con un status code di errore senza alcun messaggio specifico
        // stampo la stringa 'Server non raggiungibile'
        this.errorMsg.set(err.error?.message || 'Server non raggiungibile');
        this.loading.set(false);
      }
    });
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
   * Navigazione verso la schermata di dettaglio di ciascun prodotto
   */
  navigaVersoDettaglio(codiceProdotto: string): void {
    this.router.navigate(['/prodotti', codiceProdotto]);
  }
}