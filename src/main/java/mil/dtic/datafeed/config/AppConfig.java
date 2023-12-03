package mil.dtic.datafeed.config;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {
	
	@Value("${custom.property}")
    private String customProperty;
	
	@Value("A static value")
	private String staticValue;
	
	@Value("${my.list.value}")
	private List<String> listPropertyValue;
	
	@Value("#{${db.connection}}")
	private Map<String, String> dbConnection;
	
	@Autowired
	private DbSetting dbSetting;

    // Use customProperty in your code
	@Bean
	public String getParameterValue() {
		System.out.println("Custom Property: " + customProperty);
		System.out.println("Static Value: " + staticValue);
		System.out.println("List Value: " + listPropertyValue);
		System.out.println("Hash Value: " + dbConnection);
		System.out.println("DbSetting connection: " + dbSetting.getConnection());
		System.out.println("DbSetting host: " + dbSetting.getHost());
		System.out.println("DbSetting port: " + dbSetting.getPort());
		
		return customProperty;
	}
//	@Bean
//	  public MessageSource messageSource() {
//	      ReloadableResourceBundleMessageSource bean = new ReloadableResourceBundleMessageSource();
//	      bean.setBasename("classpath:messages");
//	      bean.setDefaultEncoding("UTF-8");
//	      return bean;
//	  }

}
