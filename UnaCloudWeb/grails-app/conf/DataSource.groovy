dataSource {
    pooled = true
	// Other database parameters..
	properties {
	   maxActive = 50
	   maxIdle = 25
	   minIdle = 5
	   initialSize = 5
	   minEvictableIdleTimeMillis = 1800000
	   timeBetweenEvictionRunsMillis = 1800000
	   maxWait = 10000
	}
    driverClassName = "com.mysql.jdbc.Driver"
	dialect = "org.hibernate.dialect.MySQL5InnoDBDialect"
	
}
hibernate {
	show_sql=false
    cache.use_second_level_cache = true
    cache.use_query_cache = false
    cache.region.factory_class = 'org.hibernate.cache.ehcache.EhCacheRegionFactory'
}
Properties prop = new Properties();
String propFileName = "config.properties";
InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
prop.load(inputStream);
// environment specific settings
environments {
    development {
        dataSource {
			//using properties file
			username = prop.getProperty("dev.username");
			password = prop.getProperty("dev.password");
			dbCreate = prop.getProperty("dev.dbCreate");// one of 'create', 'create-drop', 'update', 'validate', ''
			url = prop.getProperty("dev.url").replace('\\', '');
        }
    }
    test {
        dataSource {
			username = prop.getProperty("test.username");
			password = prop.getProperty("test.password");
            dbCreate = prop.getProperty("test.dbCreate");
            url = prop.getProperty("test.url").replace('\\', '');
        }
    }
    production {
        dataSource {
			username = prop.getProperty("prod.username");
			password = prop.getProperty("prod.password");
            dbCreate = prop.getProperty("prod.dbCreate");
            url = prop.getProperty("prod.url").replace('\\', '');
            pooled = true
            properties {
               maxActive = -1
               minEvictableIdleTimeMillis=1800000
               timeBetweenEvictionRunsMillis=1800000
               numTestsPerEvictionRun=3
               testOnBorrow=true
                testWhileIdle=true
               testOnReturn=true
               validationQuery="SELECT 1"
            }
        }
    }
}
