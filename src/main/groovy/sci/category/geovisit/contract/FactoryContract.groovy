package sci.category.geovisit.contract

interface FactoryContract<T>{
    T newInstance( Map cfg , List<Map> bootstrap )
//    T newInstance( Map cfg  )
}