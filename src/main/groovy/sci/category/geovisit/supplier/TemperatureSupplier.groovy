package sci.category.geovisit.supplier

import grails.gorm.services.Service
import groovy.transform.CompileStatic
import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.RxStreamingHttpClient
import io.micronaut.http.client.annotation.Client
import org.jgrapht.Graph
import org.jgrapht.traverse.BreadthFirstIterator
import sci.category.geovisit.constant.FactoryKey
import sci.category.geovisit.constant.OrgAddressKey
import sci.category.geovisit.contract.BuildContract
import sci.category.geovisit.contract.SupplierContract
import sci.category.geovisit.domain.OrgAddress
import sci.category.geovisit.domain.Temperature

import javax.inject.Inject
//@CompileStatic
@Service(TemperatureSupplier)
class TemperatureSupplier implements SupplierContract<List<Temperature>>{
    private Map buildConfig
    private RxStreamingHttpClient client

    @Override
    List<Temperature> get() {
        return buildConfig[FactoryKey.Bootstrap]
    }

    RxStreamingHttpClient getClient() {
        return client
    }

    // TODO makes no sense. need to inject client into builder and then DI on constructor here
    TemperatureSupplier(Builder builder) {
        buildConfig = builder.builderConfig as Map
    }
    TemperatureSupplier() {
    }

    static class Builder implements BuildContract<TemperatureSupplier>{
//        @Inject
//        @Client("https://api.weatherapi.com/v1")
        private RxStreamingHttpClient client
        private Map builderConfig

        private Builder(Map cfg) {
            builderConfig = cfg
        }
        private Builder(RxStreamingHttpClient _client, Map cfg) {
            builderConfig = cfg
            client = _client
        }
        RxStreamingHttpClient getClient() {
            return client
        }
        static Builder newInstance(RxStreamingHttpClient client, Map cfg) {
            return new Builder(client, cfg)
        }
        @Override
        TemperatureSupplier build() {
            OrgTreeSupplier orgTreeSupplier = getOrgTreeSupplier()
            Graph graph = orgTreeSupplier.get()
            Iterator iterator = new BreadthFirstIterator(graph)
            List<Temperature> tempsList = iterator.collect { v ->
                OrgAddress oa = (OrgAddress) v
                Map payload = oa.payload
                Map cityAndState = [ city: payload.city, state: payload.state ]
                Temperature temp = getTemperatureByCityAndByStateViaHttp(cityAndState)
                temp
            }
            builderConfig[FactoryKey.Bootstrap] = tempsList
            this
        }

        private Temperature getTemperatureByCityAndByStateViaHttp(Map cityAndState) {
            final def weatherApiKey = '9ecd4d849bec4bad904195112210302'
            HttpResponse<Map> rsp = getTemperatureFromRemoteApi(cityAndState, weatherApiKey)
            Temperature tempDomain = getTemperatureDomainFromHttpResponse(rsp)
            tempDomain
        }

        private Temperature getTemperatureDomainFromHttpResponse(HttpResponse<Map> rsp) {
            Map body = rsp.body()
            Map location = body.location as Map
            Map current = body.current as Map
            Map tempPayloadMap = [city: location.name, state: location.region, temp: current.temp_c]
            Map tempConstructorMap = [description: "${tempPayloadMap.city}|${tempPayloadMap.state}", payload:
                    tempPayloadMap]
            Temperature tempDomain = new Temperature(tempConstructorMap)
            tempDomain
        }

        private HttpResponse<Map> getTemperatureFromRemoteApi(Map cityAndState, weatherApiKey) {
            def cityStateQueryParameter = "${cityAndState.city},${cityAndState.state}"
            HttpRequest request = HttpRequest.GET("/current.json?key=${weatherApiKey}&q=${cityStateQueryParameter}")
            HttpResponse<Map> rsp = getClient().toBlocking().exchange(request, Argument.of(HashMap.class as Class<Object>)) as HttpResponse<Map>
            rsp
        }

        private OrgTreeSupplier getOrgTreeSupplier() {
            OrgRelationshipSupplier orgRelationshipsSupplier = getOrgRelationshipsSupplier()
            def root = orgRelationshipsSupplier.root
            def bootstrapData = orgRelationshipsSupplier.get()
            def config = [(OrgAddressKey.Root): root, (FactoryKey.Bootstrap): bootstrapData]
            def builder = OrgTreeSupplier.Builder.newInstance(config)
            def orgTreeSupplier = builder.build()
        }
        private OrgRelationshipSupplier getOrgRelationshipsSupplier() {
            def root = [:]
            List<Map> build = [[(OrgAddressKey.Root): root]]
            Map config = [(OrgAddressKey.Root): root, (FactoryKey.Bootstrap): build]
            def orgRelationshipsBuilder = OrgRelationshipSupplier.Builder.newInstance(config)
            assert orgRelationshipsBuilder
            def orgRelationshipsSupplier = orgRelationshipsBuilder.build()
        }
    }
}
