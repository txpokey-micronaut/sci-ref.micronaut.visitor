package sci.category.geovisit.contract

interface FactoryContract<T>{
    T newInstance( Map cfg )
    T build( Map cfg , Map bootstrap )
}