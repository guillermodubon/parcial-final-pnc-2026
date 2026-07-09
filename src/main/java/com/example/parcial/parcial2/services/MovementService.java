package com.example.parcial.parcial2.services;

import com.example.parcial.parcial2.domain.dtos.MovementRequestDto;
import com.example.parcial.parcial2.domain.entities.Book;
import com.example.parcial.parcial2.domain.entities.Lector;
import com.example.parcial.parcial2.domain.entities.Movement;
import com.example.parcial.parcial2.domain.entities.MovementType;
import com.example.parcial.parcial2.repositories.BookRepository;
import com.example.parcial.parcial2.repositories.LectorRepository;
import com.example.parcial.parcial2.repositories.MovementRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class MovementService {

    private final MovementRepository movementRepository;
    private final LectorRepository lectorRepository;
    private final BookRepository bookRepository;

    public MovementService(MovementRepository movementRepository,
                           LectorRepository lectorRepository,
                           BookRepository bookRepository) {
        this.movementRepository = movementRepository;
        this.lectorRepository = lectorRepository;
        this.bookRepository = bookRepository;
    }

    public Movement borrowBook(MovementRequestDto dto) {
        Lector lector = findLector(dto.getEmail());
        Book book = findBook(dto.getIsbn());

        if (!book.isAvailable() || book.getAvailableCount() <= 0) {
            throw new RuntimeException("Book is not available");
        }

        book.setAvailableCount(book.getAvailableCount() - 1);
        book.setAvailable(book.getAvailableCount() > 0);
        bookRepository.save(book);

        return saveMovement(lector, book, MovementType.BORROWING);
    }

    public Movement returnBook(MovementRequestDto dto) {
        Lector lector = findLector(dto.getEmail());
        Book book = findBook(dto.getIsbn());

        List<Movement> movements = movementRepository
                .findByLectorIdAndBookIdOrderByTimestampDesc(lector.getId(), book.getId());
        if (movements.isEmpty() || movements.get(0).getType() != MovementType.BORROWING) {
            throw new RuntimeException("There is no active borrowing for this book");
        }

        book.setAvailableCount(book.getAvailableCount() + 1);
        book.setAvailable(true);
        bookRepository.save(book);

        return saveMovement(lector, book, MovementType.RETURN);
    }

    private Lector findLector(String email) {
        return lectorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Lector not found"));
    }

    private Book findBook(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new RuntimeException("Book not found"));
    }

    private Movement saveMovement(Lector lector, Book book, MovementType type) {
        Movement movement = new Movement();
        movement.setLector(lector);
        movement.setBook(book);
        movement.setTimestamp(Instant.now());
        movement.setType(type);

        return movementRepository.save(movement);
    }
}
