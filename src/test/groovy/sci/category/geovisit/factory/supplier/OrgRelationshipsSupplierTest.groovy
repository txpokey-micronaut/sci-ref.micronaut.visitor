package sci.category.geovisit.factory.supplier

import sci.category.geovisit.constant.OrgAddressKey
import spock.lang.Specification

class OrgRelationshipsSupplierTest extends Specification{
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
        def root = statesList[("State short")]
        def foo = statesList.each {
            stateKey, Map countiesMap -> countiesMap.each {
                countyKey, List<Map> cityMapList -> cityMapList.each {
                    city ->
                        city[(OrgAddressKey.Parent)] = [state: stateKey,county: countyKey]
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
