package sci.category.geovisit.factory.supplier

import sci.category.geovisit.constant.OrgAddressKey
import spock.lang.Specification

class OrgRelationshipSupplierTest extends Specification{
    final static DELIMIT = "[|]"

    def "test build"() {
        given:
        def parseFileToMaps = new File("src/main/resources/citiesUsa.csv").readLines().inject( [] , {
            list, line ->
                List tokens = line.split(DELIMIT)
                Map map = [state: tokens[1], county: tokens[3], city: tokens[0]]
                list + map
        })
        def statesList = parseFileToMaps.groupBy([{ m -> m.state },{ m -> m.county }])
        def root = [state: "State short",county: "County"]
        def citiesList = []
        def stateCountyMap = [:]
        def foo = statesList.each {
            stateKey, Map countiesMap -> countiesMap.each {
                countyKey, List<Map> cityMapList -> cityMapList.each {
                    city ->
                        citiesList += city
                        def key = "${stateKey}|${countyKey}"
                        def parent = stateCountyMap.get( key )
                        parent = parent ?: [state: stateKey,county: countyKey]
                        stateCountyMap.put(key,parent)
                        city[(OrgAddressKey.Parent)] = parent
                        city[(OrgAddressKey.Child)] = city
                }
            }
        }
        expect:
        parseFileToMaps
        statesList
        statesList == foo
    }
}
