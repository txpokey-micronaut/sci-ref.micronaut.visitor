package sci.category.geovisit.domain

import grails.gorm.annotation.Entity
import groovy.transform.CompileStatic

import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@CompileStatic
@Entity
class OrgAddress{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id
    String description
    Map payload = new HashMap()
//    @NotNull
//    @Size(min = 5, max = 50)
//    @Size(min = 5, max = 50)
//    @NotNull
}
