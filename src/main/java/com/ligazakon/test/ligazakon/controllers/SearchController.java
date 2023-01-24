package com.ligazakon.test.ligazakon.controllers;


import com.ligazakon.test.ligazakon.dto.SearchResult;
import com.ligazakon.test.ligazakon.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@RestController
public class SearchController {

    private SearchService service;

    @Autowired
    public SearchController(SearchService service) {
        this.service = service;
    }

    @Operation(summary = "Get companies from file")
    @ApiResponse(responseCode = "200", description = "Companies are found.",
            content = {@Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = SearchResult.class)))})
    @PostMapping(value = "/companies")
    public ResponseEntity searchCompanies(@RequestParam("file") MultipartFile file) throws IOException, ExecutionException, InterruptedException {
        return new ResponseEntity(service.searchCompanies(file), HttpStatus.OK);
    }

    @Operation(summary = "Get sentences from file")
    @ApiResponse(responseCode = "200", description = "Sentences are found.",
            content = {@Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = SearchResult.class)))})
    @PostMapping(value = "/sentences")
    public ResponseEntity searchSentences(@RequestParam("file") MultipartFile file) throws IOException, ExecutionException, InterruptedException {
        return new ResponseEntity(service.searchSentences(file), HttpStatus.OK);
    }

    @Operation(summary = "Get date and sum from file")
    @ApiResponse(responseCode = "200", description = "Dates and sums are found",
            content = {@Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = SearchResult.class)))})
    @PostMapping(value = "/date-sums")
    public ResponseEntity searchDateSums(@RequestParam("file") MultipartFile file) throws IOException, ExecutionException, InterruptedException {
        return new ResponseEntity(service.searchDatesAndSums(file), HttpStatus.OK);
    }

}
