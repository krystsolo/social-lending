package pl.fintech.dragons.dragonslending.sociallending.lending.loan.application.query;

import lombok.Value;

import java.util.List;

@Value
class LoansView {
    List<LoanQuery> loansGranted;
    List<LoanQuery> loansTaken;
}

