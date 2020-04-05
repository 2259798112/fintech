package top.duwd.fintech.sc.zhihu.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.duwd.dutil.http.api.ApiResult;
import top.duwd.dutil.http.api.ApiResultManager;
import top.duwd.fintech.common.domain.zhihu.dto.BookDto;
import top.duwd.fintech.common.domain.zhihu.entity.ZhihuBookEntity;
import top.duwd.fintech.common.domain.zhihu.entity.ZhihuQuestionAnswerPageEntity;
import top.duwd.fintech.common.domain.zhihu.vo.ZhihuBookVo;
import top.duwd.fintech.sc.zhihu.service.ZhihuAnswerService;
import top.duwd.fintech.sc.zhihu.service.ZhihuBookService;
import top.duwd.fintech.sc.zhihu.service.ZhihuQuestionService;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/zhihu")
public class ZhihuController {
    @Autowired
    private ApiResultManager apm;
    @Autowired
    private ZhihuQuestionService zhihuQuestionService;
    @Autowired
    private ZhihuAnswerService zhihuAnswerService;
    @Autowired
    private ZhihuBookService zhihuBookService;

    @GetMapping(value = "/question/add")
    public ApiResult question(@RequestParam Integer id) {
        log.info("/question/add");
        int count = zhihuQuestionService.parse(id);
        return apm.success(count);
    }

    /**
     * 网页 问题主页 添加问题
     *
     * @param json
     * @return
     */
    @PostMapping(value = "/question/add/page", consumes = "application/json", produces = "application/json")
    public ApiResult questionAddPage(@RequestBody String json) {
        log.info("/question/add/page");
        int count = 0;
        count = zhihuQuestionService.parseQuestion(count, JSONObject.parseObject(json));
        log.info(json);
        return apm.success(count);
    }

    /**
     * 网页 问题主页 添加答案
     *
     * @param answer
     * @return
     */
    @PostMapping(value = "/answer/add/page", consumes = "application/json", produces = "application/json")
    public ApiResult answer(@RequestBody ZhihuQuestionAnswerPageEntity answer) {
        log.info("/answer/add/page");
        int save = zhihuAnswerService.save(answer);
        return apm.success(save);
    }

    @GetMapping(value = "/answer/book")
    public ApiResult answerBook(@RequestParam(value = "qid") Integer qid, int limit, int start, int end, int sort, int merge) {
        log.info("question id={}", qid);
        List<BookDto> sourceList = zhihuAnswerService.findBook(qid, limit);

        List<BookDto> result = merge(start, end, sort, merge, sourceList);
        return apm.success(result);
    }

    @NotNull
    private List<BookDto> merge(int start, int end, int sort, int merge, List<BookDto> sourceList) {
        if (merge == 1) {

            Map<Integer, BookDto> linkMap = new HashMap<>();
            //是否合并
            //查找是否有关联id

            ArrayList<BookDto> list = new ArrayList<>();
            for (BookDto bookDto : sourceList) {
                Integer link = bookDto.getLinkBookId();
                if (link != null && link > 0) {
                    BookDto answer = linkMap.get(link);
                    if (answer == null) {
                        linkMap.put(link, bookDto);
                        list.add(bookDto);
                    } else {
                        answer.setBookName(answer.getBookName() + "\n" + bookDto.getBookName());

                        answer.getAuthorIconUrl().addAll(bookDto.getAuthorIconUrl());
                        answer.setAuthorIconUrl(new ArrayList<>(new HashSet<>(answer.getAuthorIconUrl())));

                        answer.getAuthorAnswerUrl().addAll(bookDto.getAuthorAnswerUrl());
                        answer.setAuthorAnswerUrl(new ArrayList<>(new HashSet<>(answer.getAuthorAnswerUrl())));

                        answer.getAuthorAnotherIconUrl().addAll(bookDto.getAuthorAnotherIconUrl());
                        answer.setAuthorAnotherIconUrl(new ArrayList<>(new HashSet<>(answer.getAuthorAnotherIconUrl())));

                        answer.getAuthorAnswerAnotherUrl().addAll(bookDto.getAuthorAnotherIconUrl());
                        answer.setAuthorAnswerAnotherUrl(new ArrayList<>(new HashSet<>(answer.getAuthorAnswerAnotherUrl())));
                    }
                }else {
                    list.add(bookDto);
                }

            }

            start = start < 0 ? 0 : start;
            end = (end > list.size() - 1) ? list.size() - 1 : end;
            if (sort == 1) {
                list.sort((l1, l2) -> l2.getAuthorIconUrl().size() - l1.getAuthorIconUrl().size());
            }

            List<BookDto> bookDtos = list.subList(start, end);


            return bookDtos;
        } else {

            List<BookDto> list = sourceList;

            start = start < 0 ? 0 : start;
            end = (end > list.size() - 1) ? list.size() - 1 : end;
            if (sort == 1) {
                list.sort((l1, l2) -> l2.getAuthorIconUrl().size() - l1.getAuthorIconUrl().size());
            }

            List<BookDto> bookDtos = list.subList(start, end);
            return bookDtos;
        }
    }


    @GetMapping(value = "/answer/content")
    public ApiResult answerBook(Integer id) {
        String content = zhihuAnswerService.findContent(id);
        return apm.success(content);
    }

    private ZhihuBookEntity voToEntity(ZhihuBookVo book) {
        ZhihuBookEntity entity = new ZhihuBookEntity();
        BeanUtils.copyProperties(book, entity);
        entity.setAuthorIconList(JSON.toJSONString(book.getAuthorIconList()));
        entity.setAuthorNameList(JSON.toJSONString(book.getAuthorNameList()));
        entity.setAuthorAllIconList(JSON.toJSONString(book.getAuthorAllIconList()));
        entity.setAuthorAllNameList(JSON.toJSONString(book.getAuthorAllNameList()));
        return entity;
    }

    @PostMapping(value = "/book/add", consumes = "application/json", produces = "application/json")
    public ApiResult bookAdd(@RequestBody ZhihuBookVo book) {
        log.info("/book/add {}", JSON.toJSONString(book));
        int i = zhihuBookService.saveOrUpdate(voToEntity(book));
        return apm.success(i);
    }

    @PostMapping(value = "/book/update", consumes = "application/json", produces = "application/json")
    public ApiResult bookUpdate(@RequestBody ZhihuBookVo book) {
        log.info("/book/update {}", JSON.toJSONString(book));
        int i = zhihuBookService.saveOrUpdate(voToEntity(book));
        return apm.success(i);
    }

    @PostMapping(value = "/book/ignore", consumes = "application/json", produces = "application/json")
    public ApiResult bookIgnore(@RequestBody ZhihuBookVo book) {
        log.info("/book/ignore {}", JSON.toJSONString(book));
        int ignore = zhihuBookService.ignore(book.getId());
        return apm.success(ignore);
    }


    @GetMapping(value = "/book/bind")
    public ApiResult bookBind(@RequestParam String sourceId, @RequestParam Integer targetId) {
        log.info("/book/bind sourceId={},targetId={}", sourceId, targetId);
        int bind = zhihuBookService.bind(sourceId, targetId);
        return apm.success(bind);
    }

    @GetMapping(value = "/book/unbind")
    public ApiResult bookUnbind(@RequestParam String sourceId, @RequestParam Integer targetId) {
        log.info("/book/unbind sourceId={},targetId={}", sourceId, targetId);
        int unbind = zhihuBookService.unbind(sourceId);
        return apm.success(unbind);
    }
}
