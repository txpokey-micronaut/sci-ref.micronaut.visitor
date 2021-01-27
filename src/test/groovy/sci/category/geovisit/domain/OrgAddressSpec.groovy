package sci.category.geovisit.domain

import io.micronaut.test.extensions.spock.annotation.MicronautTest
import org.jgrapht.Graph
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge
import sci.category.geovisit.constant.OrgAddressKey
import spock.lang.Specification

@MicronautTest
class OrgAddressSpec extends Specification{

    void 'test *.save and *.all'() {
        given:
            Map map = [description: "sample organization", payload: [foo: "bar"]]
            OrgAddress p = new OrgAddress(map)
            p.save()
            List plist = OrgAddress.all
        expect:
            map
            plist
            plist.size() >= 1
    }

    void 'test non string key on payload'() {
        given:
        OrgAddress p = new OrgAddress([description: "root", payload: [(OrgAddressKey.NodeName): "0.0"]])
            p.save()
            List plist = OrgAddress.all
        expect:
            plist
            plist.size() >= 1
    }

    void 'save all scenario'() {
        given:
        List<OrgAddress> orgList = getVertexList()
        List plist = OrgAddress.saveAll(orgList)
        List plist2 = OrgAddress.all
        expect:
        plist
        plist.size() >= 1
        plist2
        plist2.size() >= 1
        plist.size() == plist2.size()
        def l = plist2.collect {
            OrgAddress v ->
                assert plist.contains(v.id)
        }
    }

    private List<OrgAddress> getVertexList() {
        def vertexList = List.of("0|0", "1|0", "1|1", "2|0")
        def orgList = getVertexList(vertexList)
        orgList
    }

    private List<OrgAddress> getVertexList(List<String> vertexList) {
        def orgList = vertexList.collect { v ->
            def map = [description: v, payload: [(OrgAddressKey.NodeName): v]]
            def next = new OrgAddress(map)
        }
        orgList
    }

    void 'graph construction outside of factory'() {
        given:
        Graph<OrgAddress, DefaultEdge> graph
                = new DefaultDirectedGraph<>(DefaultEdge.class);
        List<OrgAddress> orgList = getVertexList()
        OrgAddress.saveAll(orgList)
        List<OrgAddress> plist = OrgAddress.all
        plist.each {
            child ->
                def parentDescription = parseToParentDescription( child )
            def parent = OrgAddress.findByDescription(parentDescription)
            performParentChildRelationship(graph, parent, child)
        }
        expect:
        plist
        plist.size() >= 1
    }

    private performParentChildRelationship(Graph<OrgAddress, DefaultEdge> graph, OrgAddress parent, OrgAddress child) {
        if (!graph.containsVertex(parent)) {
            graph.addVertex(parent)
        } else {
            graph.addVertex(child)
        }
        graph.addEdge(parent, child)
    }

    def parseToParentDescription( OrgAddress child ) {
        final DELIMIT = "|"
        String childDescription = child.description
        List childEncodingAsList = childDescription.split(DELIMIT)
        def parentLevel = childEncodingAsList[0] as Integer
        parentLevel = (--parentLevel < 0) ? 0 : parentLevel
        String parentDescription = "${parentLevel}${DELIMIT}0"
    }

}
