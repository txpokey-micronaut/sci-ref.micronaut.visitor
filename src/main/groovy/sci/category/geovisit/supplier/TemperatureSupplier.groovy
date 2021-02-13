package sci.category.geovisit.supplier

import groovy.util.logging.Slf4j
import io.micronaut.http.client.RxStreamingHttpClient
import org.jgrapht.Graph
import org.jgrapht.traverse.BreadthFirstIterator
import sci.category.geovisit.constant.FactoryKey
import sci.category.geovisit.constant.OrgAddressKey
import sci.category.geovisit.contract.BuildContract
import sci.category.geovisit.contract.SupplierContract
import sci.category.geovisit.domain.OrgAddress
import sci.category.geovisit.domain.Temperature
import sci.category.geovisit.dsl.TemperatureModel

@Slf4j
class TemperatureSupplier implements SupplierContract<List<Temperature>>{
    private Map buildConfig

    @Override
    List<Temperature> get() {
        return buildConfig[FactoryKey.Bootstrap]
    }

    // TODO makes no sense. need to inject client into builder and then DI on constructor here
    TemperatureSupplier(Builder builder) {
        buildConfig = builder.builderConfig as Map
    }

    TemperatureSupplier() {
    }

    static class Builder implements BuildContract<TemperatureSupplier>{

        private Map builderConfig

        private Builder(Map cfg) {
            builderConfig = cfg
        }

        RxStreamingHttpClient getClient() {
            return builderConfig.httpClient as RxStreamingHttpClient
        }

        static Builder newInstance(Map cfg) {
            return new Builder(cfg)
        }

        @Override
        TemperatureSupplier build() {
            final Map config = [httpClient: getClient()]
            TemperatureModel model = new TemperatureModel(config)
            OrgTreeSupplier orgTreeSupplier = getOrgTreeSupplier()
            Graph graph = orgTreeSupplier.get()
            OrgAddress root = orgTreeSupplier.getRoot()
            Iterator iterator = new BreadthFirstIterator(graph, root)
            List<Temperature> tempsList = iterator.collect { v ->
                OrgAddress oa = (OrgAddress) v
                Map payload = oa.payload
                Map cityAndState = [city: payload.city, state: payload.state]
                if (!cityAndState.values().contains(null)) {
                    Closure c = {
                        get cityAndState
                    }
                    Temperature temp = model.measure c
                    temp
                }
            }
            builderConfig[FactoryKey.Bootstrap] = tempsList
            TemperatureSupplier supplier = new TemperatureSupplier(this)
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
