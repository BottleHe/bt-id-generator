package work.bottle.plugin.client;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import work.bottle.plugin.id.consumer.client.config.IdPublisherProperties;

@Configuration
@AutoConfiguration(before = {WebMvcAutoConfiguration.class})
@ConfigurationProperties(prefix = "bt-id", ignoreUnknownFields = true)
public class BtIdProperties extends IdPublisherProperties {
}
