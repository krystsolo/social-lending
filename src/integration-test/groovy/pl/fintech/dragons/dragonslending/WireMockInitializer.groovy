package pl.fintech.dragons.dragonslending

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.cloud.contract.wiremock.WireMockConfigurationCustomizer
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ApplicationListener
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.event.ContextClosedEvent

class WireMockInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static int RANDOM_PORT = 0

    static WireMockServer wireMockServer = new WireMockServer(
            WireMockConfiguration.options()
                    .port(RANDOM_PORT)
                    .usingFilesUnderDirectory("src/integration-test/resources"))

    @Override
    void initialize(ConfigurableApplicationContext applicationContext) {
        applicationContext.beanFactory.registerSingleton('wireMockServer', wireMockServer)
        wireMockServer.start()
        TestPropertyValues.of("bankapi.url=http://localhost:${wireMockServer.port()}").applyTo(applicationContext)
        applicationContext.addApplicationListener(new WireMockShutdownListener(wireMockServer))
    }

    static class WireMockShutdownListener implements ApplicationListener<ContextClosedEvent> {
        final WireMockServer server

        WireMockShutdownListener(WireMockServer server) {
            this.server = server
        }

        @Override
        void onApplicationEvent(ContextClosedEvent event) {
            server.stop()
        }
    }
}
