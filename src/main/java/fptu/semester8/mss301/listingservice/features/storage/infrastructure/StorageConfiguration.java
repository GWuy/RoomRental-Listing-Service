package fptu.semester8.mss301.listingservice.features.storage.infrastructure;

import fptu.semester8.mss301.listingservice.features.storage.application.ImageProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({StorageProperties.class, ImageProperties.class})
public class StorageConfiguration {
}
