package sci.category.geovisit.dsl.example

class EmailBuilder{
    void from(String from) { println "From: $from"}
    void to(String... to) { println "To: $to"}
    void subject(String subject) { println "Subject: $subject"}
    void body(Closure body) {
        def bodySpec = new BodyBuilder()
        def code = body.rehydrate(bodySpec, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
    }
    class BodyBuilder{
        void p(String from) { println "Body: $from"}
    }
}
