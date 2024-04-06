package services;

import domain.CapitalizationFrequency;
import domain.SavingsAccountModel;
import repository.AccountsRepository;

import java.time.LocalDate;
import java.util.List;

public class SavingsManagerService {
    private LocalDate systemDate = LocalDate.now();



    public void passTime() {
        List<SavingsAccountModel> savingAccounts = AccountsRepository.INSTANCE.getAll().stream()
                .filter(account -> account instanceof SavingsAccountModel)
                .map(account -> (SavingsAccountModel) account).toList();

        LocalDate nextSystemDate = systemDate.plusMonths(1);

        savingAccounts.forEach(savingAccount -> {
            if (savingAccount.getInterestFrequency() == CapitalizationFrequency.QUARTERLY) {
                addMonthlyInterest(savingAccount, nextSystemDate);
            }
            else if (savingAccount.getInterestFrequency() == CapitalizationFrequency.MONTHLY) {
                addMonthlyInterest(savingAccount, nextSystemDate);
            }
        });

        savingAccounts.forEach(savingAccount -> {
            if (savingAccount.getInterestFrequency() == CapitalizationFrequency.QUARTERLY) {
                addQuarterlyInterest(savingAccount, nextSystemDate);
            }
        });

        systemDate = nextSystemDate;
    }

    private void addInterest(SavingsAccountModel savingAccount) {
        double interest = savingAccount.getBalance().getAmount() * savingAccount.getInterest();
        savingAccount.getBalance().setAmount(savingAccount.getBalance().getAmount() + interest);
    }

    private void addMonthlyInterest(SavingsAccountModel savingAccount, LocalDate currentInterestMonth) {
        LocalDate nextInterestDateForAccount = savingAccount.getLastInterestAppliedDate().plusMonths(1);

        if (isSameMonthAndYear(currentInterestMonth, nextInterestDateForAccount)) {
            addInterest(savingAccount);
            savingAccount.setLastInterestAppliedDate(currentInterestMonth);
        }
    }

    private void addQuarterlyInterest(SavingsAccountModel savingAccount, LocalDate currentIntegersQuarter) {
        LocalDate nextInterestDateForAccount = savingAccount.getLastInterestAppliedDate().plusMonths(3);

        if (isSameQuarterAndYear(currentIntegersQuarter, nextInterestDateForAccount)) {
            addInterest(savingAccount);
            savingAccount.setLastInterestAppliedDate(currentIntegersQuarter);
        }
    }

    private boolean isSameMonthAndYear(LocalDate date1, LocalDate date2) {
        return date1.getMonth() == date2.getMonth() && date1.getYear() == date2.getYear();
    }

    private int getQuarter(LocalDate date) {return (date.getMonthValue() - 1) / 3 + 1;}

    private boolean isSameQuarterAndYear(LocalDate date1, LocalDate date2) {
        return getQuarter(date1) == getQuarter(date2) && date1.getYear() == date2.getYear();
    }
}


