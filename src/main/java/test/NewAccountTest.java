package test;

import Domain.Data.Account;
import Domain.Data.Attributes;
import Domain.Data.Pygmy;
import Domain.Data.Statistics;
import Services.*;
import lombok.extern.log4j.Log4j2;

import java.util.UUID;

@Log4j2
public class NewAccountTest {
    public static void main(String[] args) {
        createAccountTest();
    }

    public static void createAccountTest() {
        log.info("Creating a new Account...");

        UUID uuid = UUID.randomUUID();
        String name = "edf";

        log.info("Creating a Account...");
        Account account = AccountService.createAccount(uuid, name,"123456");
        log.info("Creating a Pygmy...");
        Pygmy pygmy = PygmyService.createPygmy(uuid, name);
        log.info("Creating the Attributes...");
        Attributes attributes = AttributesService.createAttribute(uuid);
        log.info("Creating the Statistics...");
        Statistics statistics = StatisticsService.createStatistics(uuid);
        log.info("Saving...");
        PygmyService.savePygmy(pygmy);
        AccountService.saveAccount(account);
        AttributesService.saveAttributes(attributes);
        StatisticsService.saveStatistics(statistics);
        log.info("Done! Welcome {} !!!", name);
    }
}