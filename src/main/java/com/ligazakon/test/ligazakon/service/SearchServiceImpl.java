package com.ligazakon.test.ligazakon.service;

import com.ligazakon.test.ligazakon.dto.SearchResult;
import com.ligazakon.test.ligazakon.repository.CompanyPatternRepository;
import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.namefind.DictionaryNameFinder;
import opennlp.tools.namefind.DocumentNameFinder;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.TokenNameFinderFactory;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerFactory;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.postag.WordTagSampleStream;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.tokenize.Detokenizer;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.StringList;
import opennlp.tools.util.TrainingParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.webjars.NotFoundException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.regex.Pattern.MULTILINE;

@Service
public class SearchServiceImpl implements SearchService {

    private CompanyPatternRepository repository;
    private ThreadPoolTaskExecutor executor;

    private SentenceDetector sentenceDetector;

    @Autowired
    public SearchServiceImpl(CompanyPatternRepository repository, @Qualifier("taskExecutor") ThreadPoolTaskExecutor executor,
                             SentenceDetector sentenceDetector) {
        this.repository = repository;
        this.executor = executor;
        this.sentenceDetector = sentenceDetector;
    }

    @Override
    public List<SearchResult> searchCompanies(MultipartFile file) throws IOException, InterruptedException {
        List<SearchResult> result = new ArrayList<>();
        String text = getFileContent(file);
        Set<String> companies = repository.findAll();
        Dictionary dictionary = new Dictionary();
        WhitespaceTokenizer tokenizer = WhitespaceTokenizer.INSTANCE;
        companies.forEach(line -> dictionary.put(new StringList(tokenizer.tokenize(line))));
        DictionaryNameFinder finder = new DictionaryNameFinder(dictionary);
        Arrays.stream(sentenceDetector.sentDetect(text)).forEach(sentence -> {
                    String[] tokens = tokenizer.tokenize(sentence);
                    Arrays.stream(finder.find(tokens))
                            .map(span -> String.join(" ", Arrays.copyOfRange(tokens, span.getStart(), span.getEnd())))
                            .distinct()
                            .filter(companies::contains)
                            .forEach(company -> {
                                Pattern pattern = Pattern.compile(company, Pattern.MULTILINE + Pattern.CASE_INSENSITIVE);
                                search(pattern, text, result);
                            });
                }
        );
        checkResult(result);
        return result;
    }

    @Override
    public List<SearchResult> searchDatesAndSums(MultipartFile file) throws IOException {
        List<SearchResult> result = new ArrayList<>();
        String text = getFileContent(file);
        searchDates(text, result);
        searchSums(text, result);
        checkResult(result);
        return result;
    }

    @Override
    public List<SearchResult> searchSentences(MultipartFile file) throws IOException {
        List<SearchResult> result = new ArrayList<>();
        String text = getFileContent(file);
        Arrays.stream(sentenceDetector.sentPosDetect(text)).forEach(
                span -> {
                    String searched = text.substring(span.getStart(), span.getEnd());
                    SearchResult searchResult = new SearchResult(span.getStart(), searched.length(), searched);
                    result.add(searchResult);
                }
        );
        return result;
    }

    private void searchDates(String text, List<SearchResult> result) {
        List<String> patterns = new ArrayList<>();
        patterns.add("\\w+\\s+\\d{1,2},\\s+\\d{4}");
        patterns.add("\\d{1,2}(st|nd|rd|th)\\s+day\\s+of\\s+\\w+,\\s+\\d{4}");
        Pattern pattern = Pattern
                .compile(String.join("|", patterns), MULTILINE + Pattern.CASE_INSENSITIVE);
        search(pattern, text, result);
    }

    private void searchSums(String text, List<SearchResult> result) {
        List<String> patterns = new ArrayList<>();
        patterns.add("\\$\\s*\\d{1,3}(,\\d{3})+(\\.\\d{1,3})*");
        patterns.add("\\d{1,3}(,\\d{3})+(\\.\\d{1,3})*");
        Pattern pattern = Pattern
                .compile(String.join("|", patterns), MULTILINE);
        search(pattern, text, result);
    }

    private String getFileContent(MultipartFile file) throws IOException {
        return new String(file.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    private void search(Pattern pattern, String text, List<SearchResult> resultList) {
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String searchedString = text.substring(matcher.start(), matcher.end()).strip();
            SearchResult searchedResult = new SearchResult(matcher.start(), searchedString.length(), searchedString);
            resultList.add(searchedResult);
        }
    }

    private void checkResult(List<SearchResult> result) {
        if (result.isEmpty()) {
            throw new NotFoundException("Not found");
        }
    }

}
