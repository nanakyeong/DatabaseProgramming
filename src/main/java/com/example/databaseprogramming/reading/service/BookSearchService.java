package com.example.databaseprogramming.reading.service;

import com.example.databaseprogramming.reading.dto.BookSearchDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookSearchService {

    // application.properties에 설정할 키 값
    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.client.secret}")
    private String clientSecret;

    public List<BookSearchDto> searchBooks(String query) {
        // 1. 네이버 API 호출 URL 생성
        URI uri = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com")
                .path("/v1/search/book.json")
                .queryParam("query", query)
                .queryParam("display", 10) // 10개만 검색
                .queryParam("start", 1)
                .encode()
                .build()
                .toUri();

        // 2. 헤더에 Client ID/Secret 추가
        RequestEntity<Void> req = RequestEntity
                .get(uri)
                .header("X-Naver-Client-Id", clientId)
                .header("X-Naver-Client-Secret", clientSecret)
                .build();

        // 3. 요청 및 응답 파싱
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(req, String.class);

        // 4. JSON 파싱 후 DTO 리스트로 변환
        return parseJson(response.getBody());
    }

    private List<BookSearchDto> parseJson(String body) {
        List<BookSearchDto> list = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(body);
            JsonNode items = root.path("items");

            for (JsonNode item : items) {
                list.add(BookSearchDto.builder()
                        .title(item.path("title").asText().replaceAll("<[^>]*>", "")) // 태그 제거
                        .author(item.path("author").asText().replaceAll("<[^>]*>", ""))
                        .coverUrl(item.path("image").asText())
                        .publisher(item.path("publisher").asText())
                        .isbn(item.path("isbn").asText())
                        .build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}