package com.example.databaseprogramming.reading.controller;

import com.example.databaseprogramming.reading.dto.BookSearchDto;
import com.example.databaseprogramming.reading.service.BookSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BookSearchController {

    private final BookSearchService bookSearchService;

    @GetMapping("/books")
    public ResponseEntity<List<BookSearchDto>> search(@RequestParam String query) {
        return ResponseEntity.ok(bookSearchService.searchBooks(query));
    }
}