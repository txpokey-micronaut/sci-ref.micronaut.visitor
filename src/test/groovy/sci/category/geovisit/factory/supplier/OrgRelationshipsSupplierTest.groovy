package sci.category.geovisit.factory.supplier

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
        expect:
        parseFileToMaps
    }
}
//String childDescription = child.description
//List childEncodingAsList = childDescription.split(DELIMIT)

