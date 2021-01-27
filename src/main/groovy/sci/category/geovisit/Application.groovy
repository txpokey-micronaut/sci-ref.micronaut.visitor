package sci.category.geovisit

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.micronaut.runtime.Micronaut
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info

@OpenAPIDefinition(
    info = @Info(
            title = "micronaut-geovisit",
            version = "0.0"
    )
)
@Slf4j
@CompileStatic
class Application {
    static void main(String[] args) {
        Micronaut.run(Application, args)
        bootstrap()
    }
//    @PostConstruct
    private static void bootstrap() {
        log.info("PING!")
    }
}
