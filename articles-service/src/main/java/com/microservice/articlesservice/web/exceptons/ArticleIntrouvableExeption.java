package com.microservice.articlesservice.web.exceptons;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ArticleIntrouvableExeption extends RuntimeException {
    public ArticleIntrouvableExeption(String s) {
        super(s);
    }
}
