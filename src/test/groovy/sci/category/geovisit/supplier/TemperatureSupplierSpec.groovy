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

    def "tst"() {
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
}