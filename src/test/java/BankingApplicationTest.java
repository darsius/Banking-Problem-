import domain.CheckingAccountModel;
import domain.CurrencyType;
import domain.MoneyModel;
import org.junit.Before;
import org.junit.Test;

import seed.SeedInitializer;
import services.TransactionManagerService;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static seed.AccountsSeedData.*;
import static seed.SeedInitializer.seedData;


public class BankingApplicationTest {

//    private AccountsRepository repository;
    private TransactionManagerService service;

    CheckingAccountModel fromAccount = checkingAccountA;
    CheckingAccountModel toAccount = checkingAccountB;

    @Before
    public void setUp () {
        this.service = new TransactionManagerService();
        SeedInitializer.seedData();
    }

    @Test
    public void expectTrueTest() {
        assertTrue(true);
    }

    @Test
    public void transferValidAccounts() {
        MoneyModel amount = new MoneyModel(50, CurrencyType.RON);
        double delta = 0.01;

        service.transfer(checkingAccountA.getId(), checkingAccountB.getId(), amount);

        assertEquals(50.0, checkingAccountA.getBalance().getAmount(), delta);
        assertEquals(350.0, checkingAccountB.getBalance().getAmount(), delta);

        assertEquals(1, fromAccount.getTransactions().size());
        assertEquals(fromAccount.getTransactions(), toAccount.getTransactions());
    }

    @Test(expected = RuntimeException.class)
    public void transferNegativeAmount() {
        MoneyModel amount = new MoneyModel(-50, CurrencyType.RON);
        service.transfer(checkingAccountA.getId(), checkingAccountB.getId(), amount);
    }

    @Test(expected = RuntimeException.class)
    public void transferExceedingBalance() {
        MoneyModel amount = new MoneyModel(2000.0, CurrencyType.RON);
        service.transfer(checkingAccountA.getId(), checkingAccountB.getId(), amount);
    }

    @Test(expected = RuntimeException.class)
    public void transferToSameAccount() {
        MoneyModel amount = new MoneyModel(50, CurrencyType.RON);
        service.transfer(checkingAccountA.getId(), checkingAccountA.getId(), amount);
    }

    @Test(expected = RuntimeException.class)
    public void transferToNonExistentAccount() {
        MoneyModel amount = new MoneyModel(50, CurrencyType.RON);
        String nonExistingAccountId = "1";
        service.transfer(checkingAccountA.getId(), nonExistingAccountId, amount);
    }

    @Test(expected = RuntimeException.class)
    public void transferFromNonExistentAccount() {
        MoneyModel amount = new MoneyModel(50, CurrencyType.RON);
        String nonExistingAccountId = "1";
        service.transfer(nonExistingAccountId, checkingAccountA.getId(), amount);
    }

    @Test(expected = RuntimeException.class)
    public void transferLimitExceeded() {
        MoneyModel amount = new MoneyModel(60000.0, CurrencyType.RON);
        service.transfer(savingsAccountA.getId(), checkingAccountA.getId(), amount);
    }

    @Test
    public void successfulWithdrawal() {
        MoneyModel withdrawalAmount = new MoneyModel(200.0, CurrencyType.RON);
        service.withdraw(savingsAccountA.getId(), withdrawalAmount);

        double expectedBalance = 800.0;
        assertEquals(expectedBalance, savingsAccountA.getBalance().getAmount(), 0.01);

        assertEquals(1, savingsAccountA.getTransactions().size());
    }

    @Test(expected = RuntimeException.class)
    public void exceededBalanceWithdrawal() {
        MoneyModel withdrawalAmount = new MoneyModel(1200.0, CurrencyType.RON);
        service.withdraw(savingsAccountA.getId(), withdrawalAmount);
    }

    @Test(expected = RuntimeException.class)
    public void negativeAmountWithdrawal() {
        MoneyModel withdrawalAmount = new MoneyModel(-1200.0, CurrencyType.RON);
        service.withdraw(savingsAccountA.getId(), withdrawalAmount);
    }

}
