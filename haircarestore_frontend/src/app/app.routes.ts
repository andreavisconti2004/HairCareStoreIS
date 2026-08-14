/**
 * 'Routes' è il tipo di dato formale (un'interfaccia) fornito dal modulo di routing nativo di Angular.
 * Serve a tipizzare l'array delle rotte, garantendo che ogni oggetto inserito segua tassativamente
 * i vincoli sintattici previsti dal framework.
 */
import { Routes } from '@angular/router';
/**
 * Importiamo il componente della lista prodotti. Il router ha bisogno di conoscere fisicamente 
 * la classe del componente per poterlo istanziare e iniettare nel tag <router-outlet> 
 * quando l'utente naviga su uno specifico URL.
 */
import { ProdottoListComponent } from './components/prodotto-list/prodotto-list';
/**
 * Importiamo il componente del singolo prodotto. Il router ha bisogno di conoscere fisicamente 
 * la classe del componente per poterlo istanziare e iniettare nel tag <router-outlet> 
 * quando l'utente naviga su uno specifico URL.
 */
import { ProdottoComponent } from './components/prodotto/prodotto';

/**
   * Configurazione centrale del modulo di navigazione.
   * Esportiamo ('export') la costante 'routes' in modo che possa essere letta e registrata 
   * all'avvio dell'applicazione all'interno del file di configurazione globale (app.config.ts).
   */
export const routes: Routes = [
    /**
     * Prima rotta: 
     * - 'path': Definisce il segmento di URL sul browser. Quando l'utente digita 'http://localhost:4200/prodotti', il router si attiva.
     * - 'component': Specifica quale componente attivare. In questo caso, carica la griglia con i prodotti estratti dal backend
     *  Spring Boot tramite Hibernate, visualizzando il catalogo dello Store.
     */
    { path: 'prodotti', component: ProdottoListComponent },

    /**
     * Reindirizzamento della pagina principale:
     * Gestisce lo stato in cui l'utente digita semplicemente l'indirizzo base del sito (es. 'http://localhost:4200/').
     * - 'path: "" ': Intercetta la stringa vuota dell'URL radice.
     * - 'redirectTo': Forza il router a saltare immediatamente sulla rotta '/prodotti', mostrando subito il catalogo.
     * - 'pathMatch: "full"': È un vincolo algoritmico fondamentale. Dice ad Angular di attivare questo redirect 
     *    solo se l'URL è totalmente vuoto. 
     */
    { path: '', redirectTo: 'prodotti', pathMatch: 'full' },

    /**
     *  Seconda rotta: 
     * - 'path': Definisce il segmento di URL sul browser. Quando l'utente digita 'http://localhost:4200/prodotti/codiceProdotto', il router si attiva.
     * - 'component': Specifica quale componente attivare. In questo caso, carica il prodotto estratto dal backend Spring Boot
     * tramite Hibernate.
     */
    { path: 'prodotti/:codice', component: ProdottoComponent }
];