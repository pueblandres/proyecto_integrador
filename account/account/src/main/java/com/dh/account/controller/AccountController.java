package com.dh.account.controller;

import com.dh.account.model.Account;
import com.dh.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/create/{userId}")
    public ResponseEntity<Account> createAccount(@PathVariable Long userId) {
        Account newAccount = accountService.createAccountForUser(userId);
        return new ResponseEntity<>(newAccount, HttpStatus.CREATED);
    }
}
