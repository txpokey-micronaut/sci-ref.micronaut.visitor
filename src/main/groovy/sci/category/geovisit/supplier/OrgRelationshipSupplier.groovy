package sci.category.geovisit.supplier

import sci.category.geovisit.constant.FactoryKey
import sci.category.geovisit.constant.OrgAddressKey
import sci.category.geovisit.contract.BuildContract
import sci.category.geovisit.contract.OrgRelationshipContract
import sci.category.geovisit.contract.SupplierContract

class OrgRelationshipSupplier implements SupplierContract<List<Map>>{
    private Builder builder

    private OrgRelationshipSupplier() {}

    private OrgRelationshipSupplier(Builder _builder) {
        this.builder = _builder
    }

    @Override
    List<Map> get() {
        return builder.buildConfig[FactoryKey.Bootstrap]
    }
    Map getRoot() {
        builder.buildConfig[OrgAddressKey.Root]
    }

    static class Builder implements BuildContract<OrgRelationshipSupplier>{
        private Map buildConfig
//        private List<Map> bootstrapData

        static Builder newInstance(Map cfg) {
            return new Builder(cfg)
        }

        private Builder(cfg) {
            buildConfig = cfg
//            bootstrapData = buildConfig[FactoryKey.Bootstrap]
//            assert bootstrapData
        }

        @Override
        OrgRelationshipSupplier build() { // TODO get CSV location from cfg
            final DELIMIT = "[|]"
            OrgRelationshipSupplier supplier = new OrgRelationshipSupplier(this)

            def parseFileToMaps = new File("src/main/resources/citiesUsa.csv").readLines().inject([], {
                list, line ->
                    List tokens = line.split(DELIMIT)
                    Map map = [state: tokens[1], county: tokens[3], city: tokens[0]]
                    list + map
            })
            def statesList = parseFileToMaps.groupBy([{ m -> m.state }, { m -> m.county }])
            final def root = [state: "State short", county: "County"]
            def citiesList = []
            def stateCountyMap = [:]
            statesList.each {
                stateKey, Map countiesMap ->
                    countiesMap.each {
                        countyKey, List<Map> cityMapList ->
                            cityMapList.each {
                                city ->
                                    citiesList += city
                                    def key = "${stateKey}|${countyKey}" as String
                                    def parent = stateCountyMap.get(key)
                                    parent = parent ?: [state: stateKey, county: countyKey]
                                    stateCountyMap.put(key, parent)
                                    city[(OrgAddressKey.Parent)] = parent
                                    city[(OrgAddressKey.Child)] = city
                            }
                    }
            }
            buildConfig[FactoryKey.Bootstrap] = statesList
            buildConfig[OrgAddressKey.Root] = root
            buildConfig.root = buildConfig[((OrgAddressKey.Root))]
//            OrgRelationshipSupplier supplier = new OrgRelationshipSupplier(this)
            supplier
        }

    }
}