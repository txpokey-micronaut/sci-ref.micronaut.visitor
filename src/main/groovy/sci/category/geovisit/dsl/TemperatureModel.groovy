package sci.category.geovisit.dsl

class TemperatureModel{
    private Map config = [:]

    TemperatureModel(Map _config) {
        config.putAll(_config)
    }

    def measure(@DelegatesTo(TemperatureBuilder) Closure cl) {
        def builder = new TemperatureBuilder(config)
        def code = cl.rehydrate(builder, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
    }
}
