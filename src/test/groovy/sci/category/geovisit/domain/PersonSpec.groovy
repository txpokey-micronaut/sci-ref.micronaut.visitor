package sci.category.geovisit.domain

import io.micronaut.test.extensions.spock.annotation.MicronautTest
import spock.lang.Specification

@MicronautTest
class PersonSpec extends Specification{

    void 'test person.save and Person.all'() {
        given:
            Map map = [firstName: "bob", lastName: "mak", isCool: true]
            Person p = new Person(map)
            p.save()
            List plist = Person.all
        expect:
            map
            plist
            plist.size() == 1
    }
}
