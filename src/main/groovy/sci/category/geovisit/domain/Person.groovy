package sci.category.geovisit.domain

import grails.gorm.annotation.Entity
import groovy.transform.CompileStatic

import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@CompileStatic
@Entity
class Person{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    Long id
//    @NotNull
//    @Size(min = 5, max = 50)
    String firstName
//    @Size(min = 5, max = 50)
    String lastName
//    @NotNull
    Boolean isCool = false
}
