package sci.category.geovisit.domain

import io.micronaut.test.extensions.spock.annotation.MicronautTest
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
}
