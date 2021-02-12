package sci.category.geovisit.dsl


import io.micronaut.http.client.RxStreamingHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import sci.category.geovisit.domain.Temperature
import spock.lang.Specification

import javax.inject.Inject

@MicronautTest(packages = "sci.category.geovisit")
class TemperatureModelSpec extends Specification{
    @Inject
    @Client("https://api.weatherapi.com/v1")
    RxStreamingHttpClient httpClient
    def "test measure"() {
        given:
        final Map config = [httpClient: httpClient]
        TemperatureModel model = new TemperatureModel()
        Map dallasPayloadMap = [city: "Dallas", state: "TX"]
        Map configMap = [httpClient: httpClient]
        Temperature temperature = model.measure {
            setUp configMap
            get dallasPayloadMap
        }

        expect:
        temperature

//        when:
//        // TODO implement stimulus
//        then:
//        // TODO implement assertions
    }
}
