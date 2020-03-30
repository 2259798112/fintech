package top.duwd.fintech.sc.zhihu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public ApiResult search(String bookName,String bookAuthor,Boolean like){
        if (like == null) like = false;

        List<BookEntity> list = bookService.findListByNameAndBookAuthorLike(bookName, bookAuthor, like);
        return apm.success(list);
    }

}
