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

    OrgAddress getRoot() {
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
            bootstrapData.each { m ->
                def parent = m.payload[(OrgAddressKey.Parent)]
                def child = m.payload[(OrgAddressKey.Child)]
                guardedAddVertex(supplierGraph, parent)
                guardedAddVertex(supplierGraph, child)
                supplierGraph.addEdge(parent, child)
            }
            return supplier
        }

        private void guardedAddVertex(Graph supplierGraph, OrgAddress oa) {
            if (!supplierGraph.containsVertex(oa)) {
                supplierGraph.addVertex(oa)
            }
        }
        private Map buildConfig
        private List<OrgAddress> bootstrapData
    }
}
