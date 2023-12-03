package mil.dtic.datafeed.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Configuration
@ConfigurationProperties("db")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DbSetting {

	private String connection;
	private String host;
	private int port;
}
