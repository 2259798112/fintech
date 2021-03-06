package top.duwd.fintech.common.domain.sub.entity;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

public class SubQuestionDetailEntity implements Serializable {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_sub_question_detail.id
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    @Id
    private Integer id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_sub_question_detail.question_id
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    private Integer questionId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_sub_question_detail.title
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    private String title;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_sub_question_detail.question_type
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    private String questionType;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_sub_question_detail.answer_count
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    private Integer answerCount;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_sub_question_detail.follower_count
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    private Integer followerCount;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_sub_question_detail.visit_count
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    private Integer visitCount;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_sub_question_detail.author_id
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    private String authorId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_sub_question_detail.author_name
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    private String authorName;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_sub_question_detail.voteup_count
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    private Integer voteupCount;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_sub_question_detail.comment_count
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    private Integer commentCount;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_sub_question_detail.collapsed_answer_count
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    private Integer collapsedAnswerCount;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_sub_question_detail.created
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    private Date created;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_sub_question_detail.updated_time
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    private Date updatedTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_sub_question_detail.create_time
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    private Date createTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_sub_question_detail.update_time
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    private Date updateTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table t_sub_question_detail
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_sub_question_detail.id
     *
     * @return the value of t_sub_question_detail.id
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_sub_question_detail.id
     *
     * @param id the value for t_sub_question_detail.id
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_sub_question_detail.question_id
     *
     * @return the value of t_sub_question_detail.question_id
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    public Integer getQuestionId() {
        return questionId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_sub_question_detail.question_id
     *
     * @param questionId the value for t_sub_question_detail.question_id
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_sub_question_detail.title
     *
     * @return the value of t_sub_question_detail.title
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    public String getTitle() {
        return title;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_sub_question_detail.title
     *
     * @param title the value for t_sub_question_detail.title
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_sub_question_detail.question_type
     *
     * @return the value of t_sub_question_detail.question_type
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    public String getQuestionType() {
        return questionType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_sub_question_detail.question_type
     *
     * @param questionType the value for t_sub_question_detail.question_type
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_sub_question_detail.answer_count
     *
     * @return the value of t_sub_question_detail.answer_count
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    public Integer getAnswerCount() {
        return answerCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_sub_question_detail.answer_count
     *
     * @param answerCount the value for t_sub_question_detail.answer_count
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    public void setAnswerCount(Integer answerCount) {
        this.answerCount = answerCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_sub_question_detail.follower_count
     *
     * @return the value of t_sub_question_detail.follower_count
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    public Integer getFollowerCount() {
        return followerCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_sub_question_detail.follower_count
     *
     * @param followerCount the value for t_sub_question_detail.follower_count
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    public void setFollowerCount(Integer followerCount) {
        this.followerCount = followerCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_sub_question_detail.visit_count
     *
     * @return the value of t_sub_question_detail.visit_count
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    public Integer getVisitCount() {
        return visitCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_sub_question_detail.visit_count
     *
     * @param visitCount the value for t_sub_question_detail.visit_count
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    public void setVisitCount(Integer visitCount) {
        this.visitCount = visitCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_sub_question_detail.author_id
     *
     * @return the value of t_sub_question_detail.author_id
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    public String getAuthorId() {
        return authorId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_sub_question_detail.author_id
     *
     * @param authorId the value for t_sub_question_detail.author_id
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_sub_question_detail.author_name
     *
     * @return the value of t_sub_question_detail.author_name
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    public String getAuthorName() {
        return authorName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_sub_question_detail.author_name
     *
     * @param authorName the value for t_sub_question_detail.author_name
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_sub_question_detail.voteup_count
     *
     * @return the value of t_sub_question_detail.voteup_count
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    public Integer getVoteupCount() {
        return voteupCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_sub_question_detail.voteup_count
     *
     * @param voteupCount the value for t_sub_question_detail.voteup_count
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    public void setVoteupCount(Integer voteupCount) {
        this.voteupCount = voteupCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_sub_question_detail.comment_count
     *
     * @return the value of t_sub_question_detail.comment_count
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    public Integer getCommentCount() {
        return commentCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_sub_question_detail.comment_count
     *
     * @param commentCount the value for t_sub_question_detail.comment_count
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_sub_question_detail.collapsed_answer_count
     *
     * @return the value of t_sub_question_detail.collapsed_answer_count
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    public Integer getCollapsedAnswerCount() {
        return collapsedAnswerCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_sub_question_detail.collapsed_answer_count
     *
     * @param collapsedAnswerCount the value for t_sub_question_detail.collapsed_answer_count
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    public void setCollapsedAnswerCount(Integer collapsedAnswerCount) {
        this.collapsedAnswerCount = collapsedAnswerCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_sub_question_detail.created
     *
     * @return the value of t_sub_question_detail.created
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    public Date getCreated() {
        return created;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_sub_question_detail.created
     *
     * @param created the value for t_sub_question_detail.created
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    public void setCreated(Date created) {
        this.created = created;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_sub_question_detail.updated_time
     *
     * @return the value of t_sub_question_detail.updated_time
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    public Date getUpdatedTime() {
        return updatedTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_sub_question_detail.updated_time
     *
     * @param updatedTime the value for t_sub_question_detail.updated_time
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_sub_question_detail.create_time
     *
     * @return the value of t_sub_question_detail.create_time
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_sub_question_detail.create_time
     *
     * @param createTime the value for t_sub_question_detail.create_time
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_sub_question_detail.update_time
     *
     * @return the value of t_sub_question_detail.update_time
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_sub_question_detail.update_time
     *
     * @param updateTime the value for t_sub_question_detail.update_time
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_sub_question_detail
     *
     * @mbggenerated Mon May 04 20:44:48 CST 2020
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", questionId=").append(questionId);
        sb.append(", title=").append(title);
        sb.append(", questionType=").append(questionType);
        sb.append(", answerCount=").append(answerCount);
        sb.append(", followerCount=").append(followerCount);
        sb.append(", visitCount=").append(visitCount);
        sb.append(", authorId=").append(authorId);
        sb.append(", authorName=").append(authorName);
        sb.append(", voteupCount=").append(voteupCount);
        sb.append(", commentCount=").append(commentCount);
        sb.append(", collapsedAnswerCount=").append(collapsedAnswerCount);
        sb.append(", created=").append(created);
        sb.append(", updatedTime=").append(updatedTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}