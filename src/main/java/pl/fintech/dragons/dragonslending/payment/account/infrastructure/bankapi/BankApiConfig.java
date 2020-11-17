package pl.fintech.dragons.dragonslending.payment.account.infrastructure.bankapi;

import feign.codec.ErrorDecoder;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.cloud.commons.httpclient.HttpClientConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import pl.fintech.dragons.dragonslending.payment.account.domain.BankApiService;

@Configuration
@EnableFeignClients(clients = {
        BankApi.class
})
@Import({
        FeignAutoConfiguration.class,
        HttpClientConfiguration.class,
        HttpMessageConvertersAutoConfiguration.class,
        PropertySourcesPlaceholderConfigurer.class
})
public class BankApiConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new ApiErrorDecoder();
    }

    @Bean
    public BankApiService bankApiService(BankApi bankApi) {
        return new BankApiServiceAdapter(bankApi);
    }
}
