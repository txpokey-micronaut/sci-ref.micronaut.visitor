package sci.category.geovisit.domain

import io.micronaut.test.extensions.spock.annotation.MicronautTest
import spock.lang.Specification

@MicronautTest
class TempMapSpec extends Specification{

    void 'test temp.save and *.all'() {
        given:
            Map payload = [firstName: "bob", lastName: "mak", isCool: true]
            Map map = [ description : "this is a test" , payload : payload ]
            TempMap p = new TempMap(map)
            p.save()
            List plist = TempMap.all
        expect:
            map
            plist
            plist.size() == 1
    }
}
