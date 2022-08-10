package exam;

import com.ll.exam.Container;
import com.ll.exam.article.dto.ArticleDto;
import com.ll.exam.article.service.ArticleService;
import com.ll.exam.mymap.MyMap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ArticleServiceTest {
    // @BeforeAll를 붙인 아래 메서드는
    // 한 주기당 최초 한번 실행
    private MyMap myMap;
    private ArticleService articleService;
    private static final int TEST_DATA_SIZE = 30;

    ArticleServiceTest() {
        myMap = Container.getObj(MyMap.class);
        articleService = Container.getObj(ArticleService.class);
    }

    @BeforeAll
    public void BeforeAll() {
        // 모든 DB처리 시 처리되는 SQL 출력
        myMap.setDevMode(true);
    }

    // @BeforeEach를 붙인 아래 메서드는
    // @Test가 달려있는 메서드가 실행되기 전에 자동으로 실행이 된다.
    // 주로 테스트 환경을 깔끔하게 정리하는 역할을 한다.
    // 즉 각각의 테스트케이스가 독립적인 환경에서 실행될 수 있도록 하는 역할을 한다.
    @BeforeEach
    public void beforeEach() {
        // 게시물 테이블을 깔끔하게 삭제한다.
        // DELETE FROM article; // 보다 TRUNCATE article; 로 삭제하는게 더 깔끔하고 흔적이 남지 않는다.
        truncateArticleTable();
        // 게시물 3개를 만든다.
        // 테스트에 필요한 샘플데이터를 만든다고 보면 된다.
        makeArticleTestData();
    }

    private void makeArticleTestData() {
        IntStream.rangeClosed(1, TEST_DATA_SIZE).forEach(no -> {
            boolean isBlind = false;
            String title = "제목%d".formatted(no);
            String body = "내용%d".formatted(no);

            myMap.run("""
                    INSERT INTO article
                    SET createdDate = NOW(),
                    modifiedDate = NOW(),
                    title = ?,
                    `body` = ?,
                    isBlind = ?
                    """, title, body, isBlind);
        });
    }

    private void truncateArticleTable() {
        // 테이블을 깔끔하게 지워준다.
        myMap.run("TRUNCATE article");
    }

    @Test
    public void 존재한다() {
        assertThat(articleService).isNotNull();
    }

    @Test
    public void getArticles() {
        List<ArticleDto> articleDtoList = articleService.getArticles();
        assertThat(articleDtoList.size()).isEqualTo(TEST_DATA_SIZE);
    }

    @Test
    public void getArticleById() {
        ArticleDto articleDto = articleService.getArticleById(1);

        assertThat(articleDto.getId()).isEqualTo(1L);
        assertThat(articleDto.getTitle()).isEqualTo("제목1");
        assertThat(articleDto.getBody()).isEqualTo("내용1");
        assertThat(articleDto.getCreatedDate()).isNotNull();
        assertThat(articleDto.getModifiedDate()).isNotNull();
        assertThat(articleDto.isBlind()).isFalse();
    }

    @Test
    public void getArticlesCount() {
        long articlesCount = articleService.getArticlesCount();

        assertThat(articlesCount).isEqualTo(TEST_DATA_SIZE);
    }

    @Test
    public void write() {
        long newArticleId = articleService.write("제목 new", "내용 new", false);

        ArticleDto articleDto = articleService.getArticleById(newArticleId);

        assertThat(articleDto.getId()).isEqualTo(newArticleId);
        assertThat(articleDto.getTitle()).isEqualTo("제목 new");
        assertThat(articleDto.getBody()).isEqualTo("내용 new");
        assertThat(articleDto.getCreatedDate()).isNotNull();
        assertThat(articleDto.getModifiedDate()).isNotNull();
        assertThat(articleDto.isBlind()).isEqualTo(false);
    }

    @Test
    public void modify() {
        articleService.modify(1, "제목 new", "내용 new", true);

        ArticleDto articleDto = articleService.getArticleById(1);

        assertThat(articleDto.getId()).isEqualTo(1);
        assertThat(articleDto.getTitle()).isEqualTo("제목 new");
        assertThat(articleDto.getBody()).isEqualTo("내용 new");
        assertThat(articleDto.isBlind()).isEqualTo(true);

        // DB에서 받아온 게시물 수정날짜와 자바에서 계산한 현재 날짜 비교(s 단위)
        // 1초 이하 차이난다면 수정날짜 갱신이라 봄
        long diff = ChronoUnit.SECONDS.between(articleDto.getModifiedDate(), LocalDateTime.now());

        assertThat(diff).isLessThanOrEqualTo(1L);
    }

    @Test
    public void delete() {
        articleService.delete(1);

        long articlesCount = articleService.getArticlesCount();

        assertThat(articlesCount).isEqualTo(TEST_DATA_SIZE - 1);
    }

    @Test
    public void 이전글_가져오기2() {
        ArticleDto articleDto = articleService.js_getPreArticle(5);

        assertThat(articleDto.getId()).isEqualTo(4);
        assertThat(articleDto.getTitle()).isEqualTo("제목4");
        assertThat(articleDto.getBody()).isEqualTo("내용4");
        assertThat(articleDto.getCreatedDate()).isNotNull();
        assertThat(articleDto.getModifiedDate()).isNotNull();
        assertThat(articleDto.isBlind()).isEqualTo(false);
    }

    @Test
    public void 이전글_가져오기_없는버전2() {
        ArticleDto articleDto = articleService.js_getPreArticle(1);

        assertThat(articleDto).isNull();
    }

    @Test
    public void _2번글의_이전글은_1번글_이다() {
        ArticleDto id2ArticleDto = articleService.getArticleById(2);
        ArticleDto id1ArticleDto = articleService.getPrevArticle(id2ArticleDto);

        assertThat(id1ArticleDto.getId()).isEqualTo(1);
    }

    @Test
    public void _1번글의_이전글은_없다() {
        ArticleDto id1ArticleDto = articleService.getArticleById(1);
        ArticleDto nullArticleDto = articleService.getPrevArticle(id1ArticleDto);

        assertThat(nullArticleDto).isNull();
    }

    @Test
    public void 다음글_가져오기2() {
        ArticleDto articleDto = articleService.js_getNextArticle(5);

        assertThat(articleDto.getId()).isEqualTo(6);
        assertThat(articleDto.getTitle()).isEqualTo("제목6");
        assertThat(articleDto.getBody()).isEqualTo("내용6");
        assertThat(articleDto.getCreatedDate()).isNotNull();
        assertThat(articleDto.getModifiedDate()).isNotNull();
        assertThat(articleDto.isBlind()).isEqualTo(false);
    }

    @Test
    public void _2번글의_다음글은_3번글_이다() {
        ArticleDto id3ArticleDto = articleService.getNextArticle(2);

        assertThat(id3ArticleDto.getId()).isEqualTo(3);
    }

    @Test
    public void 마지막글의_다음글은_없다() {
        long lastArticleId = TEST_DATA_SIZE;
        ArticleDto nullArticleDto = articleService.getNextArticle(lastArticleId);

        assertThat(nullArticleDto).isNull();
    }

    @Test
    public void 블라인드_처리() {
        articleService.blind(11, 20);

        ArticleDto nextArticleDto = articleService.getNextArticle(10);


        assertThat(nextArticleDto.getId()).isEqualTo(21);
    }
}