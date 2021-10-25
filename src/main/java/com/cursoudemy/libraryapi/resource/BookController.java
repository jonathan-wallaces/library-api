package com.cursoudemy.libraryapi.resource;

import com.cursoudemy.libraryapi.api.exception.ApiErrors;
import com.cursoudemy.libraryapi.dto.BookDTO;
import com.cursoudemy.libraryapi.exception.BusinessException;
import com.cursoudemy.libraryapi.model.entity.Book;
import com.cursoudemy.libraryapi.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/books")
public class BookController {
    @Autowired
    private BookService service;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody @Valid BookDTO dto){
        Book entity = modelMapper.map(dto, Book.class);
        entity = service.save(entity);
        return modelMapper.map(entity, BookDTO.class);
    }

    @GetMapping("{id}")
    public BookDTO get(@PathVariable Long id){
        return service
                .getById(id)
                .map(book -> modelMapper.map(book, BookDTO.class))
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Long id){
       Book book = service.getById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));
       service.delete(book);

    }
    @PutMapping("{id}")
    public BookDTO updateBook(@PathVariable Long id, @Valid @RequestBody BookDTO dto) {
        return service.getById(id).map( book -> {
            book.setAuthor(dto.getAuthor());
            book.setTitle(dto.getTitle());
            service.update(book);
            return modelMapper.map(book, BookDTO.class);
    }).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));


    }


}
