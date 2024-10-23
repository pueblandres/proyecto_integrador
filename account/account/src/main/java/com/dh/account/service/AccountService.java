package com.dh.account.service;

import com.dh.account.Repository.AccountRepository;
import com.dh.account.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final List<String> aliasWords;

    @Autowired
    public AccountService(AccountRepository accountRepository) throws IOException {
        this.accountRepository = accountRepository;
        Path filePath = new ClassPathResource("words.txt").getFile().toPath();
        this.aliasWords = Files.readAllLines(filePath);
    }

     public Account createAccountForUser(Long userId) {
        String cvu = generateRandomCVU();
        String alias = generateRandomAlias();
        Account account = Account.builder()
                .userId(userId)
                .balance(0)
                .cvu(cvu)
                .alias(alias)
                .build();
        return accountRepository.save(account);
    }

    private String generateRandomCVU() {
        Random random = new Random();
        StringBuilder cvu = new StringBuilder();
        for (int i = 0; i < 22; i++) {
            cvu.append(random.nextInt(10));
        }
        return cvu.toString();
    }

    private String generateRandomAlias() {
        Random random = new Random();
        StringBuilder alias = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            String word = aliasWords.get(random.nextInt(aliasWords.size()));
            alias.append(word);
            if (i < 2) {
                alias.append(".");
            }
        }
        return alias.toString();
    }
}
