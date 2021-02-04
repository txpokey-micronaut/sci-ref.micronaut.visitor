package sci.category.geovisit.supplier

import grails.gorm.services.Service
import groovy.transform.CompileStatic
import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.RxStreamingHttpClient
import io.micronaut.http.client.annotation.Client
import sci.category.geovisit.contract.SupplierContract
import sci.category.geovisit.domain.Temperature

import javax.inject.Inject

@CompileStatic
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
    HttpResponse<Map> getTemperatureByCityAndByStateViaHttp( Map cityAndState ) {
        final def weatherApiKey = '9ecd4d849bec4bad904195112210302'
        TemperatureSupplier supplier = new TemperatureSupplier()
        def cityStateQueryParameter = "${cityAndState.city},${cityAndState.state}"
        HttpRequest request = HttpRequest.GET("/current.json?key=${weatherApiKey}&q=${cityStateQueryParameter}")
        HttpResponse<Map> rsp = getClient().toBlocking().exchange(request, Argument.of(HashMap.class as Class<Object>)) as HttpResponse<Map>
    }
}
