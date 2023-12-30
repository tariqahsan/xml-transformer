package mil.dtic.datafeed.config;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "datafeed.namespace")
public class DatafeedProperties {
	
	private Map<String, String> mdr;
	private Map<String, String> feed;
	private Map<String, String> meta;
	private Map<String, String> attribute;
	private String prefix;
	private List<String> nodenamelist;

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public Map<String, String> getFeed() {
		return feed;
	}

	public void setFeed(Map<String, String> feed) {
		this.feed = feed;
	}
	
	public Map<String, String> getMeta() {
		return meta;
	}

	public void setMeta(Map<String, String> meta) {
		this.meta = meta;
	}


	public Map<String, String> getAttribute() {
		return attribute;
	}

	public void setAttribute(Map<String, String> attribute) {
		this.attribute = attribute;
	}

	public Map<String, String> getMdr() {
		return mdr;
	}

	public void setMdr(Map<String, String> mdr) {
		this.mdr = mdr;
	}

	public List<String> getNodenamelist() {
		return nodenamelist;
	}

	public void setNodenamelist(List<String> nodenamelist) {
		this.nodenamelist = nodenamelist;
	}
	
}
