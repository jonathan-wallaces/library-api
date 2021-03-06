package com.cursoudemy.libraryapi.resource;

import com.cursoudemy.libraryapi.dto.BookDTO;
import com.cursoudemy.libraryapi.exception.BusinessException;
import com.cursoudemy.libraryapi.model.entity.Book;
import com.cursoudemy.libraryapi.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


import java.util.Optional;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = BookController.class)
@AutoConfigureMockMvc
public class bookControllerTest {

    static String BOOK_API="/api/books";

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService service;

    private BookDTO createdNewBookDto() {
        return BookDTO.builder().id(1l).author("Artur").title("As aventuras").isbn("001").build();
    }

    @Test @DisplayName("Deve criar um livro com sucesso.")
    public void createdBookTest() throws Exception{
        BookDTO dto = createdNewBookDto();
        Book savedBook = createdNewBook();
        BDDMockito.given(service.save(Mockito.any(Book.class)))
                .willReturn(savedBook);
        String json = new ObjectMapper().writeValueAsString(dto);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);
        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(dto.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(dto.getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(dto.getIsbn()));
    }

    private Book createdNewBook() {
        return Book.builder().id(1L).author("Artur").title("As aventuras").isbn("001").build();
    }


    @Test @DisplayName("Deve lan??ar um erro de valida????o quando n??o houver dados suficientes.")
    public void createdInvalidBook() throws Exception {

        String json = new ObjectMapper().writeValueAsString(new BookDTO());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(3)));
    }

    @Test @DisplayName("Lan??ando erro ao cadastrar isbn duplicado")
    public void createdBookWithDuplicatedIsbn() throws Exception{
        BookDTO dto = createdNewBookDto();
        String json = new ObjectMapper().writeValueAsString(dto);
        String mensagemErro="Isbn j?? criado";
        BDDMockito.given(service.save(Mockito.any(Book.class)))
                .willThrow(new BusinessException(mensagemErro));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);
        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0]").value(mensagemErro));
    }

    @Test
    @DisplayName("Deve obter as informa????s de um livro")
    public void getBookDetailsTest() throws Exception{
       //cenario (given)
        Long id = 1L;

        Book book = Book.builder()
                .id(id)
                .title(createdNewBook().getTitle())
                .author(createdNewBook().getAuthor())
                .isbn(createdNewBook().getIsbn())
                .build();
        BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));

        //execucao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/"+id))
                .accept(MediaType.APPLICATION_JSON);
        //teste

        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(createdNewBook().getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(createdNewBook().getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(createdNewBook().getIsbn()));

    }

    @Test
    @DisplayName("Deve retornar resource not found quando o livro procurado n??o existir")
    public void bookNotFoundTest() throws Exception{
        //cenario
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        //execucao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/"+1))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        //teste
        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest() throws Exception{
       //cenario
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.of(Book.builder().id(1l).build()));
        //execucao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/"+1));
        //teste
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar resource not found quando n??o encontrar o livro a ser deletado")
    public void deleteNotFoundBookTest() throws Exception{
        //cenario
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());
        //execucao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/"+1));
        //teste
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
    @Test @DisplayName("Deve atualizar um livro")
    public void updateBookTest() throws Exception{
        //cenario
        Long id = 1L;
        String json = new ObjectMapper().writeValueAsString(createdNewBook());

        Book updatingBook = Book.builder().id(1L).isbn("321").author("some author").title("some title").build();
        BDDMockito.given(service.getById(id)).willReturn(Optional.of(updatingBook));
        //Book updateBook = Book.builder().id(id).author("Artur").title("As aventuras").isbn("321").build();
        Book updateBook = Book.builder().id(id).author("Artur").title("As aventuras").isbn("321").build();
        BDDMockito.given(service.update(updatingBook)).willReturn(updateBook);

        //execu????o
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/"+1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        //teste
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(createdNewBook().getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(createdNewBook().getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value("321"));
    }
    @Test @DisplayName("Deve retornar 404 quando tentar atualizar um livro inexistente.")
    public void updateNotFoundBookTest() throws Exception{
        //cenario
        String json = new ObjectMapper().writeValueAsString(createdNewBook());
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        //execu????o
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/"+1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        //teste
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

}
