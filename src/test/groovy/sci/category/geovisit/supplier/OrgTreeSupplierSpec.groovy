package sci.category.geovisit.supplier

import io.micronaut.test.extensions.spock.annotation.MicronautTest
import sci.category.geovisit.constant.FactoryKey
import sci.category.geovisit.constant.OrgAddressKey
import sci.category.geovisit.domain.OrgAddress
import spock.lang.Specification

@MicronautTest(packages="sci.category.geovisit")
class OrgTreeSupplierSpec extends Specification{

    void 'test persist root via gorm'() {
        given:
        OrgAddress p = buildRootMember()
        p.save()
        List plist = OrgAddress.all
        Set set = new HashSet( plist )
        expect:
        plist
        plist.size() >= 1
        set.contains(p)
    }

    def "test static newInstance"() {
        given:
        OrgAddress root = buildRootMember()
        List<Map> build = [[(OrgAddressKey.Root): root]]
        Map config = [(OrgAddressKey.Root): root, (FactoryKey.Bootstrap): build]
        when:
        def builder = OrgTreeSupplier.Builder.newInstance(config)
        then:
        builder
    }

    def "test org relationships fed into org tree supplier"() {
        given:
        OrgRelationshipSupplier orgRelationshipsSupplier = getOrgRelationshipsSupplier()
        def root = orgRelationshipsSupplier.root
        def bootstrapData = orgRelationshipsSupplier.get()
        def config = [(OrgAddressKey.Root): root, (FactoryKey.Bootstrap): bootstrapData]
        when:
        def builder = OrgTreeSupplier.Builder.newInstance(config)
        def orgTreeSupplier = builder.build()
        then:
        orgTreeSupplier
        orgTreeSupplier.get()
        orgTreeSupplier.getRoot()
        true
    }

    private getOrgRelationshipsSupplier() {
        def root = [:]
        List<Map> build = [[(OrgAddressKey.Root): root]]
        Map config = [(OrgAddressKey.Root): root, (FactoryKey.Bootstrap): build]
        def orgRelationshipsBuilder = OrgRelationshipSupplier.Builder.newInstance(config)
        assert orgRelationshipsBuilder
        def orgRelationshipsSupplier = orgRelationshipsBuilder.build()
    }

    // TODO regression when changed to use OrgAddress instead of Map on suppliers
//    def "test static build with fake data as persisted vertex and parent child relationships"() {
//        given:
//        List<Map> build = buildListOfFakeParentChildRelationships()
//        OrgAddress root = build[0][(OrgAddressKey.Parent)]
//        Map config = [(OrgAddressKey.Root): root, (FactoryKey.Bootstrap): build]
//        when:
//        def builder = OrgTreeSupplier.Builder.newInstance(config)
//        def contract = builder.build()
//        then:
//        contract
//
//        def graph = contract.get()
//        graph
//    }

    private def buildListOfFakeParentChildRelationships() {
        OrgAddress root = new OrgAddress(getMapToInitializeOrgAddressTestCase("root","0.0"))
        OrgAddress z1_0 = new OrgAddress(getMapToInitializeOrgAddressTestCase("1.0","1.0"))
        OrgAddress z1_1 = new OrgAddress(getMapToInitializeOrgAddressTestCase("1.1","1.1"))
        OrgAddress z2_0 = new OrgAddress(getMapToInitializeOrgAddressTestCase("2.0","2.0"))
        List saveThese = List.of(root,z1_0,z1_1,z2_0)
        def result = OrgAddress.saveAll(saveThese)
        Map m_root = mapParentChildRelationship(root,root)
        Map m1_0 = mapParentChildRelationship(root,z1_0)
        Map m1_1 = mapParentChildRelationship(root,z1_1)
        Map m2_0 = mapParentChildRelationship(z1_0,z2_0)
        List.of(m_root,m1_0,m1_1,m2_0)
    }

    private mapParentChildRelationship(OrgAddress parent,OrgAddress child) {
        [(OrgAddressKey.Parent): parent, (OrgAddressKey.Child): child]
    }

    private getMapToInitializeOrgAddressTestCase(def description, def parentChildLabel) {
        [description: "${description}", payload: [(OrgAddressKey.NodeName): "${parentChildLabel}"]]
    }

    private buildRootMember() {
        Map rootMap = [description: "root", payload: [(OrgAddressKey.NodeName): "0.0"]]
        OrgAddress root = new OrgAddress(rootMap)
    }

}
