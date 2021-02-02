package sci.category.geovisit.supplier

import sci.category.geovisit.constant.FactoryKey
import sci.category.geovisit.constant.OrgAddressKey
import sci.category.geovisit.contract.BuildContract
import sci.category.geovisit.contract.SupplierContract
import sci.category.geovisit.domain.OrgAddress

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
                                cityMap ->
                                    def fullCountyKey = "${stateKey}|${countyKey}" as String
                                    def countyOrgAddress = stateCountyMap.get(fullCountyKey)
                                    if ( null == countyOrgAddress ) {
                                        def countyPayloadMap = [state: stateKey, county: countyKey]
                                        def countyAddressMap = [description: fullCountyKey, payload: countyPayloadMap]
                                        countyOrgAddress = new OrgAddress(countyAddressMap)
                                        stateCountyMap.put(fullCountyKey, countyOrgAddress)
//                                        countyOrgAddress.save()
                                    }
                                    def fullCityKey = "${fullCountyKey}|${cityMap.city}" as String
                                    def cityAddressMap = [description: fullCityKey, payload: cityMap]
                                    def city = new OrgAddress(cityAddressMap)
                                    citiesList += city
                                    cityAddressMap[(OrgAddressKey.Parent)] = countyOrgAddress
                                    cityAddressMap[(OrgAddressKey.Child)] = city
                            } //cityMapList
                    } //countiesMap
            } // statesMap
            OrgAddress.saveAll(citiesList)
            OrgAddress.saveAll(stateCountyMap.values())
            buildConfig[FactoryKey.Bootstrap] = citiesList
            final def root = [state: "State short", county: "County"]
            buildConfig[OrgAddressKey.Root] = root
            supplier
        }

    }
}