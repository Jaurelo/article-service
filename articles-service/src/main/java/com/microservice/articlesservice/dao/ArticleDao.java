package com.microservice.articlesservice.dao;

import com.microservice.articlesservice.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleDao extends JpaRepository<Article, Integer> {

    Article findById(int id);
    List<Article> findByPrixGreaterThan(int prixLimit);
    List<Article> findByPrixLessThan(int prixLimit);
    List<Article> findByNomContains(String nom);
    List<Article> find();



}
