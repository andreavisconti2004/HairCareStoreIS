/**
 * Incapsula il payload richiesto dalla Boundary nella prima fase del caso d'uso acquistaProdotto.
 * Le chiavi rispecchiano fedelmente i vincoli sintattici strutturati nel backend.
 */
export interface RequestAcquisto{
  nomeUtente: string;
  password: string;
  codiceProdotto: string;
  quantita: number;
}

/**
 * Riceve i dati finanziari e logistici emessi dalla Control al termine della prima fase del caso d'uso
 * acquistaProdotto.
 */
export interface ResponseAcquisto{
  costoTotale: string;
  cartaDiCredito: string;
  indirizzo: string;
  codiceOrdine: number;
}

/**
 * Contiene l'identificativo numerico generato nativamente dal Database, necessario per innescare la
 * seconda fase del caso d'uso acquistaProdotto.
 */
export interface RequestEmissione{
  codiceOrdine: number;
}

export interface RequestAnnullamento{
  codiceOrdine: number;
}