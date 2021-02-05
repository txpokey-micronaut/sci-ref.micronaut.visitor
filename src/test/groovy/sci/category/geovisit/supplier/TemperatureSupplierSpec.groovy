package sci.category.geovisit.supplier

import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.RxStreamingHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import sci.category.geovisit.domain.Temperature
import spock.lang.Specification

import javax.inject.Inject

@MicronautTest(packages="sci.category.geovisit")
class TemperatureSupplierSpec extends Specification{
    @Inject
    @Client("https://api.weatherapi.com/v1")
    RxStreamingHttpClient client // <

    @Inject
    TemperatureSupplier supplier

    def "sanityCheck"(){
        expect:
        supplier
        when:
        RxStreamingHttpClient client = supplier.client
        then:
        client
    }
    def "test TDD on http clienting for temperature by city and state"() {
        when:
        HttpRequest request = HttpRequest.GET('/current.json?key=9ecd4d849bec4bad904195112210302&q=plano,tx')
        HttpResponse<Map> rsp = client.toBlocking().exchange(request, Argument.of(HashMap.class))

        then: 'the endpoint can be accessed'
        rsp.status == HttpStatus.OK // <5>
        rsp.body() // <6>
        when:
        Map body = rsp.body()
        Map tempPayloadMap = [ city: body.location.name , state: body.location.region , temp: body.current.temp_c ]
        Map tempConstructorMap = [description: "${tempPayloadMap.city}|${tempPayloadMap.state}", payload:
                tempPayloadMap ]
        Temperature tempDomain = new Temperature(tempConstructorMap)
        tempDomain.save()
        then:
        def all = Temperature.all
        1 <= all.size()
        Set set = new HashSet( all )
        set.contains(tempDomain)
        true
    }
    def "refactor TDD on http client for temperature by city and state"() {
        when:
        Map cityStateMap = [city: "plano", state: "tx"]
        Temperature tempDomain = supplier.getTemperatureByCityAndByStateViaHttp(cityStateMap)
//        Temperature tempDomain = null
        tempDomain.save()
        then:
        def all = Temperature.all
        1 <= all.size()
        Set set = new HashSet( all )
        set.contains(tempDomain)
        true
    }

    def "test static newInstance"() {
        given:
        def builder = TemperatureSupplier.Builder.newInstance([:])
        assert builder
        def supplier = builder.build()
        expect:
        supplier
        def supply = supplier.get()
        supply
    }
}