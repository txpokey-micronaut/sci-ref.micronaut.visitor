package sci.category.geovisit.supplier

import sci.category.geovisit.constant.FactoryKey
import sci.category.geovisit.constant.OrgAddressKey
import sci.category.geovisit.contract.BuildContract
import sci.category.geovisit.contract.SupplierContract
import sci.category.geovisit.domain.OrgAddress

class OrgRelationshipSupplier implements SupplierContract<List<OrgAddress>>{
    private Map buildConfig

    private OrgRelationshipSupplier() {}

    private OrgRelationshipSupplier(Builder _builder) {
        this.buildConfig = _builder.buildConfig
    }

    @Override
    List<OrgAddress> get() {
        return buildConfig[FactoryKey.Bootstrap]
    }
    OrgAddress getRoot() {
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
            def statesList = []
            def countyList = []
            def citiesList = []
            def stateCountyMap = [:] // hysteresis for parent map
            final def rootAddressMap =
                    [description: "root", payload: [country: "usa", state: null, county:null, city:null]]
            final def root = new OrgAddress(rootAddressMap)
            rootAddressMap.payload[(OrgAddressKey.Parent)] = root
            rootAddressMap.payload[(OrgAddressKey.Child)] = root
            statesMap.each {
                stateKey, Map countiesMap ->
                    def stateMap = [state: stateKey, county: null, city: null ]
                    def stateAddessMap = [description: stateKey , payload: stateMap]
                    def state = new OrgAddress(stateAddessMap)
                    stateAddessMap.payload[(OrgAddressKey.Parent)] = root
                    stateAddessMap.payload[(OrgAddressKey.Child)] = state
                    statesList += state
                    countiesMap.each {
                        countyKey, List<Map> cityMapList ->
                            def fullCountyKey = "${stateKey}|${countyKey}" as String
                            def county = stateCountyMap.get(fullCountyKey)
                            if ( null == county ) {
                                def countyMap = [state: stateKey, county: countyKey, city: null ]
                                def countyAddressMap = [description: fullCountyKey , payload: countyMap]
                                county = new OrgAddress(countyAddressMap)
                                countyAddressMap.payload[(OrgAddressKey.Parent)] = state
                                countyAddressMap.payload[(OrgAddressKey.Child)] = county
                                stateCountyMap[fullCountyKey] = county
                                countyList += county
                            } // if null == county
                            cityMapList.unique{m -> m.city }.each {
                                cityMap ->
                                    def fullCityKey = "${fullCountyKey}|${cityMap.city}" as String
                                    def cityAddressMap = [description: fullCityKey, payload: cityMap]
                                    def city = new OrgAddress(cityAddressMap)
                                    citiesList += city
                                    cityAddressMap.payload[(OrgAddressKey.Parent)] = county
                                    cityAddressMap.payload[(OrgAddressKey.Child)] = city
                            } //cityMapList
                    } //countiesMap
            } // statesMap
            List<Map> bootstrap = [root]
            bootstrap += statesList
            bootstrap += countyList
            bootstrap += citiesList
            OrgAddress.saveAll(bootstrap)
            buildConfig[FactoryKey.Bootstrap] = bootstrap
            buildConfig[OrgAddressKey.Root] = root
            supplier
        }

    }
}