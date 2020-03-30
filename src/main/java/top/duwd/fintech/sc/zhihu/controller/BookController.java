package top.duwd.fintech.sc.zhihu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import top.duwd.dutil.http.api.ApiResult;
import top.duwd.dutil.http.api.ApiResultManager;
import top.duwd.fintech.common.domain.BookEntity;
import top.duwd.fintech.sc.zhihu.service.BookService;

import java.util.List;

@RequestMapping("/book")
@RestController
public class BookController {

    @Autowired
    private BookService bookService;
    @Autowired
    private ApiResultManager apm;

    @GetMapping("/search")
    public ApiResult search(String bookName, String bookAuthor, Boolean like) {
        if (like == null) like = false;

        List<BookEntity> list = bookService.findListByNameAndBookAuthorLike(bookName, bookAuthor, like);
        return apm.success(list);
    }


    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResult save(@RequestBody BookEntity book) {
        if (StringUtils.isEmpty(book.getBookAuthor()) || StringUtils.isEmpty(book.getBookName())) {
            return apm.failDefault();
        }
        int save = bookService.save(book);
        return apm.success(save);
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResult update(@RequestBody BookEntity book) {
        if (book.getId() == null || book.getId() < 1) {
            return apm.failDefault();
        }
        int save = bookService.update(book);
        return apm.success(save);
    }


}
