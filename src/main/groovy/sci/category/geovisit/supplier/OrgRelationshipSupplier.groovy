package sci.category.geovisit.supplier

import sci.category.geovisit.constant.FactoryKey
import sci.category.geovisit.constant.OrgAddressKey
import sci.category.geovisit.contract.BuildContract
import sci.category.geovisit.contract.SupplierContract

class OrgRelationshipSupplier implements SupplierContract<List<Map>>{
    private Map buildConfig

    private OrgRelationshipSupplier() {}

    private OrgRelationshipSupplier(Builder _builder) {
        this.buildConfig = _builder.buildConfig
    }

    @Override
    List<Map> get() {
        return buildConfig[FactoryKey.Bootstrap]
    }
    Map getRoot() {
        buildConfig[OrgAddressKey.Root]
    }

    static class Builder implements BuildContract<OrgRelationshipSupplier>{
        private Map buildConfig

        static Builder newInstance(Map cfg) {
            return new Builder(cfg)
        }

        private Builder(cfg) {
            buildConfig = cfg
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
            def statesMap = parseFileToMaps.groupBy([{ m -> m.state }, { m -> m.county }])
            def citiesList = []
            def stateCountyMap = [:] // hysteresis for parent map
            statesMap.each {
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
            buildConfig[FactoryKey.Bootstrap] = citiesList
            final def root = [state: "State short", county: "County"]
            buildConfig[OrgAddressKey.Root] = root
            supplier
        }

    }
}