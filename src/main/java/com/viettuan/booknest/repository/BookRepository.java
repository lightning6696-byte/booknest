package com.viettuan.booknest.repository;

import com.viettuan.booknest.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository
        extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn);
}