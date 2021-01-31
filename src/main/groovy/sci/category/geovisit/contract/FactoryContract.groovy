package sci.category.geovisit.contract

interface FactoryContract<T> extends BuildContract<OrgTreeContract>{
    FactoryContract<T> newInstance()
//    T newInstance( Map cfg  )
}