package br.com.projeto.biblioteca.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BookData(@JsonProperty("title") String title,
                       @JsonProperty("subtitle") String subtitle,
                       @JsonProperty("authors") List<String> authors,
                       @JsonProperty("publisher") String publisher,
                       @JsonProperty String description) {
}
