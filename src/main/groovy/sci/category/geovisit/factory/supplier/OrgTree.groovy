package sci.category.geovisit.factory.supplier

import sci.category.geovisit.contract.FactoryContract
import sci.category.geovisit.domain.OrgAddress

class OrgTree implements FactoryContract<OrgAddress>{
    @Override
    OrgAddress newInstance(Map cfg) {
        return null
    }

    @Override
    static OrgAddress build(Map cfg, Map bootstrap) {
        return null
    }
}
