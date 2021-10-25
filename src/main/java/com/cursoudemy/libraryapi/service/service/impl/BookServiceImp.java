package com.cursoudemy.libraryapi.service.service.impl;

import com.cursoudemy.libraryapi.exception.BusinessException;
import com.cursoudemy.libraryapi.model.entity.Book;
import com.cursoudemy.libraryapi.model.entity.repository.BookRepository;
import com.cursoudemy.libraryapi.service.BookService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookServiceImp implements BookService {

    public BookServiceImp(BookRepository repository) {
        this.repository = repository;
    }

    private BookRepository repository;

    @Override
    public Book save(Book book) {

        if (repository.existsByIsbn(book.getIsbn())) {
            throw new BusinessException("Isbn já criado");
        }
        return repository.save(book);
    }

    @Override
    public Optional<Book> getById(Long id) {
        return this.repository.findById(id);
    }

    @Override
    public void delete(Book book){
        if (book == null || book.getId() == null) {
            throw new IllegalArgumentException("Id não pode ser nulo");
        }
        repository.delete(book);
    }

    @Override
    public Book update(Book book) {
        if (book == null || book.getId() == null) {
            throw new IllegalArgumentException("Id não pode ser nulo");
        }
        return repository.save(book);
    }

    @Override
    public Optional<Book> getBookByIsbn(String s) {
        return Optional.empty();
    }


}

