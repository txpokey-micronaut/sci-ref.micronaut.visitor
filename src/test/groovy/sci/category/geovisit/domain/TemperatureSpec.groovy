package sci.category.geovisit.domain

import io.micronaut.test.extensions.spock.annotation.MicronautTest
import spock.lang.Specification

@MicronautTest
class TemperatureSpec extends Specification{

    void 'test temp.save and *.all'() {
        given:
            Map payload = [firstName: "bob", lastName: "mak", isCool: true]
            Map map = [ description : "this is a test" , payload : payload ]
            Temperature p = new Temperature(map)
            p.save()
            List plist = Temperature.all
        expect:
            map
            plist
            plist.size() == 1
    }
}
