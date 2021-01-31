package sci.category.geovisit.factory.supplier

import org.jgrapht.Graph
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge
import sci.category.geovisit.constant.FactoryKey
import sci.category.geovisit.constant.OrgAddressKey
import sci.category.geovisit.contract.FactoryContract
import sci.category.geovisit.contract.OrgTreeContract
import sci.category.geovisit.domain.OrgAddress

class OrgTreeFactory implements FactoryContract<OrgTreeContract>{

    static OrgTreeFactory newInstance(Map config) {
        OrgTreeFactory factory = new OrgTreeFactory(config)
        FactoryContract<OrgTreeContract> contract = factory.newInstance()
    }

    static OrgTreeContract build(Map config) {
        FactoryContract<OrgTreeContract> factory = OrgTreeFactory.newInstance(config)
        OrgTreeContract contract = factory.build()
        contract
    }
    //
    private OrgTreeContract contract_
    private Map configure_
    private List<Map> bootstrap_
    private Graph<OrgAddress, DefaultEdge> graph_
            = new DefaultDirectedGraph<>(DefaultEdge.class)

    private OrgTreeFactory(config) {
        configure_ = config
        bootstrap_ = config[FactoryKey.Bootstrap]
        assert bootstrap_
    }
    FactoryContract<OrgTreeContract> newInstance() {
        contract_ = new OrgTreeContract(){
            private Map configure = configure_
            private List<Map> bootstrap = bootstrap_
            private Graph<OrgAddress, DefaultEdge> graph = graph_

            @Override
            Graph get() {
                return graph
            }
        }
        this
    }

    @Override
    OrgTreeContract build() {
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
