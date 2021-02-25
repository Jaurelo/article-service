package com.microservice.articlesservice.web.controller;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.microservice.articlesservice.dao.ArticleDao;
import com.microservice.articlesservice.model.Article;
import com.microservice.articlesservice.web.exceptons.ArticleIntrouvableExeption;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.Servlet;
import java.net.URI;
import java.util.List;

@RestController
public class ArticleController {

    @Autowired
    private ArticleDao articleDao;
    @ApiOperation(value = "Récupérer tous les articles")
    @RequestMapping(value="/Articles", method= RequestMethod.GET)
    public MappingJacksonValue listeArticles() {
        List<Article> articles = articleDao.findAll();

        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat","id");
        FilterProvider listDeNosFiltres = new SimpleFilterProvider().addFilter("MonFiltreDynamique",monFiltre);

        MappingJacksonValue articlesFiltres = new MappingJacksonValue(articles);

        articlesFiltres.setFilters(listDeNosFiltres);;
        return articlesFiltres;
    }

    //Récupérer un article par son Id
    @ApiOperation(value = "Récupérer un article grâce à son ID à condition que celui-ci soit en stock!")
    @GetMapping(value="/Articles/id/{id}")
    public Article afficherUnArticle(@PathVariable int id) throws ArticleIntrouvableExeption {
        Article article = articleDao.findById(id);
        if(article==null) {
            throw new ArticleIntrouvableExeption("L'article avec l'id " + id + " est INTROUVABLE");
        }
        return article;
    }


    //Récupérer les article avec un prix supprieur au param
    @ApiOperation(value = "Récupérer les articles avec un prix suppérieur au paramètre")
    @GetMapping(value="/Articles/prixGreater/{prixLimit}")
    public List<Article> afficherListeArticlePrixGreater(@PathVariable int prixLimit) {
        return articleDao.findByPrixGreaterThan(prixLimit);
    }

    //Récupérer les article avec un prix infèrieur au param
    @ApiOperation(value = "Récupérer les articles avec un prix infèrieur au paramètre")
    @GetMapping(value="/Articles/prixLess/{prixLimit}")
    public List<Article> afficherListeArticlePrixLess(@PathVariable int prixLimit) {
        return articleDao.findByPrixLessThan(prixLimit);
    }

    //Récupérer les article avec un prix supprieur au param
    @ApiOperation(value = "Récupèrer les article avec une partie de leur nom en paramètre")
    @GetMapping(value="/Articles/nom/{nom}")
    public List<Article> afficherListeArticleByNom(@PathVariable String nom) {
        return articleDao.findByNomContains(nom);
    }

    //Ajouter un article
    @ApiOperation(value = "Ajoute un article")
    @PostMapping(value = "/Articles")
    public ResponseEntity<Object> ajouterArticle(@RequestBody Article article){

        Article articleAdded = articleDao.save(article);

        if(articleAdded == null) {
            return ResponseEntity.noContent().build();
        }

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/id/{id}")
                    .buildAndExpand(articleAdded.getId())
                    .toUri();
        return ResponseEntity.created(location).build();
    }

    @ApiOperation(value = "Supprime un article")
    @DeleteMapping (value = "/Articles/{id}")
    public void supprimerArticle(@PathVariable int id){
        articleDao.deleteById(id);
    }

    @ApiOperation(value = "Edite un article")
    @PutMapping ( value = "/Articles")
    public void updateArticle(@RequestBody Article article){
        articleDao.save(article);
    }



}
