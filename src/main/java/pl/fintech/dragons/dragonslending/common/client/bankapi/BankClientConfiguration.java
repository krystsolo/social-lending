package pl.fintech.dragons.dragonslending.common.client.bankapi;

import feign.auth.BasicAuthRequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.commons.httpclient.HttpClientConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import pl.fintech.dragons.dragonslending.common.ExcludeFromComponentScan;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.domain.BankApiService;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.infrastructure.bankapi.BankApi;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.infrastructure.bankapi.BankApiServiceAdapter;

@Configuration
@ExcludeFromComponentScan
@EnableConfigurationProperties
@Import({
        FeignAutoConfiguration.class,
        HttpClientConfiguration.class,
        HttpMessageConvertersAutoConfiguration.class,
        PropertySourcesPlaceholderConfigurer.class
})
public class BankClientConfiguration {

    @Value("${bankapi.security.username}")
    private String apiUsername;
    @Value("${bankapi.security.password}")
    private String apiPassword;

    @Bean
    public ErrorDecoder errorDecoder() {
        return new ApiErrorDecoder();
    }

    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
        return new BasicAuthRequestInterceptor(apiUsername, apiPassword);
    }
}
