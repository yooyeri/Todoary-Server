package com.todoary.ms.src.diary;



import com.todoary.ms.src.diary.dto.GetDiaryByDateRes;
import com.todoary.ms.src.diary.dto.PostDiaryReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

import java.util.List;


@Repository
public class DiaryDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DiaryDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public void insertOrUpdateDiary(long userId, PostDiaryReq postDiaryReq, String createdDate) {
        String insertDiaryQuery = "INSERT INTO diary (user_id, title, content, created_date) VALUES(?, ?, ?, ?) ON DUPLICATE KEY UPDATE title=?, content=?";
        Object[] insertDiaryParams=new Object[]{userId, postDiaryReq.getTitle(), postDiaryReq.getContent(), createdDate, postDiaryReq.getTitle(), postDiaryReq.getContent()};
        this.jdbcTemplate.update(insertDiaryQuery, insertDiaryParams);
    }


    public int selectExistsUsersDiaryById(Long userId, String createdDate) {
        String selectExistsUsersDiaryByIdQuery = "SELECT EXISTS(SELECT user_id, id FROM diary " +
                "WHERE user_id = ? and created_date = ?)";
        Object[] selectExistsUsersDiaryByIdParams = new Object[]{userId, createdDate};
        return this.jdbcTemplate.queryForObject(selectExistsUsersDiaryByIdQuery, int.class, selectExistsUsersDiaryByIdParams);
    }

    public void deleteDiary(Long userId, String created_date) {
        String deleteDiaryQuery = "DELETE FROM diary "+"WHERE user_id = ? and DATE(?)=DATE(created_date) ";
        Object[] deleteDiaryParam = new Object[]{userId, created_date};
        this.jdbcTemplate.update(deleteDiaryQuery, deleteDiaryParam);
    }



    public GetDiaryByDateRes selectDiaryByDate(Long userId, String created_date) {
        String selectDiaryByDateQuery = "SELECT id, title, content, created_date " +
                "FROM diary " +
                "WHERE user_id = ? and DATE(?)=DATE(created_date) " +
                "ORDER BY created_date ";
        Object[] selectDiaryByDateParams = new Object[]{userId, created_date};
        return this.jdbcTemplate.queryForObject(selectDiaryByDateQuery,
                (rs,rowNum) -> new GetDiaryByDateRes(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getString("created_date")
                ),selectDiaryByDateParams);

    }

    public List<Integer> selectIsDiaryInMonth(Long userId, String yearAndMonth) {
        String selectIsDiaryInMonthQuery = "SELECT DAY(created_date) as day " +
                "FROM diary WHERE user_id=? and ? = DATE_FORMAT(created_date, '%Y-%m') " +
                "GROUP by day ORDER BY day";
        Object[] selectIsDiaryInMonthParams = new Object[]{userId, yearAndMonth};
        return this.jdbcTemplate.query(selectIsDiaryInMonthQuery,
                (rs, rowNum) -> (rs.getInt("day")), selectIsDiaryInMonthParams);
    }



}
