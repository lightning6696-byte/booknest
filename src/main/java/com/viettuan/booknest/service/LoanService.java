package com.viettuan.booknest.service;

import com.viettuan.booknest.entity.Book;
import com.viettuan.booknest.entity.Loan;
import com.viettuan.booknest.entity.User;
import com.viettuan.booknest.exception.BadRequestException;
import com.viettuan.booknest.exception.ResourceNotFoundException;
import com.viettuan.booknest.repository.BookRepository;
import com.viettuan.booknest.repository.LoanRepository;
import com.viettuan.booknest.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public LoanService(
            LoanRepository loanRepository,
            BookRepository bookRepository,
            UserRepository userRepository
    ) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public List<Loan> getAllLoans() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        boolean isAdmin = false;

        for (GrantedAuthority authority : authentication.getAuthorities()) {

            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                isAdmin = true;
            }
        }

        if (isAdmin) {
            return loanRepository.findAll();
        }

        return loanRepository.findByUserEmail(email);
    }

    public Loan getLoanById(Long id) {

        Loan loan = loanRepository.findById(id).orElse(null);

        if (loan == null) {
            throw new ResourceNotFoundException(
                    "Không tìm thấy phiếu mượn"
            );
        }

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = false;

        for (GrantedAuthority authority : authentication.getAuthorities()) {

            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                isAdmin = true;
            }
        }

        if (isAdmin) {
            return loan;
        }

        String email = authentication.getName();

        if (!loan.getUser().getEmail().equals(email)) {

            throw new ResourceNotFoundException(
                    "Không tìm thấy phiếu mượn"
            );
        }

        return loan;
    }

    public Loan createLoan(Loan loan) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            throw new ResourceNotFoundException(
                    "Không tìm thấy người dùng"
            );
        }

        if (loan.getBook() == null
                || loan.getBook().getId() == null) {

            throw new BadRequestException(
                    "Sách không hợp lệ"
            );
        }

        Book book = bookRepository
                .findById(loan.getBook().getId())
                .orElse(null);

        if (book == null) {
            throw new ResourceNotFoundException(
                    "Không tìm thấy sách"
            );
        }

        if (book.getQuantity() == null
                || book.getQuantity() <= 0) {

            throw new BadRequestException(
                    "Sách đã hết"
            );
        }

        if (loan.getDueDate() == null) {

            throw new BadRequestException(
                    "Ngày hết hạn không được để trống"
            );
        }

        LocalDate today = LocalDate.now();

        if (loan.getDueDate().isBefore(today)) {

            throw new BadRequestException(
                    "Ngày hết hạn không hợp lệ"
            );
        }

        book.setQuantity(
                book.getQuantity() - 1
        );

        bookRepository.save(book);

        loan.setUser(user);
        loan.setBook(book);
        loan.setBorrowDate(today);
        loan.setReturnDate(null);

        return loanRepository.save(loan);
    }

    public Loan updateLoan(Long id, Loan loan) {

        Loan existingLoan =
                loanRepository.findById(id).orElse(null);

        if (existingLoan == null) {

            throw new ResourceNotFoundException(
                    "Không tìm thấy phiếu mượn"
            );
        }

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = false;

        for (GrantedAuthority authority : authentication.getAuthorities()) {

            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                isAdmin = true;
            }
        }

        String email = authentication.getName();

        if (!isAdmin
                && !existingLoan.getUser().getEmail().equals(email)) {

            throw new ResourceNotFoundException(
                    "Không tìm thấy phiếu mượn"
            );
        }

        if (loan.getDueDate() != null) {
            existingLoan.setDueDate(
                    loan.getDueDate()
            );
        }

        if (existingLoan.getReturnDate() == null
                && loan.getReturnDate() != null) {

            Book book = existingLoan.getBook();

            book.setQuantity(
                    book.getQuantity() + 1
            );

            bookRepository.save(book);

            existingLoan.setReturnDate(
                    loan.getReturnDate()
            );
        }

        return loanRepository.save(existingLoan);
    }

    public void deleteLoan(Long id) {

        Loan loan = loanRepository.findById(id).orElse(null);

        if (loan == null) {

            throw new ResourceNotFoundException(
                    "Không tìm thấy phiếu mượn"
            );
        }

        loanRepository.deleteById(id);
    }
}