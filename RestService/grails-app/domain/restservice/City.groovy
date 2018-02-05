package restservice

import grails.rest.Resource
import groovy.transform.ToString
import groovy.transform.EqualsAndHashCode

/**
 * City
 * A domain class describes the data object and it's mapping to the database
 */

@ToString(includeNames = true, includeFields = true, excludes = 'dateCreated,lastUpdated,metaClass')
@EqualsAndHashCode
class City {

    /* Default (injected) attributes of GORM */
    Long    id
    Long    version

    /* Automatic timestamping of GORM */
    Date    dateCreated
    Date    lastUpdated


    String cityName
    String postalCode
    String countryCode // either iso2 or iso3

    static constraints = {
        postalCode blank:false, nullable:false
        cityName blank:false, nullable:false
        countryCode minSize:2, maxSize:3, blank:false, nullable:false, matches: "[A-Z]+"
    }
}
