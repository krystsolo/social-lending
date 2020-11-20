package pl.fintech.dragons.dragonslending.paymentplatformmock.client;

import feign.codec.ErrorDecoder;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.cloud.commons.httpclient.HttpClientConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@EnableFeignClients(clients = {
        BankClient.class
})
@Import({
        FeignAutoConfiguration.class,
        HttpClientConfiguration.class,
        HttpMessageConvertersAutoConfiguration.class,
        PropertySourcesPlaceholderConfigurer.class
})
public class BankClientConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new ApiErrorDecoder();
    }
}
