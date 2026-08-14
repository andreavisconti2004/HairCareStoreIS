package App;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Punto di ingresso principale e classe di configurazione globale dell'applicazione Spring Boot.
 * L'annotazione {@code @SpringBootApplication} agisce come meta-annotazione che sintetizza l'attivazione
 * dei moduli:
 * <p>{@code @Configuration}
 * <p>{@code @EnableAutoConfiguration} Spring Boot configura automaticamente Tomcat, Jackson, Hibernate, ecc.
 * <p>{@code @ComponentScan} Spring scansiona i package indicati e registra nel Container Ioc tutte le classi 
 * annotate come: {@code @Component} / {@code @Service} / {@code @Repository} / {@code @Controller} / 
 * {@code @RestController}.
 * Di norma, Spring Boot avvia la scansione automatica a partire dal pacchetto in cui è allocata la classe Main 
 * (in questo caso il package {@code App}) esplorandone i nodi figli. Tuttavia, nel progetto i moduli
 * {@code Boundary}, {@code Control} e {@code Database} non sono figli di {@code App}, bensì package di pari livello.
 * Dunque esplicitamente sono inseriti tramite {@code scanBasePackages}.
 * {@code @EntityScan} e {@code @EnableJpaRepositories} servono per indicare ad Hibernate dove cercare le {@code @Entity} 
 * e a Spring Data dove cercare le interfacce {@code JpaRepository}.
 */
@SpringBootApplication(scanBasePackages={ "App", "Boundary", "Control", "Database"})
@EntityScan(basePackages="Entity")
@EnableJpaRepositories(basePackages="Database")
public class HairCareStoreApplication {

    /**
     * SpringApplication.run avvia il context, fa il component-scan, configura Hibernate, alza Tomcat
     * sulla porta indicata in application.properties (8080 di default) e blocca il main thread finchè
     * l'applicazione resta viva.
     */
    public static void main(String[] args) {
        SpringApplication.run(HairCareStoreApplication.class, args);
    }
}
