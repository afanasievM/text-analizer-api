package com.ligazakon.test.ligazakon.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeSet;

@Component
@Slf4j
public class CompanyPatternRepository {

    private Set<String> companies;

    @Value("${dict.filename}")
    private String filename;

    @Autowired
    public CompanyPatternRepository() {
    }

    public Set<String> findAll() {
        return companies;
    }

    @EventListener(ApplicationReadyEvent.class)
    private void initializeCompanies() throws IOException {
        companies = new TreeSet<>();
        if (!pathIsCorrect(filename)) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                companies.add(line.strip());
            }
        }
        log.info("Companies initialized");
    }

    private boolean pathIsCorrect(String path) {
        if (path == null) {
            throw new IllegalArgumentException("Path is NULL");
        }
        if (path.isEmpty()) {
            throw new IllegalArgumentException("Path is empty");
        }
        if (Files.isRegularFile(Paths.get(path))) {
            return true;
        } else {
            throw new IllegalArgumentException("Can't find file " + path);
        }
    }

}
