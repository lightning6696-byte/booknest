package com.viettuan.booknest.Controller;

import com.viettuan.booknest.entity.Loan;
import com.viettuan.booknest.service.LoanService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping
    public List<Loan> getAllLoans() {
        return loanService.getAllLoans();
    }

    @GetMapping("/{id}")
    public Loan getLoanById(
            @PathVariable Long id
    ) {
        return loanService.getLoanById(id);
    }

    @PostMapping
    public Loan createLoan(
            @RequestBody Loan loan
    ) {
        return loanService.createLoan(loan);
    }

    @PutMapping("/{id}")
    public Loan updateLoan(
            @PathVariable Long id,
            @RequestBody Loan loan
    ) {
        return loanService.updateLoan(id, loan);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteLoan(
            @PathVariable Long id
    ) {
        loanService.deleteLoan(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}