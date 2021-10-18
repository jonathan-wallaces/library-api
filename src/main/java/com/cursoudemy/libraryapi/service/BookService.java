package com.cursoudemy.libraryapi.service;

import com.cursoudemy.libraryapi.model.entity.Book;

import java.util.Optional;

public interface BookService {
    Book save(Book book);

    Optional<Book> getById(Long id);

    void deletById(Long id);
}
