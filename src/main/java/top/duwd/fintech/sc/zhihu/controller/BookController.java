package top.duwd.fintech.sc.zhihu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.duwd.fintech.sc.zhihu.service.BookService;

@RequestMapping("/book")
@RestController
public class BookController {

    @Autowired
    private BookService bookService;


}
