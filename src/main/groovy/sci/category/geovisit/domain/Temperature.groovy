package sci.category.geovisit.domain

import grails.gorm.annotation.Entity
import groovy.transform.CompileStatic

import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@CompileStatic
@Entity
class Temperature{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id
    String description
    Map payload = new HashMap()
    Temperature(Map cfg) {
        description = cfg.description
        payload = cfg.payload as Map
    }
}
