import grails.util.Environment
import restservice.*



class BootStrap {
    def init = { servletContext ->

        def result = '################## running in UNCLEAR mode.'
        println "Application starting ... "
        switch (Environment.current) {
            case Environment.DEVELOPMENT:
                result = 'now running in DEV mode.'
                seedTestData()
                break;
            case Environment.TEST:
                result = 'now running in TEST mode.'
                break;
            case Environment.PRODUCTION:
                result = 'now running in PROD mode.'
                seedProdData()
                break;
        }
        println "current environment: $Environment.current"
        println "$result"
    }

    def destroy = {
        println "Application shutting down... "
    }

    private void seedTestData() {
        def city = null
        println "Start loading cities into database"
        city = new City(cityName: 'Munich', postalCode: "81927", countryCode: 'DE')
        assert city.save(failOnError:true, flush:true, insert: true)
        city.errors = null

        city = new City(cityName: 'Berlin', postalCode: "10115", countryCode: 'DE')
        assert city.save(failOnError:true, flush:true, insert: true)
        city.errors = null

        assert City.count == 2;
        println "Finished loading $City.count cities into database"


        println "Creating users and roles"
        Role role = new Role(authority: "ROLE_USER").save(failOnError:true)
        User user = new User(username: "me", password: "password").save(failOnError:true)
        UserRole.create(user, role)
    }
}