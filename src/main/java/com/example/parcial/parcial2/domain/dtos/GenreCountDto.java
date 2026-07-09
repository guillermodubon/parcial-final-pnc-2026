package com.example.parcial.parcial2.domain.dtos;

import com.example.parcial.parcial2.domain.entities.Genre;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GenreCountDto {
    private Genre genre;
    private long count;

    public GenreCountDto(Genre genre, Long count) {
        this.genre = genre;
        this.count = count == null ? 0 : count;
    }
}