package restservice

import grails.rest.RestfulController
import org.springframework.security.access.annotation.Secured

@Secured("ROLE_USER")
class RestAuthenticationTokenController extends RestfulController<RestAuthenticationToken> {
    static responseFormats = ['json', 'xml']
    RestAuthenticationTokenController() {
        super(RestAuthenticationToken)
    }
    def index(Integer max)
    {
        println("You can update get all function here")
        super.index(max)
    }
    def show()
    {
        println("You can update get function here")
        super.show()
    }
    def save()
    {
        println("You can update post function here")
        super.save()
    }
    def update()
    {
        println("You can update put and patch functions here")
        super.update()
    }
    def delete()
    {
        println("You can update delete functions here")
        super.delete()
    }
}
