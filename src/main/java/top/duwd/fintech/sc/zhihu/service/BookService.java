package top.duwd.fintech.sc.zhihu.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;
import top.duwd.fintech.common.domain.BookEntity;
import top.duwd.fintech.common.mapper.BookMapper;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class BookService {

    @Autowired
    private BookMapper bookMapper;

    public int save(BookEntity entity) {
        Date date = new Date();
        entity.setCreateTime(date);
        entity.setUpdateTime(date);

        return bookMapper.insert(entity);
    }

    /**
     * 根据书名查询
     *
     * @param bookName
     * @return
     */
    private List<BookEntity> findListByNameAndBookAuthor(String bookName, String bookAuthor) {
        Example example = new Example(BookEntity.class);
        if (StringUtils.isEmpty(bookName) && StringUtils.isEmpty(bookAuthor)) {
            return null;
        } else {
            if (StringUtils.isEmpty(bookName) && !StringUtils.isEmpty(bookName)) {
                example.createCriteria().andEqualTo("bookAuthor", bookAuthor);
            } else if (!StringUtils.isEmpty(bookName) && StringUtils.isEmpty(bookName)) {
                example.createCriteria().andEqualTo("bookName", bookName);
            } else {
                example.createCriteria()
                        .andEqualTo("bookName", bookName)
                        .andEqualTo("bookAuthor", bookAuthor);
            }
            List<BookEntity> list = bookMapper.selectByExample(example);
            return list;
        }
    }

    private List<BookEntity> findListByNameAndBookAuthorLike(String bookName, String bookAuthor) {
        Example example = new Example(BookEntity.class);
        if (StringUtils.isEmpty(bookName) && StringUtils.isEmpty(bookAuthor)) {
            return null;
        } else {
            if (StringUtils.isEmpty(bookName) && !StringUtils.isEmpty(bookName)) {
                example.createCriteria().andLike("bookAuthor", "%" + bookAuthor + "%");
            } else if (!StringUtils.isEmpty(bookName) && StringUtils.isEmpty(bookName)) {
                example.createCriteria().andLike("bookName", "%" + bookName + "%");
            } else {
                example.createCriteria()
                        .andLike("bookName", "%" + bookName + "%")
                        .andLike("bookAuthor", "%" + bookAuthor + "%");
            }
            List<BookEntity> list = bookMapper.selectByExample(example);
            return list;
        }
    }

    /**
     * 根据书名 + 作者 查询
     *
     * @param bookName
     * @return
     */
    public List<BookEntity> findListByNameAndBookAuthorLike(String bookName, String bookAuthor, Boolean like) {
        if (like) {
            return findListByNameAndBookAuthorLike(bookName, bookAuthor);
        } else {
            return findListByNameAndBookAuthor(bookName, bookAuthor);
        }
    }


}
