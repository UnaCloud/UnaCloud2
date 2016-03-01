import java.io.FileInputStream;

import unacloud.share.utils.EnvironmentManager;

import com.losandes.utils.UnaCloudConstants;

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
	String propFileName = EnvironmentManager.getConfigPath()+UnaCloudConstants.FILE_CONFIG;
	println propFileName
	FileInputStream inputStream = new FileInputStream(propFileName);
	prop.load(inputStream);
// environment specific settings
environments {
    development {
        dataSource {
			//using properties file
			username = prop.getProperty("dev_username");
			password = prop.getProperty("dev_password");
			dbCreate = 'update'// one of 'create', 'create-drop', 'update', 'validate', ''
			url = prop.getProperty("dev_url").replace('\\', '');
        }
    }
    test {
        dataSource {
			username = prop.getProperty("test_username");
			password = prop.getProperty("test.password");
            dbCreate = "create-drop"
            url = prop.getProperty("test_url").replace('\\', '');
        }
    }
    production {
        dataSource {
			username = prop.getProperty(UnaCloudConstants.DB_USERNAME);
			password = prop.getProperty(UnaCloudConstants.DB_PASS);
            dbCreate = "update";
            url = 'jdbc:mysql://'+prop.getProperty(UnaCloudConstants.DB_IP)+':'+prop.getProperty(UnaCloudConstants.DB_PORT)+'/'+prop.getProperty(UnaCloudConstants.DB_NAME)+'?useUnicode=yes&characterEncoding=UTF-8&autoReconnect=true'
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
