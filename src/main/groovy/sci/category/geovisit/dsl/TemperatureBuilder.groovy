package sci.category.geovisit.dsl

import groovy.util.logging.Slf4j
import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.RxStreamingHttpClient
import sci.category.geovisit.domain.Temperature

@Slf4j
class TemperatureBuilder{
    private Map config = [:]
    private getClient() {
        config.httpClient as RxStreamingHttpClient
    }
    void setUp( Map cfg ) {
        config = cfg
    }
    Temperature get(Map cityAndState) {
        Temperature result = getTemperatureByCityAndByStateViaHttp(cityAndState)
    }

    private Temperature getTemperatureByCityAndByStateViaHttp(Map cityAndState) {
        final def weatherApiKey = '9ecd4d849bec4bad904195112210302'
        HttpResponse<Map> rsp = getTemperatureFromRemoteApi(cityAndState, weatherApiKey)
        Map responseMap = getMapFromHttpResponseWhileHandleNullResponseUseCase(cityAndState, rsp)
        Temperature tempDomain = getTemperatureDomainFromHttpResponse(responseMap)
        tempDomain
    }

    private Map getMapFromHttpResponseWhileHandleNullResponseUseCase(Map cityAndState, HttpResponse<Map> rsp) {
        Map map = [:]
        if (Objects.isNull(rsp)) {
            final Double ABSOLUTE_ZERO = -273.15d
            final def errorUseCaseReferenceMap =
                    [status: HttpStatus.I_AM_A_TEAPOT, current: [temp_c: ABSOLUTE_ZERO]]
            map.location = [name: cityAndState.city, region: cityAndState.state]
            map.putAll(errorUseCaseReferenceMap)
        } else {
            map.putAll(rsp.body())
            map.status = rsp.status()
        }
        return map
    }

    private Temperature getTemperatureDomainFromHttpResponse(Map body) {
        Map location = body.location as Map
        Map current = body.current as Map
        Map tempPayloadMap = [city: location.name, state: location.region, temp: current.temp_c]
        Map tempConstructorMap = [description: "${tempPayloadMap.city}|${tempPayloadMap.state}", payload:
                tempPayloadMap]
        Temperature tempDomain = new Temperature(tempConstructorMap)
        tempDomain
    }

    private HttpResponse<Map> getTemperatureFromRemoteApi(Map cityAndState, weatherApiKey) {
        def cityStateQueryParameter = "${cityAndState.city},${cityAndState.state}".replaceAll(/\s/, '%20')
        log.info(cityStateQueryParameter)
        HttpResponse<Map> rsp = null
        try {
            HttpRequest request = HttpRequest.GET("/current.json?key=${weatherApiKey}&q=${cityStateQueryParameter}")
            rsp = getClient().toBlocking().exchange(request, Argument.of(HashMap.class as Class<Object>)) as HttpResponse<Map>
        } catch (Exception ex) {
            log.error("temperature lookup failure", ex)
        }
        rsp
    }
}
