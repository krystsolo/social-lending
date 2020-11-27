package pl.fintech.dragons.dragonslending.sociallending.lending.loan.infrastructure;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import pl.fintech.dragons.dragonslending.common.events.EventPublisher;
import pl.fintech.dragons.dragonslending.common.events.publisher.EventPublisherConfig;
import pl.fintech.dragons.dragonslending.sociallending.identity.application.UserService;
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.application.LoanCalculationService;
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.application.LoanRepaymentHandlerScheduler;
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.application.OfferSelectedEventHandler;
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.application.query.LoanFinder;
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.application.query.LoanViewAssembler;
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.Loan;
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.calculation.LoanCalculator;
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.LoanRepository;
import pl.fintech.dragons.dragonslending.sociallending.lending.loan.domain.calculation.SystemFeeCalculationPolicy;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.application.AccountFinder;
import pl.fintech.dragons.dragonslending.sociallending.payment.account.infrastructure.AccountConfig;

@Configuration
@EnableJpaRepositories(basePackageClasses = LoanRepositoryAdapter.class, considerNestedRepositories = true)
@EntityScan(basePackageClasses = Loan.class)
@Import({AccountConfig.class, EventPublisherConfig.class})
public class LoanConfig {

    @Bean
    LoanCalculationService loanCalculationService(LoanCalculator loanCalculator) {
        return new LoanCalculationService(loanCalculator);
    }

    @Bean
    LoanCalculator loanCalculator() {
        return new LoanCalculator(new SystemFeeCalculationPolicy());
    }

    @Bean
    LoanRepository loanRepository(LoanRepositoryAdapter.LoanJpaRepository jpaRepository) {
        return new LoanRepositoryAdapter(jpaRepository);
    }

    @Bean
    LoanRepaymentHandlerScheduler loanRepaymentHandler(LoanRepository loanRepository, AccountFinder accountFinder, EventPublisher eventPublisher) {
        return new LoanRepaymentHandlerScheduler(loanRepository, accountFinder, eventPublisher);
    }

    @Bean
    LoanFinder loanFinder(LoanRepository loanRepository, UserService userService) {
        return new LoanFinder(loanRepository, userService, new LoanViewAssembler(userService));
    }

    @Bean
    OfferSelectedEventHandler offerSelectedEventHandler(LoanRepository loanRepository, LoanCalculator loanCalculator) {
        return new OfferSelectedEventHandler(loanRepository, loanCalculator);
    }
}
