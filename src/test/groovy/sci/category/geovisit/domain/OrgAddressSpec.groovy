package sci.category.geovisit.domain

import io.micronaut.test.extensions.spock.annotation.MicronautTest
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
        def mapList = []
        def map = [description: "root", payload: [(OrgAddressKey.NodeName): "0.0"]]
        mapList.add(map)
        def vertexList = List.of( "1.0", "1.1", "2.0")
        vertexList.each { v ->
            def next = [description: v, payload: [(OrgAddressKey.NodeName): v]]
            mapList.add(next)
        }
        def orgList = []
        mapList.each { m ->
            def next = new OrgAddress(m)
            orgList.add(next)
        }
        List plist = OrgAddress.saveAll(orgList)
        List plist2 = OrgAddress.all
        expect:
        plist
        plist.size() >= 1
        plist2
        plist2.size() >= 1
        plist.size() == plist2.size()
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

}
