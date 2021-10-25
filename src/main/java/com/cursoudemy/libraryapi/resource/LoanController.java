package com.cursoudemy.libraryapi.resource;

import com.cursoudemy.libraryapi.dto.LoanDto;
import com.cursoudemy.libraryapi.model.entity.Book;
import com.cursoudemy.libraryapi.model.entity.Loan;
import com.cursoudemy.libraryapi.service.BookService;
import com.cursoudemy.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final BookService bookService;
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Long createLoan(@RequestBody @Valid LoanDto loanDto){
       Book book =  bookService.getBookByIsbn(loanDto.getIsbn())
               .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isbn."));
       Loan loan = Loan.builder()
               .book(book)
               .loanDate(LocalDate.now())
               .customer(loanDto.getCustomer())
               .build();
       loan = loanService.save(loan);
       return loan.getId();
    }
}
