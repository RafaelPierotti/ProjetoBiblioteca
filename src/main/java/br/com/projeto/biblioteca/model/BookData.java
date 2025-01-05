package br.com.projeto.biblioteca.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BookData(String title,
                       String subtitle,
                       String authors,
                       String publisher,
                       String description) {
}
