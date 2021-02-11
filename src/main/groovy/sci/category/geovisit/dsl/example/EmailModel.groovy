package sci.category.geovisit.dsl.example

class EmailModel{
    def email(@DelegatesTo(EmailBuilder) Closure cl) {
        def email = new EmailBuilder()
        def code = cl.rehydrate(email, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
    }
}
