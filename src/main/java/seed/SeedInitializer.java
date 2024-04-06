package seed;

import repository.AccountsRepository;

import static seed.AccountsSeedData.checkingAccountA;
import static seed.AccountsSeedData.checkingAccountB;
import static seed.AccountsSeedData.checkingAccountC;
import static seed.AccountsSeedData.checkingAccountD;
import static seed.AccountsSeedData.savingsAccountA;
import static seed.AccountsSeedData.savingsAccountB;

public class SeedInitializer {

    public static void seedData() {
        System.out.println("[Seeder] -------------Seeding data----------------\n");
        AccountsRepository.INSTANCE.clear();
        AccountsRepository.INSTANCE.add(savingsAccountA.getId(), savingsAccountA);
        AccountsRepository.INSTANCE.add(savingsAccountB.getId(), savingsAccountB);
        AccountsRepository.INSTANCE.add(checkingAccountA.getId(), checkingAccountA);
        AccountsRepository.INSTANCE.add(checkingAccountB.getId(), checkingAccountB);
        AccountsRepository.INSTANCE.add(checkingAccountC.getId(), checkingAccountC);
        AccountsRepository.INSTANCE.add(checkingAccountD.getId(), checkingAccountD);
    }
}
