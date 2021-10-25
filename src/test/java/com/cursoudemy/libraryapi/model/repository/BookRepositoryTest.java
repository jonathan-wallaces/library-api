package com.cursoudemy.libraryapi.model.repository;

import com.cursoudemy.libraryapi.model.entity.Book;
import com.cursoudemy.libraryapi.model.entity.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.OPTIONAL;

@ExtendWith({SpringExtension.class})
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;
    @Autowired
    BookRepository bookRepository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando existir um livro na base com o isbn informado")
    public void returnTrueWhenIsbnExists(){
        //cenario
        Book book = createdValidBook();
        String isbn ="123";
        entityManager.persist(book);

        //execucao

        boolean exists = bookRepository.existsByIsbn(isbn);

        //verificacao

        assertThat(exists).isTrue();
    }
    @Test
    @DisplayName("Deve retornar falso quando não existir um livro na base com o isbn informado")
    public void returnFalseWhenIsbnDoesnotExists(){
        //cenario
        String isbn ="123";
        //execucao
        boolean exists = bookRepository.existsByIsbn(isbn);
        //verificacao
        assertThat(exists).isFalse();
    }
    @Test @DisplayName("Deve obter um livro pelo Id")
    public void findByIdTest(){
        Book book = createdValidBook();
        entityManager.persist(book);

        //execucao
       Optional<Book> foundBook = bookRepository.findById(book.getId());

       //teste
        assertThat(foundBook.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest(){
        Book book = createdValidBook();
        Book savedBook = bookRepository.save(book);
        assertThat(savedBook.getId()).isNotNull();
    }

    @Test@DisplayName("Deve deletar um livro")
    public void deleteBook(){
        Book book = createdValidBook();
        entityManager.persist(book);
        Book foundBook = entityManager.find(Book.class, book.getId());

        bookRepository.delete(book);
        Book foundDeleteBook= entityManager.find(Book.class, book.getId());

        assertThat(foundDeleteBook).isNull();

    }



    private Book createdValidBook() {
        return Book.builder()
                .author("Artur").title("As aventuras").isbn("123").build();
    }
}
