package sci.category.geovisit.supplier

import grails.gorm.services.Service
import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.RxStreamingHttpClient
import io.micronaut.http.client.annotation.Client
import sci.category.geovisit.contract.SupplierContract
import sci.category.geovisit.domain.Temperature

import javax.inject.Inject

@Service(TemperatureSupplier)
class TemperatureSupplier implements SupplierContract<List<Temperature>>{

    @Inject
    @Client("https://api.weatherapi.com/v1")
    RxStreamingHttpClient client

    @Override
    List<Temperature> get() {
        return null
    }
    RxStreamingHttpClient getClient() {
        return client
    }
    Temperature getTemperatureByCityAndByStateViaHttp( Map cityAndState ) {
        final def weatherApiKey = '9ecd4d849bec4bad904195112210302'
        HttpResponse<Map> rsp = getTemperatureFromRemoteApi(cityAndState, weatherApiKey)
        Temperature tempDomain = getTemperatureDomainFromHttpResponse(rsp)
        tempDomain
    }

    private getTemperatureFromRemoteApi(Map cityAndState, weatherApiKey) {
        def cityStateQueryParameter = "${cityAndState.city},${cityAndState.state}"
        HttpRequest request = HttpRequest.GET("/current.json?key=${weatherApiKey}&q=${cityStateQueryParameter}")
        HttpResponse<Map> rsp = getClient().toBlocking().exchange(request, Argument.of(HashMap.class as Class<Object>)) as HttpResponse<Map>
        rsp
    }
    private getTemperatureDomainFromHttpResponse(HttpResponse<Map> rsp) {
        Map body = rsp.body()
        Map tempPayloadMap = [city: body.location.name, state: body.location.region, temp: body.current.temp_c]
        Map tempConstructorMap = [description: "${tempPayloadMap.city}|${tempPayloadMap.state}", payload:
                tempPayloadMap]
        Temperature tempDomain = new Temperature(tempConstructorMap)
        tempDomain
    }

}
