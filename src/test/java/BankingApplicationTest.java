import domain.CheckingAccountModel;
import domain.CurrencyType;
import domain.MoneyModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import repository.AccountsRepository;
import services.TransactionManagerService;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static seed.AccountsSeedData.*;
import static seed.SeedInitializer.seedData;


public class BankingApplicationTest {

    private AccountsRepository repository;
    private TransactionManagerService service;

    CheckingAccountModel fromAccount = checkingAccountA;
    CheckingAccountModel toAccount = checkingAccountB;

    @BeforeEach
    void setUp () {
        this.service = new TransactionManagerService();
        this.repository = new AccountsRepository();
        seedData();
    }

    @Test
    public void expectTrueTest() {
        assertTrue(true);
    }

    @Test
    public void transferValidAccounts() {
        MoneyModel amount = new MoneyModel(50, CurrencyType.RON);
        service.transfer(checkingAccountA.getId(), checkingAccountB.getId(), amount);
        assertEquals(50, checkingAccountA.getBalance().getAmount());
        assertEquals(350, checkingAccountA.getBalance().getAmount());

        assertEquals(1, fromAccount.getTransactions().size());
        assertEquals(fromAccount.getTransactions(), toAccount.getTransactions());
    }


}
