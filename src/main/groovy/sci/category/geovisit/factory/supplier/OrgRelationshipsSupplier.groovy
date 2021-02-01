package sci.category.geovisit.factory.supplier

import org.jgrapht.Graph
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge
import sci.category.geovisit.constant.FactoryKey
import sci.category.geovisit.constant.OrgAddressKey
import sci.category.geovisit.contract.FactoryContract
import sci.category.geovisit.contract.OrgRelationshipContract
import sci.category.geovisit.domain.OrgAddress

class OrgRelationshipsSupplier implements FactoryContract<OrgRelationshipContract>{

    static OrgRelationshipsSupplier newInstance(Map config) {
        OrgRelationshipsSupplier factory = new OrgRelationshipsSupplier(config)
        FactoryContract<OrgRelationshipContract> contract = factory.newInstance()
    }

    static OrgRelationshipContract build(Map config) {
        FactoryContract<OrgRelationshipContract> factory = OrgRelationshipsSupplier.newInstance(config)
        OrgRelationshipContract contract = factory.build()
        contract
    }
    //
    private OrgRelationshipContract contract_
    private Map configure_
    private List<Map> bootstrap_
    private Graph<OrgAddress, DefaultEdge> graph_
            = new DefaultDirectedGraph<>(DefaultEdge.class)

    private OrgRelationshipsSupplier(config) {
        configure_ = config
//        bootstrap_ = config[FactoryKey.Bootstrap]
//        assert bootstrap_
    }
    FactoryContract<OrgRelationshipContract> newInstance() {
        contract_ = new OrgRelationshipContract(){
            private Map configure = configure_
            private List<Map> bootstrap = bootstrap_

            @Override
            Map get() {
                return bootstrap
            }
        }
        this
    }
//    Adjuntas|PR|Puerto Rico|ADJUNTAS|URB San Joaquin
//    City|State short|State full|County|City alias

    @Override
    OrgRelationshipContract build() {
        final DELIMIT = "[|]"

        def parseFileToMaps = new File("src/main/resources/citiesUsa.csv").readLines().inject( [] , {
            list, line ->
                List tokens = line.split(DELIMIT)
                Map map = [state: tokens[1], county: tokens[3], city: tokens[0]]
                list + map
        })
        def statesList = parseFileToMaps.groupBy([{ m -> m.state },{ m -> m.county }])

        this.contract_
    }
    private OrgRelationshipContract build0() {
        configure_.root = configure_[((OrgAddressKey.Root))]
        bootstrap_.stream().each { m ->
            m.parent = m[(OrgAddressKey.Parent)]
            m.child = m[(OrgAddressKey.Child)]
            guardedAddVertex(m.parent)
            guardedAddVertex(m.child)
            graph_.addEdge(m.parent, m.child)
        }
        this.contract_
    }

    private void guardedAddVertex(OrgAddress oa) {
        if (!graph_.containsVertex(oa)) {
            graph_.addVertex(oa)
        }
    }
}
