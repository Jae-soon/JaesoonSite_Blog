package com.ll.exam.article.controller;

import com.ll.exam.Rq;
import com.ll.exam.annotation.*;
import com.ll.exam.article.dto.ArticleDto;
import com.ll.exam.article.service.ArticleService;

import java.util.List;

// ArticleController 가 컨트롤러 이다.
// 아래 ArticleController 클래스는 Controller 이다.
@Controller
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @GetMapping("/usr/article/list")
    public void showList(Rq rq) {
        List<ArticleDto> articleDtos = articleService.getArticles();

        rq.setAttr("articles", articleDtos);
        rq.view("usr/article/list");
    }

    @GetMapping("/usr/article/write")
    public void showWrite(Rq rq) {
        rq.view("usr/article/write");
    }

    @PostMapping("/usr/article/write")
    public void write(Rq rq) {
        String title = rq.getParam("title", "");
        String body = rq.getParam("body", "");

        if (title.length() == 0) {
            rq.historyBack("제목을 입력해주세요.");
            return;
        }

        if (body.length() == 0) {
            rq.historyBack("내용을 입력해주세요.");
            return;
        }

        long id = articleService.write(title, body);

        rq.replace("/usr/article/detail/%d".formatted(id), "%d번 게시물이 생성 되었습니다.".formatted(id));
    }

    @GetMapping("/usr/article/detail/{id}")
    public void showDetail(Rq rq) {
        long id = rq.getLongParam("id", 0);

        if (id == 0) {
            rq.historyBack("번호를 입력해주세요.");
            return;
        }

        ArticleDto articleDto = articleService.getArticleById(id);

        if (articleDto == null) {
            rq.historyBack("해당 글이 존재하지 않습니다.");
            return;
        }

        rq.setAttr("article", articleDto);
        rq.view("usr/article/detail");
    }

    @PutMapping("/usr/article/modify/{id}")
    public void modify(Rq rq) {
        long id = rq.getLongParam("id", 0);

        if (id == 0) {
            rq.historyBack("번호를 입력해주세요.");
            return;
        }

        ArticleDto articleDto = articleService.getArticleById(id);

        if (articleDto == null) {
            rq.historyBack("해당 글이 존재하지 않습니다.");
            return;
        }

        String title = rq.getParam("title", "");
        String body = rq.getParam("body", "");

        if (title.length() == 0) {
            rq.historyBack("제목을 입력해주세요.");
            return;
        }

        if (body.length() == 0) {
            rq.historyBack("내용을 입력해주세요.");
            return;
        }

        articleService.modify(id, title, body);

        rq.replace("/usr/article/detail/%d".formatted(id), "%d번 게시물이 수정 되었습니다.".formatted(id));
    }


    @GetMapping("/usr/article/modify/{id}")
    public void showModify(Rq rq) {
        long id = rq.getLongParam("id", 0);

        if (id == 0) {
            rq.historyBack("번호를 입력해주세요.");
            return;
        }

        ArticleDto articleDto = articleService.getArticleById(id);

        if (articleDto == null) {
            rq.historyBack("해당 글이 존재하지 않습니다.");
            return;
        }

        String articleBody = articleDto.getBody();
        // 토스트UI 에디터 특성상 script를 t-script 로 치환해야 함
        // 나중에 처리

        rq.setAttr("articleBody", articleBody);
        rq.setAttr("article", articleDto);
        rq.view("usr/article/modify");
    }

    @DeleteMapping("/usr/article/delete/{id}")
    public void delete(Rq rq) {
        long id = rq.getLongParam("id", 0);

        if (id == 0) {
            rq.historyBack("번호를 입력해주세요.");
            return;
        }

        ArticleDto articleDto = articleService.getArticleById(id);

        if (articleDto == null) {
            rq.historyBack("해당 글이 존재하지 않습니다.");
            return;
        }

        articleService.delete(id);

        rq.replace("/usr/article/list", "%d번 게시물이 삭제 되었습니다.".formatted(id));
    }
}