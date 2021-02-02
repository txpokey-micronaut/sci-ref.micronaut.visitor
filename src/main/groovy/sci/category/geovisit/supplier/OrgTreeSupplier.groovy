package sci.category.geovisit.supplier

import org.jgrapht.Graph
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge
import sci.category.geovisit.constant.FactoryKey
import sci.category.geovisit.constant.OrgAddressKey
import sci.category.geovisit.contract.BuildContract
import sci.category.geovisit.contract.SupplierContract
import sci.category.geovisit.domain.OrgAddress

class OrgTreeSupplier implements SupplierContract<Graph>{
    private Graph<OrgAddress, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class)
    private Builder builder
    private Map root

    @Override
    Graph get() {
        return graph
    }

    Map getRoot() {
        return builder.buildConfig.root
    }
    private OrgTreeSupplier() {}

    private OrgTreeSupplier(Builder _builder) {
        this.builder = _builder
    }

    static class Builder implements BuildContract<OrgTreeSupplier>{

        static Builder newInstance(Map cfg) {
            return new Builder(cfg)
        }

        private Builder(cfg) {
            buildConfig = cfg
            bootstrapData = buildConfig[FactoryKey.Bootstrap]
            assert bootstrapData
        }

        @Override
        OrgTreeSupplier build() {
            OrgTreeSupplier supplier = new OrgTreeSupplier(this)
            buildConfig.root = buildConfig[((OrgAddressKey.Root))]
            final def supplierGraph = supplier.get()
            bootstrapData.stream().each { m ->
                m.parent = m[(OrgAddressKey.Parent)]
                m.child = m[(OrgAddressKey.Child)]
                guardedAddVertex(supplierGraph, m.parent)
                guardedAddVertex(supplierGraph, m.child)
                supplierGraph.addEdge(m.parent, m.child)
            }
            return supplier
        }

        private void guardedAddVertex(Graph supplierGraph, OrgAddress oa) {
            if (!supplierGraph.containsVertex(oa)) {
                supplierGraph.addVertex(oa)
            }
        }
        private Map buildConfig
        private List<Map> bootstrapData
    }
}
