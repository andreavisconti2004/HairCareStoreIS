/**
 * Modello formale accoppiato in modo speculare al DTO di risposta emesso dall'endpoint di consultazione
 * del catalogo dal backend Spring Boot.
 */
export interface Prodotto {
    codice: string;
    nome: string;
    descrizione: string;
    categoria: string;
    prezzo: number;
    quantita: number;
}