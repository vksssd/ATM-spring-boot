package org.example.atm.service;

import org.example.atm.model.Account;
import org.example.atm.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.util.Optional;

@Service
public class ATMService {

    private final AccountRepository accountRepository;

    @Autowired
    public ATMService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public BigDecimal checkBalance(String accountNumber, String pin) throws AccountNotFoundException, InvalidPinException {
        Account account = validateAccount(accountNumber, pin);
        return account.getBalance();
    }


    public void deposit(String accountNumber, String pin, BigDecimal amount) throws AccountNotFoundException, InvalidPinException {
        Account account = validateAccount(accountNumber, pin);
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
    }

    public void withdraw(String accountNumber, String pin, BigDecimal amount) throws AccountNotFoundException, InvalidPinException, InsufficientFundsException {
        Account account = validateAccount(accountNumber, pin);
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }
            account.setBalance(account.getBalance().subtract(amount));
            accountRepository.save(account);

    }

    private Account validateAccount(String accountNumber, String pin) throws AccountNotFoundException, InvalidPinException {
        Optional<Account> accountOpt = accountRepository.findByAccountNumber(accountNumber);

        if (accountOpt.isEmpty()) {
            throw new AccountNotFoundException("Account not found for account number: " + accountNumber);
        }

        Account account = accountOpt.get();

        if (!account.getPin().equals(pin)) {
            throw new InvalidPinException("Invalid PIN provided.");
        }

        return account;
    }
}
