package sci.category.geovisit.contract

interface FactoryContract<T> extends BuildContract<T>{
    FactoryContract<T> newInstance()
//    T newInstance( Map cfg  )
}