package pl.fintech.dragons.dragonslending.sociallending.loanCalculator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoanCalculatorConfig {

  @Bean
  LoanCalculator loanCalculatorAdapter() {return new LoanCalculatorAdapter();}
}
