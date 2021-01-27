package sci.category

import groovy.util.logging.Slf4j
import io.micronaut.runtime.EmbeddedApplication
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
    private static final Logger log = LoggerFactory.getLogger(MicronautDemoSpec.class);
}
