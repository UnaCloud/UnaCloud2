package restservice

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString(includeNames = true, includeFields = true, excludes = 'dateCreated,lastUpdated,metaClass')
@EqualsAndHashCode
class RestAuthenticationToken {

    String token
    String username

    Date dateCreated

    static mapping = {
        version false
    }

    static constraints = {
        dateCreated nullable: true
    }
}

