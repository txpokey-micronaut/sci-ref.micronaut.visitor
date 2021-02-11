package sci.category.geovisit.dsl.example

import spock.lang.Specification

class EmailModelTest extends Specification{
    void "Email"() {
        given:
        EmailModel emailModel = new EmailModel()
        Closure c = {
            from 'dsl-guru@mycompany.com'
            to 'john.doe@waitaminute.com'
            subject 'Dune 2021 is coming!'
            body {
                p 'Bring Me Spice!'
            }
        }
        emailModel.email c
    }
}
