package sci.category.geovisit.dsl

class TemperatureModel{
    def measure(@DelegatesTo(TemperatureBuilder) Closure cl) {
        def builder = new TemperatureBuilder()
        def code = cl.rehydrate(builder, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
    }
}
