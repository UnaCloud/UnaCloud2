import java.io.FileInputStream;
import unacloud.share.utils.EnvironmentManager;
import com.losandes.utils.ConfigurationReader
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
ConfigurationReader reader = new ConfigurationReader(EnvironmentManager.getConfigPath()+UnaCloudConstants.FILE_CONFIG)
// environment specific settings
environments {
    development {
        dataSource {
			//using properties file
			username = reader.getStringVariable("dev_username");
			password = reader.getStringVariable("dev_password");
			dbCreate = 'update'// one of 'create', 'create-drop', 'update', 'validate', ''
			url = reader.getStringVariable("dev_url")!=null?reader.getStringVariable("dev_url").replace('\\', ''):'';
        }
    }
    test {
        dataSource {
			username = reader.getStringVariable("test_username");
			password = reader.getStringVariable("test_password");
            dbCreate = "update"
            url = reader.getStringVariable("test_url")!=null?reader.getStringVariable("test_url").replace('\\', ''):'';
        }
    }
    production {
        dataSource {
			username = reader.getStringVariable(UnaCloudConstants.DB_USERNAME);
			password = reader.getStringVariable(UnaCloudConstants.DB_PASS);
            dbCreate = "update";
            url = 'jdbc:mysql://'+reader.getStringVariable(UnaCloudConstants.DB_IP)+':'+reader.getStringVariable(UnaCloudConstants.DB_PORT)+'/'+reader.getStringVariable(UnaCloudConstants.DB_NAME)+'?useUnicode=yes&characterEncoding=UTF-8&autoReconnect=true'
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
