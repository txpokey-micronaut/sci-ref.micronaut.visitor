package sci.category.geovisit.dsl

class DslBuilder{
    static String getWellFormedClosure( String text ) {
        def wellFormed = """
        Closure asText = {
            ${text}
        }
        """
    }
}
