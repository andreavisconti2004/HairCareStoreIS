/**
 * 'Injectable' è il decoratore fondamentale che permette a questa classe di essere gestita  dal motore di 
 * Dependency Injection (DI) di Angular.
 */
import { Injectable } from '@angular/core';
/**
 * 'HttpClient' è il servizio nativo di Angular utilizzato per effettuare richieste HTTP (GET, POST, PUT, DELETE)
 * verso un server remoto (il backend Spring Boot).
 */
import { HttpClient } from '@angular/common/http';
/**
 * 'Observable' è una classe della libreria RxJS (Reactive Extensions for JavaScript). Rappresenta un flusso
 * di dati asincrono. Poiché le risposte della rete non sono istantanee, un Observable funge da canale a cui il frontend
 * si iscrive (.subscribe()) per ricevere i dati non appena il backend ha terminato l'elaborazione.
 */
import { Observable } from 'rxjs';
/**
 * Importiamo le interfacce formali definite nel frontend. Queste garantiscono la Type Safety, assicurando che i 
 * dati scambiati tra Angular e Spring Boot rispettino fedelmente la struttura dei DTO definiti nel backend Java.
 */
import { Prodotto } from '../models/prodotto';
import { RequestAcquisto, ResponseAcquisto, RequestEmissione, RequestAnnullamento } from '../models/acquisto';

/**
 * Il decoratore @Injectable configura il comportamento della classe all'interno dell'applicazione.
 * L'attributo 'providedIn: root' specifica che il servizio viene registrato a livello globale.
 * Questo implementa il Design Pattern "Singleton": Angular creerà una sola istanza di questa classe 
 * in tutta l'applicazione, ottimizzando la memoria e centralizzando lo stato delle chiamate di rete.
 */
@Injectable({ providedIn: 'root'})
export class StoreService {

    /**
     * 'baseUrl' rappresenta l'endpoint principale esposto dalla boundary di Spring Boot.
     * - private: impedisce ai componenti esterni di modificare questo indirizzo per errore.
     * - readonly: garantisce l'immutabilità della stringa una volta avviata l'applicazione.
     */
    private readonly baseUrl = 'http://localhost:8080/api/store';

    /**
     * Costruttore della classe
     * Sfrutta il pattern della Dependency Injection per richiedere ad Angular un'istanza di HttpClient.
     * Scrivendo 'private http: HttpClient', Angular fa due cose contemporaneamente:
     * 1. Inietta il client HTTP nel servizio.
     * 2. Crea automaticamente una variabile d'istanza chiamata 'http' accessibile in tutta la classe.
     */
    constructor(private http: HttpClient) { }

    /**
     *  Recupero del catalogo di prodotti
     * - Scopo: Invia una richiesta HTTP di tipo GET all'endpoint '/catalogo'.
     * - Ritorno: Restituisce un flusso asincrono (Observable) che conterrà un array di oggetti Prodotto.
     * - Uso '<Prodotto[]>' per dire ad Angular di convertire automaticamente il JSON ricevuto dal backend
     * in un array conforme all'interfaccia TypeScript scritta nel model.
     */
    getProdotti(): Observable<Prodotto[]> {
        return this.http.get<Prodotto[]>(`${this.baseUrl}/catalogo`);
    }

    /**
     * Recupero del prodotto per il catalogo
     * - Innesca una richiesta HTTP GET verso l'endpoint della Boundary Spring Boot.
     */
    getProdottoByCodice(codice: string): Observable<Prodotto> {
        return this.http.get<Prodotto>(`${this.baseUrl}/catalogo/${codice}`);
    }

    /**
     *  Avvio della prima fase di acquisto
     * - Scopo: Invia una richiesta HTTP di tipo POST all'endpoint '/acquisto'.
     * - Payload: Richiede in input un oggetto di tipo 'RequestAcquisto' (credenziali utente, codice prodotto, quantità).
     * - Ritorno: Restituisce un Observable contenente un oggetto 'ResponseAcquisto' (costo totale, indirizzo, carta di credito
     *   e codice ordine), generato dalla Control del backend per mostrare il riepilogo finanziario e logistico.
     */
    acquistaProdotto(req: RequestAcquisto): Observable<ResponseAcquisto> {
        return this.http.post<ResponseAcquisto>(`${this.baseUrl}/acquisto`, req);
    }

    /**
     *  Finalizzazione dell'acquisto
     * - Scopo: Invia una richiesta HTTP di tipo PUT all'endpoint '/emissione' per confermare l'ordine nel Database.
     * - Payload: Richiede l'oggetto 'RequestEmissione' contenente il codice numerico dell'ordine generato nella prima fase.
     * - Ritorno: Restituisce un Observable con un oggetto '{ message: string }', 
     * utile per intercettare il messaggio di conferma testuale inviato dal server Spring Boot.
     */
    emettiOrdine(req: RequestEmissione): Observable<{ message: string }> {
        return this.http.put<{ message: string }>(`${this.baseUrl}/emissione`, req);
    }

    /**
     * Annullamento dell'ordine
     * - Scopo: Spedisce una richiesta DELETE inviando l'oggetto JSON '{codiceOrdine}' nel Body.
     * - Payload: Richiede l'oggetto 'codiceOrdine' contenente il codice numerico dell'ordine generato nella prima fase da cancellare.
     * - Ritorno: Restituisce un Observable con un oggetto '{ message: string }', 
     */
    annullaOrdine(req: RequestAnnullamento): Observable<{ message: string }> {
        return this.http.delete<{ message: string }>(`${this.baseUrl}/annulla`, { body: req });
    }
}
