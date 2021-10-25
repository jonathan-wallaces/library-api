package com.cursoudemy.libraryapi.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Loan {
    private Long id;
    private String customer;
    private Book book;
    private LocalDate loanDate;
    private Boolean returned;
}
