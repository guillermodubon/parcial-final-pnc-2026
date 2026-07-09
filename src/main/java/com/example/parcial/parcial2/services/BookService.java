package com.example.parcial.parcial2.services;

import com.example.parcial.parcial2.domain.dtos.BookRequestDto;
import com.example.parcial.parcial2.domain.dtos.GenreCountDto;
import com.example.parcial.parcial2.domain.entities.Book;
import com.example.parcial.parcial2.domain.entities.Genre;
import com.example.parcial.parcial2.repositories.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book createBook(BookRequestDto dto) {
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setGenre(parseGenre(dto.getGenre()));
        book.setIsbn(dto.getIsbn());
        book.setAvailable(dto.isAvailable());
        book.setAvailableCount(dto.getAvailableCount());
        book.setActive(true);
        return bookRepository.save(book);
    }

    public Book getBookById(UUID id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
    }

    public List<Book> getAllBooks(String author, String genre) {
        String normalizedAuthor = normalizeText(author);
        Genre normalizedGenre = parseGenre(genre);

        if (normalizedAuthor != null && normalizedGenre != null) {
            return bookRepository.findByAuthorAndGenre(normalizedAuthor, normalizedGenre);
        } else if (normalizedAuthor != null) {
            return bookRepository.findByAuthor(normalizedAuthor);
        } else if (normalizedGenre != null) {
            return bookRepository.findByGenre(normalizedGenre);
        }
        return bookRepository.findAll();
    }

    public Book updateBook(UUID id, BookRequestDto dto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        if (dto.getGenre() != null) {
            book.setGenre(parseGenre(dto.getGenre()));
        }
        book.setIsbn(dto.getIsbn());
        book.setAvailable(dto.isAvailable());
        book.setAvailableCount(dto.getAvailableCount());
        return bookRepository.save(book);
    }

    public void deleteBook(UUID id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        book.setActive(false);
        bookRepository.save(book);
    }

    public List<GenreCountDto> getGenresAvailable() {
        return bookRepository.findAvailableCountByGenre();
    }

    private Genre parseGenre(String genre) {
        String normalizedGenre = normalizeText(genre);
        return normalizedGenre == null ? null : Genre.valueOf(normalizedGenre.toUpperCase());
    }

    private String normalizeText(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}
