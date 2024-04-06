package services;

import domain.*;
import repository.AccountsRepository;
import utils.MoneyUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class TransactionManagerService {

    public TransactionModel transfer(String fromAccountId, String toAccountId, MoneyModel value) {
        AccountModel fromAccount = AccountsRepository.INSTANCE.get(fromAccountId);
        AccountModel toAccount = AccountsRepository.INSTANCE.get(toAccountId);

        value = checkTransferPreconditions(value, fromAccount, toAccount);

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

    private static MoneyModel checkTransferPreconditions(MoneyModel value, AccountModel fromAccount, AccountModel toAccount) {
        if (value.getAmount() < 0) {
            throw new RuntimeException("Can't transfer negative sums of money");
        }

        validateTransferAmount(value);

        if (fromAccount == null || toAccount == null) {
            throw new RuntimeException("Specified account does not exist");
        }

        if (Objects.equals(fromAccount.getId(), toAccount.getId())) {
            throw new RuntimeException("Can't transfer money to same account");
        }

        if (fromAccount instanceof SavingsAccountModel) {
            throw  new RuntimeException("Can't transfer from a savings account");
        }

        if (fromAccount.getBalance().getCurrency() != toAccount.getBalance().getCurrency()) {
            value = MoneyUtils.convert(value, toAccount.getBalance().getCurrency());
        }

        if (fromAccount.getBalance().getAmount() < value.getAmount()) {
            throw new RuntimeException("Not enough funds for transfer");
        }


        return value;
    }

    public static void validateTransferAmount(MoneyModel amount) {
        double transferAmount = amount.getAmount();
        switch (amount.getCurrency()) {
            case EUR:
                if (transferAmount > MaximumTransferableAmount.getMaxEuroTransfer()) {
                    throw new RuntimeException("Transfer amount exceeded for euro currency");
                }
                break;
            case RON:
                if (transferAmount > MaximumTransferableAmount.getMaxRonTransfer()) {
                    throw new RuntimeException("Transfer amount exceeded for ron currency");
                }
            default:
                throw new RuntimeException("Transfer currency not available");
        }
    }

    public synchronized TransactionModel withdraw(String accountId, MoneyModel amount) {
        AccountModel account = AccountsRepository.INSTANCE.get(accountId);
        checkWithdrawPreconditions(amount, account);

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

    private static void checkWithdrawPreconditions(MoneyModel amount, AccountModel account) {
        if (account == null) {
            throw new RuntimeException("This account doesn't exist");
        }
        if (amount.getAmount() > account.getBalance().getAmount()) {
            throw new RuntimeException("Insufficient funds");
        }
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

