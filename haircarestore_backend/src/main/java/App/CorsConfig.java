package App;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Componente configurativo deputato alla definizione delle policy CORS (Cross-Origin Resource Sharing) 
 * a livello globale. CORS è un meccanismo di sicurezza implementato nativamente dai browser web per determinare 
 * se consentire a un'applicazione client (Il Front-End Angular residente su http://localhost:4200) 
 * di accedere a risorse posizionate su un'origine o porta differente (Il backend REST su http://localhost:8080).
 * Questa classe abilita la ricezione di richieste da qualsiasi origine ("*") e per tutti i principali verbi 
 * del protocollo HTTP, garantendo la piena operatività dei canali di comunicazione.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer{

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        WebMvcConfigurer.super.addCorsMappings(registry);
        // Applica le regole unicamente alle rotte che iniziano con il prefisso di servizio /api/store/**
        registry.addMapping("/api/store/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }

}
