package com.ll.exam.article.service;

import com.ll.exam.annotation.Autowired;
import com.ll.exam.annotation.Service;
import com.ll.exam.article.dto.ArticleDto;
import com.ll.exam.article.repository.ArticleRepository;
import com.ll.exam.mymap.MyMap;

import java.util.List;

@Service
public class ArticleService {
    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private MyMap myMap;

    public List<ArticleDto> getArticles() {
        return articleRepository.getArticles();
    }

    public ArticleDto getArticleById(long id) {
        return articleRepository.getArticleById(id);
    }

    public long getArticlesCount() {
        return articleRepository.getArticlesCount();
    }

    public long write(String title, String body) {
        return write(title, body, false);
    }

    public long write(String title, String body, boolean isBlind) {
        return articleRepository.write(title, body, isBlind);
    }

    public void modify(long id, String title, String body) {
        modify(id, title, body, false);
    }

    public void modify(long id, String title, String body, boolean isBlind) {
        articleRepository.modify(id, title, body, isBlind);
    }

    public void delete(long id) {
        articleRepository.delete(id);
    }

    public ArticleDto js_getPreArticle(int id) {
        return articleRepository.js_getPreArticle(id);
    }

    public ArticleDto getPrevArticle(ArticleDto articleDto) {
        return getPrevArticle(articleDto.getId());
    }

    public ArticleDto getPrevArticle(long id) {
        return articleRepository.getPrevArticle(id);
    }

    public ArticleDto js_getNextArticle(int id) {
        return articleRepository.js_getNextArticle(id);
    }


    public ArticleDto getNextArticle(long id) {
        return articleRepository.getNextArticle(id);
    }

    public void blind(int id1, int id2) {
        articleRepository.blind(id1, id2);
    }
}
