package com.microservice.articlesservice.web.controller;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.microservice.articlesservice.DTO.ArticleDto;
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
import java.util.ArrayList;
import java.util.List;

@RestController
public class ArticleController {

    @Autowired
    private ArticleDao articleDao;

    @ApiOperation(value = "Récupérer tous les articles")
    @RequestMapping(value = "/Articles", method = RequestMethod.GET)
    public MappingJacksonValue listeArticles() {
        List<Article> articles = articleDao.findAll();

        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat", "id");
        FilterProvider listDeNosFiltres = new SimpleFilterProvider().addFilter("MonFiltreDynamique", monFiltre);

        MappingJacksonValue articlesFiltres = new MappingJacksonValue(articles);

        articlesFiltres.setFilters(listDeNosFiltres);
        ;
        return articlesFiltres;
    }

    //Récupérer un article par son Id
    @ApiOperation(value = "Récupérer un article grâce à son ID à condition que celui-ci soit en stock!")
    @GetMapping(value = "/Articles/id/{id}")
    public MappingJacksonValue afficherUnArticle(@PathVariable int id) throws ArticleIntrouvableExeption {
        Article article = articleDao.findById(id);
        if (article == null) {
            throw new ArticleIntrouvableExeption("L'article avec l'id " + id + " est INTROUVABLE");
        }
        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat", "id");
        FilterProvider listDeNosFiltres = new SimpleFilterProvider().addFilter("MonFiltreDynamique", monFiltre);

        MappingJacksonValue articlesFiltres = new MappingJacksonValue(article);

        articlesFiltres.setFilters(listDeNosFiltres);
        ;
        return articlesFiltres;
    }


    //Récupérer les article avec un prix supprieur au param
    @ApiOperation(value = "Récupérer les articles avec un prix suppérieur au paramètre")
    @GetMapping(value = "/Articles/prixGreater/{prixLimit}")
    public MappingJacksonValue afficherListeArticlePrixGreater(@PathVariable int prixLimit) {
        List<Article> listeArticle =  articleDao.findByPrixGreaterThan(prixLimit);

        if (listeArticle == null || listeArticle.isEmpty()) {
            throw new ArticleIntrouvableExeption("Aucun article ne vaut plus de :"+prixLimit+" €");
        }
        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat", "id");
        FilterProvider listDeNosFiltres = new SimpleFilterProvider().addFilter("MonFiltreDynamique", monFiltre);

        MappingJacksonValue articlesFiltres = new MappingJacksonValue(listeArticle);

        articlesFiltres.setFilters(listDeNosFiltres);
        ;
        return articlesFiltres;
    }

    //Récupérer les article avec un prix infèrieur au param
    @ApiOperation(value = "Récupérer les articles avec un prix infèrieur au paramètre")
    @GetMapping(value = "/Articles/prixLess/{prixLimit}")
    public MappingJacksonValue afficherListeArticlePrixLess(@PathVariable int prixLimit) {
        List<Article> listeArticle =  articleDao.findByPrixLessThan(prixLimit);

        if (listeArticle == null || listeArticle.isEmpty()) {
            throw new ArticleIntrouvableExeption("Aucun article ne vaut moins de :"+prixLimit+" €");
        }
        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat", "id");
        FilterProvider listDeNosFiltres = new SimpleFilterProvider().addFilter("MonFiltreDynamique", monFiltre);

        MappingJacksonValue articlesFiltres = new MappingJacksonValue(listeArticle);

        articlesFiltres.setFilters(listDeNosFiltres);
        ;
        return articlesFiltres;
    }

    //Récupérer les article avec un prix supprieur au param
    @ApiOperation(value = "Récupèrer les article avec une partie de leur nom en paramètre")
    @GetMapping(value = "/Articles/nom/{nom}")
    public MappingJacksonValue afficherListeArticleByNom(@PathVariable String nom) {
        List<Article> articleList = articleDao.findByNomContains(nom);
        if (articleList == null || articleList.isEmpty()) {
            throw new ArticleIntrouvableExeption("Aucun article trouvé");
        }
        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat", "id");
        FilterProvider listDeNosFiltres = new SimpleFilterProvider().addFilter("MonFiltreDynamique", monFiltre);

        MappingJacksonValue articlesFiltres = new MappingJacksonValue(articleList);

        articlesFiltres.setFilters(listDeNosFiltres);
        ;
        return articlesFiltres;
    }

    //Ajouter un article
    @ApiOperation(value = "Ajoute un article")
    @PostMapping(value = "/Articles")
    public ResponseEntity<Object> ajouterArticle(@RequestBody Article article) {

        Article articleAdded = articleDao.save(article);

        if (articleAdded == null) {
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
    @DeleteMapping(value = "/Articles/{id}")
    public void supprimerArticle(@PathVariable int id) {
        articleDao.deleteById(id);
    }

    @ApiOperation(value = "Edite un article")
    @PutMapping(value = "/Articles")
    public void updateArticle(@RequestBody Article article) {
        articleDao.save(article);
    }


    //Calculer la marge des articles

    @ApiOperation(value = "Permet de calculer la marge sur chaque article")
    @GetMapping(value = "/AdminArticle")
    public MappingJacksonValue calculerMargeArticle() {
        List<Article> articles = articleDao.findAll();
        List<ArticleDto> articlesWithMarge = new ArrayList<>();
        for (Article article : articles) {
            ArticleDto articleDto = new ArticleDto();
            int marge = article.getPrix() - article.getPrixAchat();
            articleDto.setNom(article.getNom());
            articleDto.setMarge(marge);
            articlesWithMarge.add(articleDto);
        }

       // return articlesWithMarge;

        if (articlesWithMarge.isEmpty()) {
            throw new ArticleIntrouvableExeption("Aucun article trouvé");
        }
        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept();
        FilterProvider listDeNosFiltres = new SimpleFilterProvider().addFilter("MonFiltreDynamique", monFiltre);

        MappingJacksonValue articlesFiltres = new MappingJacksonValue(articlesWithMarge);

        articlesFiltres.setFilters(listDeNosFiltres);
        ;
        return articlesFiltres;
    }

    /*


    //Calculer les marge des articles
    @ApiOperation(value = "Permet de calculer la marge d'un article par son id")
    @GetMapping(value = "/AdminArticle")
    public List<Integer> calculerMargeArticle(@PathVariable int id) {
        List<Article> articles = articleDao.findAll();
        List<Integer> margesArticles = new ArrayList<>();
        for(Article article : articles){
            int marge = article.getPrix()-article.getPrixAchat();
            margesArticles.add(marge);
        }
        return margesArticles;
    }
*/
    @ApiOperation(value = "Récupérer tous les articles triés par nom")
    @RequestMapping(value = "/Articlesordered", method = RequestMethod.GET)
    public MappingJacksonValue trierArticlesParOrdreAlphabetique() {
        List<Article> articles = articleDao.findByOrderByNom();

        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat", "id");
        FilterProvider listDeNosFiltres = new SimpleFilterProvider().addFilter("MonFiltreDynamique", monFiltre);

        MappingJacksonValue articlesFiltres = new MappingJacksonValue(articles);

        articlesFiltres.setFilters(listDeNosFiltres);
        ;
        return articlesFiltres;
    }


}




