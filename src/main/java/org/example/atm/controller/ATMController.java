package org.example.atm.controller;

import org.example.atm.service.ATMService;
import org.example.atm.service.InsufficientFundsException;
import org.example.atm.service.InvalidPinException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;

@RestController
@RequestMapping("/atm")
public class ATMController {

    private final ATMService atmService;

    @Autowired
    public ATMController(ATMService atmService) {
        this.atmService = atmService;
    }

    @GetMapping("/balance")
    public ResponseEntity<BigDecimal> checkBalance(@RequestParam String accountNumber, @RequestParam String pin) throws AccountNotFoundException, InvalidPinException {
        BigDecimal balance = atmService.checkBalance(accountNumber, pin);
        return ResponseEntity.ok(balance);
    }

    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(
            @RequestParam String accountNumber,
            @RequestParam String pin,
            @RequestParam BigDecimal amount
    ) throws AccountNotFoundException, InvalidPinException {
        atmService.deposit(accountNumber, pin, amount);
        return ResponseEntity.ok("deposited: "+amount+"\nBalance: "+atmService.checkBalance(accountNumber,pin)+"\n");
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(
            @RequestParam String accountNumber,
            @RequestParam String pin,
            @RequestParam BigDecimal amount
    ) throws AccountNotFoundException, InvalidPinException, InsufficientFundsException {
        atmService.withdraw(accountNumber, pin, amount);
        return ResponseEntity.ok("withdrawn: "+amount+"\nBalance: "+atmService.checkBalance(accountNumber,pin)+"\n");
    }


}