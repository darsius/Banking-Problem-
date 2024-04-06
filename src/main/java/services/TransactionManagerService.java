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

    public synchronized TransactionModel transfer(String fromAccountId, String toAccountId, MoneyModel value) {
        AccountModel fromAccount = validateAccount(fromAccountId);
        AccountModel toAccount = validateAccount(toAccountId);

        value = checkTransferPreconditions(value, fromAccount, toAccount);

        return processTransfer(fromAccountId, toAccountId, value, fromAccount, toAccount);
    }

    private TransactionModel createTransaction(String fromAccountId, String toAccountId, MoneyModel value) {
        TransactionModel transaction = new TransactionModel(
                UUID.randomUUID(),
                fromAccountId,
                toAccountId,
                value,
                LocalDate.now()
        );
        AccountsRepository.INSTANCE.get(fromAccountId).getTransactions().add(transaction);
        AccountsRepository.INSTANCE.get(toAccountId).getTransactions().add(transaction);
        return transaction;
    }

    private AccountModel validateAccount(String accountId) {
        AccountModel account = AccountsRepository.INSTANCE.get(accountId);
        if (account == null) {
            throw new RuntimeException("This account doesn't exist");
        }
        return account;
    }

    private MoneyModel convertCurrency (MoneyModel value, AccountModel fromAccount, AccountModel toAccount) {
        if (fromAccount.getBalance().getCurrency() != toAccount.getBalance().getCurrency()) {
            return MoneyUtils.convert(value, toAccount.getBalance().getCurrency());
        }
        return value;
    }

    private TransactionModel processTransfer(String fromAccountId, String toAccountId, MoneyModel value, AccountModel fromAccount, AccountModel toAccount) {
        TransactionModel transaction = createTransaction(fromAccountId, toAccountId, value);
        updateBalances(value, fromAccount, toAccount);
        return transaction;
    }

    private void checkSufficientFunds(AccountModel fromAccount, MoneyModel value) {
        if (fromAccount.getBalance().getAmount() < value.getAmount()) {
            throw new RuntimeException("Not enough funds for transfer");
        }
    }

    private void updateBalances(MoneyModel value, AccountModel fromAccount, AccountModel toAccount) {
        fromAccount.getBalance().setAmount(fromAccount.getBalance().getAmount() - value.getAmount());
        toAccount.getBalance().setAmount(toAccount.getBalance().getAmount() + value.getAmount());
    }

    private void checkForNegativeAmount(MoneyModel value) {
        if (value.getAmount() < 0) {
            throw new RuntimeException("Cannot transfer negative sums of money");
        }
    }

    private void checkForSameAccountTransfer(AccountModel fromAccount, AccountModel toAccount) {
        if (Objects.equals(fromAccount.getId(), toAccount.getId())) {
            throw new RuntimeException("Cannot transfer money to the same account");
        }
    }

    private void checkForSavingsAccountTransfer(AccountModel fromAccount) {
        if (fromAccount instanceof SavingsAccountModel) {
            throw new RuntimeException("Cannot transfer from a savings account");
        }
    }

    private MoneyModel checkTransferPreconditions(MoneyModel value, AccountModel fromAccount, AccountModel toAccount) {
        checkForNegativeAmount(value);
        validateTransferAmount(value);
        checkForSameAccountTransfer(fromAccount, toAccount);
        checkForSavingsAccountTransfer(fromAccount);
        checkSufficientFunds(fromAccount, value);

        return convertCurrency(value, fromAccount, toAccount);
    }

    private static void validateTransferAmount(MoneyModel amount) {
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
                break;
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

