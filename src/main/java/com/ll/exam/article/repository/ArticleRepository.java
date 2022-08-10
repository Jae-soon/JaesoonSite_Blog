package com.ll.exam.article.repository;

import com.ll.exam.annotation.Autowired;
import com.ll.exam.annotation.Repository;
import com.ll.exam.article.dto.ArticleDto;
import com.ll.exam.mymap.MyMap;
import com.ll.exam.mymap.SecSql;

import java.util.List;

@Repository
public class ArticleRepository {
    @Autowired
    private MyMap myMap;

    public List<ArticleDto> getArticles() {
        SecSql sql = myMap.genSecSql();
        sql
                .append("SELECT *")
                .append("FROM article")
                .append("ORDER BY id DESC");
        return sql.selectRows(ArticleDto.class);
    }

    public ArticleDto getArticleById(long id) {
        SecSql sql = myMap.genSecSql();
        sql
                .append("SELECT * FROM article WHERE id = ?", id);
        ArticleDto articleDto = sql.selectRow(ArticleDto.class);

        return articleDto;
    }

    public long getArticlesCount() {
        SecSql sql = myMap.genSecSql();
        sql
                .append("SELECT count(*)")
                .append("FROM article");

        return sql.selectLong();
    }

    public long write(String title, String body, boolean isBlind) {
        SecSql sql = myMap.genSecSql();
        sql
                .append("INSERT INTO article")
                .append("SET createdDate = NOW()")
                .append(", modifiedDate = NOW()")
                .append(", title = ?", title)
                .append(", body = ?", body)
                .append(", isBlind = ?", isBlind);

        return sql.insert();
    }

    public void modify(long id, String title, String body, boolean isBlind) {
        SecSql sql = myMap.genSecSql();

        sql
                .append("UPDATE article")
                .append("SET title = ?, body = ?, modifiedDate = NOW(), isBlind = ?", title, body, isBlind)
                .append("WHERE id = ?", id);

        // 수정된 row 개수
        sql.update();
    }

    public void delete(long id) {
        SecSql sql = myMap.genSecSql();

        // id가 0, 1, 3인 글 삭제
        // id가 0인 글은 없으니, 실제로는 2개의 글이 삭제됨
        sql
                .append("DELETE")
                .append("FROM article")
                .append("WHERE id = ?", id);

        sql.delete();
    }

    public ArticleDto js_getPreArticle(int id) {
        SecSql sql = myMap.genSecSql();
        sql
                .append("SELECT * FROM article WHERE id = ?", id - 1);

        ArticleDto articleDto = sql.selectRow(ArticleDto.class);

        if (articleDto == null) {
            return null;
        }

        return articleDto;
    }

    public ArticleDto getPrevArticle(long id) {
        SecSql sql = myMap.genSecSql();
        sql
                .append("SELECT *")
                .append("FROM article")
                .append("WHERE id < ?", id)
                .append("ORDER BY id DESC")
                .append("LIMIT 1");
        return sql.selectRow(ArticleDto.class);
    }

    public ArticleDto js_getNextArticle(int id) {
        SecSql sql = myMap.genSecSql();
        sql
                .append("SELECT * FROM article WHERE id = ?", id + 1);

        ArticleDto articleDto = sql.selectRow(ArticleDto.class);

        if (articleDto == null) {
            return null;
        }

        return articleDto;
    }

    public ArticleDto getNextArticle(long id) {
        SecSql sql = myMap.genSecSql();
        sql
                .append("SELECT *")
                .append("FROM article")
                .append("WHERE id > ?", id)
                .append("AND isBlind = 0")
                .append("ORDER BY id ASC")
                .append("LIMIT 1");

        return sql.selectRow(ArticleDto.class);
    }

    public void blind(int id1, int id2) {
        SecSql sql = myMap.genSecSql();

        // id가 0, 1, 2, 3인 글 수정
        // id가 0인 글은 없으니, 실제로는 3개의 글이 삭제됨
        sql
                .append("UPDATE article")
                .append("SET isBlind = ?", true)
                .append("WHERE id BETWEEN ? AND ?", id1, id2);

        sql.update();
    }
}
