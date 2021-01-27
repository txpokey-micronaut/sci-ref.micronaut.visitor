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
    static OrgTreeContract newInstance(Map config){
        List<Map> bootstrap = config[ FactoryKey.Bootstrap ]
        assert bootstrap
        OrgTreeFactory factory = new OrgTreeFactory()
        OrgTreeContract contract = factory.newInstance(config,bootstrap)
    }
    static OrgTreeContract build(Map config){
        List<Map> bootstrap = config[ FactoryKey.Bootstrap ]
        assert bootstrap
        OrgTreeContract factory = OrgTreeFactory.newInstance(config)
        OrgTreeContract contract = factory.build()
        contract
    }
    OrgTreeContract newInstance(Map _configure, List<Map> _bootstrap) {
        OrgTreeContract contract = new OrgTreeContract(){
            private Map configure = _configure
            private List<Map> bootstrap = _bootstrap
            private Graph<OrgAddress, DefaultEdge> graph
                    = new DefaultDirectedGraph<>(DefaultEdge.class);

            @Override
            Graph get() {
                return graph
            }

            @Override
            OrgTreeContract build() {
                configure.root = configure[((OrgAddressKey.Root))]
                bootstrap.stream().each { m ->
                    m.parent = m[(OrgAddressKey.Parent)]
                    m.child = m[(OrgAddressKey.Child)]
                    guardedAddVertex(m.parent)
                    guardedAddVertex(m.child)
                    graph.addEdge(m.parent, m.child)
                }
                this
            }
            private void guardedAddVertex(OrgAddress oa) {
                if (!graph.containsVertex(oa)) {
                    graph.addVertex(oa)
                }
            }
        }
    }
}
