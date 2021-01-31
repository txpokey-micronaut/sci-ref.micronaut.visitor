package sci.category.geovisit.contract

interface FactoryContract<T> extends BuildContract<OrgTreeContract>{
    FactoryContract<T> newInstance( Map cfg , List<Map> bootstrap )
//    T newInstance( Map cfg  )
}