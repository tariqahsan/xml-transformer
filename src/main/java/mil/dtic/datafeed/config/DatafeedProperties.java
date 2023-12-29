package mil.dtic.datafeed.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "datafeed.namespace")
public class DatafeedProperties {
	
	private Map<String, String> mdr;

	public Map<String, String> getMdr() {
		return mdr;
	}

	public void setMdr(Map<String, String> mdr) {
		this.mdr = mdr;
	}
	
}
