package sci.category.geovisit.factory.supplier


import sci.category.geovisit.constant.FactoryKey
import sci.category.geovisit.constant.OrgAddressKey
import sci.category.geovisit.contract.FactoryContract
import sci.category.geovisit.contract.OrgRelationshipContract

class OrgRelationshipSupplier implements FactoryContract<OrgRelationshipContract>{

    static OrgRelationshipSupplier newInstance(Map config) {
        OrgRelationshipSupplier factory = new OrgRelationshipSupplier(config)
        FactoryContract<OrgRelationshipContract> contract = factory.newInstance()
    }

    static OrgRelationshipContract build(Map config) {
        FactoryContract<OrgRelationshipContract> factory = newInstance(config)
        OrgRelationshipContract contract = factory.build()
        contract
    }
    //
    private OrgRelationshipContract factoryContract
    private Map factoryConfigure
    private List<Map> factoryBootstrap

    private OrgRelationshipSupplier(_config) {
        factoryConfigure = _config
    }

    FactoryContract<OrgRelationshipContract> newInstance() {
        factoryContract = new OrgRelationshipContract(){
            private Map configure = factoryConfigure
            private List<Map> bootstrap = factoryBootstrap

            @Override
            List<Map> get() {
                return bootstrap
            }
        }
        this
    }

    @Override
    OrgRelationshipContract build() {
        final DELIMIT = "[|]"

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
        factoryConfigure[FactoryKey.Bootstrap] = statesList
        factoryConfigure[OrgAddressKey.Root] = root
        this.factoryContract
    }

}
