package sci.category.geovisit.contract

interface FactoryContract<T> {
    T newInstance()
    T newInstance( Map cfg  )
}