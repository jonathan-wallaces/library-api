package com.cursoudemy.libraryapi.service;

import com.cursoudemy.libraryapi.model.entity.Book;

import java.util.Optional;

public interface BookService {
    Book save(Book book);

    Optional<Book> getById(Long id);

    void delete(Book book);


    Book update(Book book);

    Optional<Book> getBookByIsbn(String isbn);
}
