package mil.dtic.datafeed.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

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
