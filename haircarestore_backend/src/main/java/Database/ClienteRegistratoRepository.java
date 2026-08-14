package Database;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import Entity.EntityClienteRegistrato;

/**
 * Repository per la tabella CLIENTI_REGISTRATI.
 * Un Repository di Spring Data è un'interfaccia.
 * A runtime Spring Data genera (con un proxy dinamico) un bean che implementa
 * questa interfaccia con il codice JDBC necessario. 
 * Hibernate + reflection leggono il nome dei metodi e le annotazioni e generano la query SQL.
 *
 * Estendendo {@code JpaRepository<Entita, TipoChiavePrimaria>} otteniamo i metodi CRUD:
 *   {@code - save(entity)        → INSERT o UPDATE}
 *   {@code - findById(id)        → SELECT * FROM ... WHERE id = ?}
 *   {@code - findAll()           → SELECT * FROM ...}
 *   {@code - findAll(Pageable)   → SELECT con paginazione}
 *   {@code - existsById(id)      → SELECT COUNT(*) > 0}
 *   {@code - count()             → SELECT COUNT(*)}
 *   {@code - deleteById(id)      → DELETE FROM ... WHERE id = ?}
 *   {@code - deleteAll()}
 *
 */
public interface ClienteRegistratoRepository extends JpaRepository<EntityClienteRegistrato, String>{

    /**
     * Recupera le credenziali di un cliente per le operazioni di autenticazione.
     * Per le derived queries seguiamo il formalismo:
     * {@code - findBy + <Proprieta1>And<Proprieta2>And...}
     * dove Proprietà è il nome dell'attributo in Java mappato sulla colonna del Database.
     * Restituisce un Optional.empty() per gestire in modo sicuro l'eventuale assenza del record 
     * senza sollevare eccezioni di tipo NullPointerException.
     */
    Optional<EntityClienteRegistrato> findByNomeUtenteAndPassword(String nomeUtente, String password);
}
