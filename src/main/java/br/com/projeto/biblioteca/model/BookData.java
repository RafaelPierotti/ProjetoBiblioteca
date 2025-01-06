package br.com.projeto.biblioteca.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BookData(String title,
                       String subtitle,
                       List<String> authors,
                       String publisher,
                       String description) {
}
