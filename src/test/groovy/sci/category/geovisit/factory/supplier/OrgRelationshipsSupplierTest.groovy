package sci.category.geovisit.factory.supplier

import spock.lang.Specification

class OrgRelationshipsSupplierTest extends Specification{
    final static DELIMIT = "[|]"

    def "test build"() {
        given:
        def foo = []
        def listLines = new File("src/main/resources/citiesUsa.csv").readLines()
        def bar = listLines.each {
            String line ->
                List tokens = line.split(DELIMIT)
                Map map = [state: tokens[1], county: tokens[3], city: tokens[0]]
                foo.add(map)
        }

//        when:
//        // TODO implement stimulus
//        then:
//        // TODO implement assertions
        expect:
        listLines
        foo
    }
}
//String childDescription = child.description
//List childEncodingAsList = childDescription.split(DELIMIT)

