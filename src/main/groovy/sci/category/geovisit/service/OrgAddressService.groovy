package sci.category.geovisit.service

import grails.gorm.services.Service
import groovy.transform.CompileStatic
import sci.category.geovisit.domain.OrgAddress

import javax.validation.constraints.NotNull

@CompileStatic
@Service(OrgAddress)
abstract class OrgAddressService{
    abstract int count()
    abstract List<OrgAddress> findAll()
    abstract List<OrgAddress> findAll(Map args)
    abstract OrgAddress find(@NotNull Long id)
    abstract OrgAddress save(OrgAddress person)
    abstract OrgAddress delete(@NotNull Long id)
}
