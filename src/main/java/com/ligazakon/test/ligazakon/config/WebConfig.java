package com.ligazakon.test.ligazakon.config;

import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.io.IOException;
import java.io.InputStream;


@Configuration
@EnableWebMvc
public class WebConfig {

    @Bean
    public SentenceDetector sentenceDetector(SentenceModel model) {
        SentenceDetector sentenceDetector = new SentenceDetectorME(model);
        return sentenceDetector;
    }

    @Bean
    public SentenceModel sentenceModel() throws IOException {
        InputStream is = getClass().getResourceAsStream("/models/en-sent.bin");
        return new SentenceModel(is);
    }

}
