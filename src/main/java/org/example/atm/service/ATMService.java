package org.example.atm.service;

import org.example.atm.model.Account;
import org.example.atm.model.Transaction;
import org.example.atm.repository.AccountRepository;
import org.example.atm.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ATMService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public ATMService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    //    @Autowired
//    private PasswordEncoder

    public BigDecimal checkBalance(String accountNumber, String pin) throws AccountNotFoundException, InvalidPinException {
        Account account = validateAccount(accountNumber, pin);
        return account.getBalance();
    }


    public void deposit(String accountNumber, String pin, BigDecimal amount) throws AccountNotFoundException, InvalidPinException {
        Account account = validateAccount(accountNumber, pin);
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
        createTransaction(account, amount, "DEPOSIT", "Deposit");
    }


    public void withdraw(String accountNumber, String pin, BigDecimal amount) throws AccountNotFoundException, InvalidPinException, InsufficientFundsException {
        Account account = validateAccount(accountNumber, pin);
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }
            account.setBalance(account.getBalance().subtract(amount));
            accountRepository.save(account);
            createTransaction(account, amount, "WITHDRAW", "Withdraw");

    }

    public void transfer(String fromAccountNumber, String pin, String toAccountNumber, BigDecimal amount) throws AccountNotFoundException, InvalidPinException, InsufficientFundsException {
        Account fromAccount = validateAccount(fromAccountNumber, pin);
        Account toAccount = doesAccountExists(toAccountNumber);
        if (fromAccount.getBalance().compareTo(amount) < 0 ){
            throw new InsufficientFundsException("Insufficient funds");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        createTransaction(fromAccount, amount.negate(), "TRANSFER_OUT", "Transfer_OUT");
        createTransaction(toAccount, amount, "TRANSFER_IN", "Transfer_IN");

    }

    public List<Transaction> getTransactionHistory(String accountNumber, String pin) throws AccountNotFoundException, InvalidPinException {
        Account account = validateAccount(accountNumber, pin);
        return transactionRepository.findByAccountOrderByTimestampDesc(account);
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

    private Account doesAccountExists(String accountNumber) throws AccountNotFoundException {
        Optional<Account> accountOpt = accountRepository.findByAccountNumber(accountNumber);

        if (accountOpt.isEmpty()) {
            throw new AccountNotFoundException("Account not found for account number: " + accountNumber);
        }

        Account account = accountOpt.get();

        if (account.getPin().isEmpty()) {
            throw new AccountNotFoundException("Account not found for account number: " + accountNumber);
        }

        return account;
    }



    private void createTransaction(Account account, BigDecimal amount, String type, String description) {
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setDescription(description);
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

}
