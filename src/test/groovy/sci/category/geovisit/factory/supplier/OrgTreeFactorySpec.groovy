package sci.category.geovisit.factory.supplier

import io.micronaut.test.extensions.spock.annotation.MicronautTest
import sci.category.geovisit.constant.FactoryKey
import sci.category.geovisit.constant.OrgAddressKey
import sci.category.geovisit.contract.OrgTreeContract
import sci.category.geovisit.domain.OrgAddress
import sci.category.geovisit.service.OrgAddressService
import spock.lang.Specification

import javax.inject.Inject

@MicronautTest
class OrgTreeFactorySpec extends Specification{
    @Inject OrgAddressService orgAddressService
    def "test static newInstance"() {
        given:
        OrgAddress root = buildRootMember()
        List<Map> build = [[(OrgAddressKey.Root): root]]
        Map config = [(OrgAddressKey.Root): root, (FactoryKey.Bootstrap): build]
        when:
        def contract = OrgTreeFactory.newInstance(config)
        then:
        contract
    }

    def "test static build"() {
        given:
        List<Map> build = buildListOfParentChildRelationships()
        OrgAddress root = build[0][(OrgAddressKey.Parent)]
        Map config = [(OrgAddressKey.Root): root, (FactoryKey.Bootstrap): build]
        when:
        OrgTreeContract contract = OrgTreeFactory.build(config)
        then:
        contract
    }

    private def buildListOfParentChildRelationships() {
        OrgAddress root = new OrgAddress([description: "root", payload: [(OrgAddressKey.NodeName): "0.0"]])
//        OrgAddress root = new OrgAddress([description: "root", payload: [(OrgAddressKey.NodeName.name()): "0.0"]])
        OrgAddress z1_0 = new OrgAddress([description: "1.0", payload: [(OrgAddressKey.NodeName): "1.0"]])
        OrgAddress z1_1 = new OrgAddress([description: "1.1", payload: [(OrgAddressKey.NodeName): "1.1"]])
        OrgAddress z2_0 = new OrgAddress([description: "2.0", payload: [(OrgAddressKey.NodeName): "2.0"]])
        Map m_root = [(OrgAddressKey.Parent): root, (OrgAddressKey.Child): root]
        Map m1_0 = [(OrgAddressKey.Parent): root, (OrgAddressKey.Child): z1_0]
        Map m1_1 = [(OrgAddressKey.Parent): root, (OrgAddressKey.Child): z1_1]
        Map m2_0 = [(OrgAddressKey.Parent): z1_0, (OrgAddressKey.Child): z2_0]
        List saveThese = List.of(root,z1_0,z1_1,z2_0)
//        root.save()
//        OrgAddress.save(root)
//        orgAddressService.save(root)
//        def result = OrgAddress.saveAll(saveThese)
        List.of(m_root,m1_0,m1_1,m2_0)
    }

    private buildRootMember() {
        Map rootMap = [description: "root", payload: [(OrgAddressKey.NodeName): "0.0"]]
        OrgAddress root = new OrgAddress(rootMap)
    }

    private buildRootMember(LinkedHashMap<String, Serializable> rootMap) {
        OrgAddress root = new OrgAddress(rootMap)
    }
}
