package com.viettuan.booknest.Controller;

import com.viettuan.booknest.entity.Book;
import com.viettuan.booknest.service.BookService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'READER')")
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'READER')")
    public Book getBookById(
            @PathVariable Long id
    ) {
        return bookService.getBookById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Book createBook(
            @RequestBody Book book
    ) {
        return bookService.createBook(book);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Book updateBook(
            @PathVariable Long id,
            @RequestBody Book book
    ) {
        return bookService.updateBook(id, book);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteBook(
            @PathVariable Long id
    ) {
        bookService.deleteBook(id);
    }
}