package sci.category.geovisit

import groovy.util.logging.Slf4j
import io.micronaut.runtime.EmbeddedApplication
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import spock.lang.Specification

import javax.inject.Inject

@MicronautTest
@Slf4j
class MicronautDemoSpec extends Specification {

    @Inject
    EmbeddedApplication<?> application

    void 'test it works'() {
        given:
        log.debug("PING!")
        expect:
        application.running
    }
}
