package com.ligazakon.test.ligazakon.service;

import com.ligazakon.test.ligazakon.dto.SearchResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface SearchService {
    List<SearchResult> searchCompanies(MultipartFile file) throws IOException, ExecutionException, InterruptedException;
    List<SearchResult> searchDatesAndSums(MultipartFile file) throws IOException, ExecutionException, InterruptedException;
    List<SearchResult> searchSentences(MultipartFile file) throws IOException, ExecutionException, InterruptedException;

}
