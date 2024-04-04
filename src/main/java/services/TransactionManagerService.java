package services;

import domain.AccountModel;
import domain.MoneyModel;
import domain.SavingsAccountModel;
import domain.TransactionModel;
import repository.AccountsRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransactionManagerService {

    public TransactionModel transfer(String fromAccountId, String toAccountId, MoneyModel value) {
        AccountModel fromAccount = AccountsRepository.INSTANCE.get(fromAccountId);
        AccountModel toAccount = AccountsRepository.INSTANCE.get(toAccountId);

        if (fromAccount == null || toAccount == null) {
            throw new RuntimeException("Specified account does not exist");
        }

        if (fromAccount instanceof SavingsAccountModel) {
            throw  new RuntimeException("You can't transfer from a savings account");
        }

        TransactionModel transaction = new TransactionModel(
                UUID.randomUUID(),
                fromAccountId,
                toAccountId,
                value,
                LocalDate.now()
        );

        fromAccount.getBalance().setAmount(fromAccount.getBalance().getAmount() - value.getAmount());
        fromAccount.getTransactions().add(transaction);

        toAccount.getBalance().setAmount(toAccount.getBalance().getAmount() + value.getAmount());
        toAccount.getTransactions().add(transaction);

        return transaction;
    }

    public TransactionModel withdraw(String accountId, MoneyModel amount) {
        AccountModel account = AccountsRepository.INSTANCE.get(accountId);
        if (account == null) {
            throw new RuntimeException("This account doesn't exist");
        }
        if (amount.getAmount() > account.getBalance().getAmount()) {
            throw  new RuntimeException("Insufficient funds");
        }

        account.getBalance().setAmount(account.getBalance().getAmount() - amount.getAmount());

        TransactionModel transaction = new TransactionModel(
                UUID.randomUUID(),
                accountId,
                accountId,
                amount,
                LocalDate.now()
        );

        account.getTransactions().add(transaction);
        return transaction;
    }

    public MoneyModel checkFunds(String accountId) {
        if (!AccountsRepository.INSTANCE.exist(accountId)) {
            throw new RuntimeException("Specified account does not exist");
        }
        return AccountsRepository.INSTANCE.get(accountId).getBalance();
    }

    public List<TransactionModel> retrieveTransactions(String accountId) {
        if (!AccountsRepository.INSTANCE.exist(accountId)) {
            throw new RuntimeException("Specified account does not exist");
        }
        return new ArrayList<>(AccountsRepository.INSTANCE.get(accountId).getTransactions());
    }
}

