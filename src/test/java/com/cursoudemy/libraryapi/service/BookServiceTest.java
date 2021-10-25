package com.cursoudemy.libraryapi.service;

import com.cursoudemy.libraryapi.exception.BusinessException;
import com.cursoudemy.libraryapi.model.entity.Book;
import com.cursoudemy.libraryapi.model.entity.repository.BookRepository;
import com.cursoudemy.libraryapi.service.service.impl.BookServiceImp;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;
    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setUp(){
        this.service = new BookServiceImp(repository);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest(){
        //cenario
        Book book = createdValidBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
        Mockito.when(repository.save(book))
                .thenReturn(Book.builder().id(11L).author("Artur").isbn("001").title("As aventuras").build());

        //execucao
        Book savedBook = service.save(book);

        //teste
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getAuthor()).isEqualTo("Artur");
        assertThat(savedBook.getTitle()).isEqualTo("As aventuras");
        assertThat(savedBook.getIsbn()).isEqualTo("001");
    }

    private Book createdValidBook() {
        return Book.builder()
                .author("Artur").title("As aventuras").isbn("001").build();
    }

    @Test
    @DisplayName("Deve Lançar erro quando tentar salvar isbn duplicado")
    public void shouldNotSaveABookWithDuplicatedISBN(){
        //cenario
        Book book = createdValidBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);
        //execucao
        Throwable exception = Assertions.catchThrowable(() -> service.save(book));

        //verificação
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn já criado");
        Mockito.verify(repository, Mockito.never()).save(book);

    }
    @Test @DisplayName("Deve obter um livro pelo id")
    public void getByIdTest(){
        Long id = 1l;
        Book book = createdValidBook();
        book.setId(id);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));

        //execucao
        Optional<Book> foundBook = service.getById(id);

        //teste
        assertThat(foundBook.isPresent()).isTrue();
        assertThat(foundBook.get().getId()).isEqualTo(id);
        assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
        assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
        assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());


    }
    @Test @DisplayName("Deve retornar falso quando o id nao existir")
    public void getNotFoundIdTest(){
        Long id = 1l;
        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        //execucao
        Optional<Book> book = service.getById(id);

        //teste
        assertThat(book.isPresent()).isFalse();


    }

    @Test
    @DisplayName("Deve atualizar um livro")
    public void updateBookTest(){
        //cenario
        Long id = 1l;
        //livro a atualizar
        Book updatingBook = Book.builder().id(id).build();

        //simulaçao
        Book updatedBook = createdValidBook();
        updatedBook.setId(id);
        Mockito.when(repository.save(updatingBook)).thenReturn(updatedBook);

        //execucção
        Book book = service.update(updatingBook);

        //teste
        assertThat(book.getId()).isEqualTo(updatedBook.getId());
        assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());
        assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());
        assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());
    }

    @Test
    @DisplayName("Deve lançar erro ao atualizar um livro e nao encontrar o id ou livro nulo")
    public void updateNotFoundBookTest(){
        //cenario
        Book book = createdValidBook();
        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        //execucao
        Throwable exception = Assertions.catchThrowable(() -> service.update(book));

        //teste
        //verificação
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Id não pode ser nulo");
        Mockito.verify(repository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest(){
        //cenario
        Long id = 1l;
        Book book = createdValidBook();
        book.setId(id);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));

        //execucao
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.delete(book));

        //teste
        Mockito.verify(repository, Mockito.times(1)).delete(book);
    }
    @Test
    @DisplayName("Deve lançar erro ao deletar um livro e nao encontrar o id ou livro nulo")
    public void deleteNotFoundBookTest(){
        //cenario
        Book book = new Book();
        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        //execucao
        //execucao
        Throwable exception = Assertions.catchThrowable(() -> service.delete(book));

        //teste
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Id não pode ser nulo");
        Mockito.verify(repository, Mockito.never()).delete(book);
    }

}
