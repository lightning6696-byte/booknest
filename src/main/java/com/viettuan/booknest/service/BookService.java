package com.viettuan.booknest.service;

import com.viettuan.booknest.entity.Book;
import com.viettuan.booknest.exception.BadRequestException;
import com.viettuan.booknest.exception.ConflictException;
import com.viettuan.booknest.exception.ResourceNotFoundException;
import com.viettuan.booknest.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Long id) {

        Book book = bookRepository.findById(id).orElse(null);

        if (book == null) {
            throw new ResourceNotFoundException(
                    "Không tìm thấy sách"
            );
        }

        return book;
    }

    public Book createBook(Book book) {

        if (book.getTitle() == null
                || book.getTitle().isBlank()) {

            throw new BadRequestException(
                    "Tên sách không được để trống"
            );
        }

        if (book.getIsbn() == null
                || book.getIsbn().isBlank()) {

            throw new BadRequestException(
                    "ISBN không được để trống"
            );
        }

        if (book.getQuantity() == null
                || book.getQuantity() < 0) {

            throw new BadRequestException(
                    "Số lượng sách không hợp lệ"
            );
        }

        if (bookRepository.existsByIsbn(book.getIsbn())) {

            throw new ConflictException(
                    "ISBN đã tồn tại"
            );
        }

        return bookRepository.save(book);
    }

    public Book updateBook(Long id, Book book) {

        Book oldBook = bookRepository.findById(id).orElse(null);

        if (oldBook == null) {
            throw new ResourceNotFoundException(
                    "Không tìm thấy sách"
            );
        }

        if (book.getTitle() == null
                || book.getTitle().isBlank()) {

            throw new BadRequestException(
                    "Tên sách không được để trống"
            );
        }

        if (book.getIsbn() == null
                || book.getIsbn().isBlank()) {

            throw new BadRequestException(
                    "ISBN không được để trống"
            );
        }

        if (book.getQuantity() == null
                || book.getQuantity() < 0) {

            throw new BadRequestException(
                    "Số lượng sách không hợp lệ"
            );
        }

        if (!oldBook.getIsbn().equals(book.getIsbn())
                && bookRepository.existsByIsbn(book.getIsbn())) {

            throw new ConflictException(
                    "ISBN đã tồn tại"
            );
        }

        oldBook.setTitle(book.getTitle());
        oldBook.setIsbn(book.getIsbn());
        oldBook.setAuthor(book.getAuthor());
        oldBook.setCategory(book.getCategory());
        oldBook.setPublicationYear(book.getPublicationYear());
        oldBook.setQuantity(book.getQuantity());

        return bookRepository.save(oldBook);
    }

    public void deleteBook(Long id) {

        Book book = bookRepository.findById(id).orElse(null);

        if (book == null) {
            throw new ResourceNotFoundException(
                    "Không tìm thấy sách"
            );
        }

        bookRepository.deleteById(id);
    }
}