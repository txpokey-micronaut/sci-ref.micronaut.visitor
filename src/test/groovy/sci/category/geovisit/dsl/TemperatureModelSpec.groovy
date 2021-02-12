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
        TemperatureModel model = new TemperatureModel(config)
        Map dallasPayloadMap = [city: "Dallas", state: "TX"]
        Closure c = {
            get dallasPayloadMap
        }
        Temperature temperature = model.measure c

        expect:
        temperature

    }

    def "test measure2"() {
        given:
        final Map config = [httpClient: httpClient]
        TemperatureModel model = new TemperatureModel(config)

        def text = """
        Closure asText = {
            Map dallasPayloadMap = [city: "Dallas", state: "TX"]
            get dallasPayloadMap
        }
        """
        def tempInDallas = Eval.me(text)

        Temperature temperature = model.measure tempInDallas

        expect:
        temperature

    }
    def "test measure3"() {
        given:
        final Map config = [httpClient: httpClient]
        TemperatureModel model = new TemperatureModel(config)

        def text = """
            Map dallasPayloadMap = [city: "Dallas", state: "TX"]
            get dallasPayloadMap
        """
        def wellFormed = DslBuilder.getWellFormedClosure(text)
        def tempInDallas = Eval.me(wellFormed)

        Temperature temperature = model.measure tempInDallas

        expect:
        temperature

    }
}
