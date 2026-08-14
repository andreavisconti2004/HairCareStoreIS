package Database;

import org.springframework.data.jpa.repository.JpaRepository;

import Entity.EntityOrdine;

/**
 * Repository per la tabella ORDINI.
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
public interface OrdineRepository extends JpaRepository<EntityOrdine, Integer>{

}
