package com.todoary.ms.src.user;

import com.todoary.ms.src.user.dto.*;
import com.todoary.ms.src.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;

@Repository
public class UserDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public User insertUser(User user, boolean isTermsEnable) {
        String insertUserQuery = "insert into user (name,nickname,email,password,role,provider,provider_id, terms) values (?,?,?,?,?,?,?,?)";
        Object[] insertUserParams = new Object[]{user.getName(), user.getNickname(), user.getEmail(), user.getPassword(),
                user.getRole(), user.getProvider(), user.getProvider_id(), isTermsEnable};

        this.jdbcTemplate.update(insertUserQuery, insertUserParams);

        Long lastInsertId = this.jdbcTemplate.queryForObject("select last_insert_id()", Long.class);

        user.setId(lastInsertId);

        return user;
    }

    public User selectByEmail(String email, String provider) {
        String selectByEmailQuery = "select id, name, nickname,email, password,profile_img_url, introduce, role, provider, provider_id from user where email = ? and provider = ? and status = 1";
        Object[] selectByEmailParams = new Object[]{email, provider};
        try {
            return this.jdbcTemplate.queryForObject(selectByEmailQuery,
                    (rs, rowNum) -> new User(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("nickname"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("profile_img_url"),
                            rs.getString("introduce"),
                            rs.getString("role"),
                            rs.getString("provider"),
                            rs.getString("provider_id")),
                    selectByEmailParams);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public User selectById(Long user_id) {
        String selectByIdQuery = "select id, name,nickname,email,password,profile_img_url,introduce,role, provider, provider_id from user where id = ? and status = 1";
        return this.jdbcTemplate.queryForObject(selectByIdQuery,
                (rs, rowNum) -> new User(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("nickname"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("profile_img_url"),
                        rs.getString("introduce"),
                        rs.getString("role"),
                        rs.getString("provider"),
                        rs.getString("provider_id")),
                user_id);
    }

    public User selectByProviderId(String provider_id) {
        String selectByIdQuery = "select id, name,nickname,email,password,profile_img_url,introduce,role, provider, provider_id from user where provider_id = ? and status = 1";
        return this.jdbcTemplate.queryForObject(selectByIdQuery,
                (rs, rowNum) -> new User(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("nickname"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("profile_img_url"),
                        rs.getString("introduce"),
                        rs.getString("role"),
                        rs.getString("provider"),
                        rs.getString("provider_id")),
                provider_id);
    }


    public int checkEmail(String email, String provider) {
        String checkEmailQuery = "select exists(select email, provider from user where email = ? and provider = ?)";
        Object[] checkEmailParams = new Object[]{email, provider};
        return this.jdbcTemplate.queryForObject(checkEmailQuery, int.class, checkEmailParams);
    }

    public int checkNickname(String nickname) {
        String checkNameQuery = "select exists(select nickname from user where nickname = ?)";
        String checkNameParam = nickname;
        return this.jdbcTemplate.queryForObject(checkNameQuery, int.class, checkNameParam);
    }

    public int checkOtherUserNickname(Long user_id,String nickname) {
        String checkOtherUserNicknameQuery = "select exists(select nickname from user where nickname = ? and id != ?)";
        Object[] checkOtherUserNicknameParams = new Object[]{nickname, user_id};
        return this.jdbcTemplate.queryForObject(checkOtherUserNicknameQuery, int.class, checkOtherUserNicknameParams);
    }

    public int checkId(Long id) {
        String checkIdQuery = "select exists(select nickname from user where id = ? and status = 1)";
        Long checkIdParam = id;
        return this.jdbcTemplate.queryForObject(checkIdQuery, int.class, checkIdParam);
    }

    public int checkAppleUniqueNo(String provider_id) {
        String checkAppleUniqueNoQuery = "select exists(select email, provider from user where provider_id = ?and status = 1)";
        String checkAppleUniqueNoParams = provider_id;
        return this.jdbcTemplate.queryForObject(checkAppleUniqueNoQuery, int.class, checkAppleUniqueNoParams);
    }

    public String updateProfileImg(Long user_id, String profile_img_url) {
        String updateProfileImgQuery = "update user set profile_img_url = ? where id = ? and status = 1";
        Object[] updateProfileImgParams = new Object[]{profile_img_url, user_id};

        this.jdbcTemplate.update(updateProfileImgQuery, updateProfileImgParams);

        return profile_img_url;
    }

    public PatchUserRes updateProfile(Long user_id, PatchUserReq patchUserReq) {
        String updateProfileQuery = "update user set nickname = ? , introduce = ? where id = ? and status = 1";
        Object[] updateProfileParams = new Object[]{patchUserReq.getNickname(), patchUserReq.getIntroduce(), user_id};

        int result = this.jdbcTemplate.update(updateProfileQuery, updateProfileParams);
        if (result == 1)
            return new PatchUserRes(patchUserReq.getNickname(), patchUserReq.getIntroduce());
        else
            return null;
    }

    public void updateUserStatus(Long user_id) {
        String updateStatusQuery = "update user set status = 0 where id = ?";
        Long updateStatusParam = user_id;
        this.jdbcTemplate.update(updateStatusQuery, updateStatusParam);
    }

    public void updateAlarm(Long user_id, String alarm, boolean isChecked) {
        String alarmStatusQuery = "update user set "+ alarm + " = ? where id = ?";
        Object[] alarmStatusParams = new Object[]{isChecked, user_id};
        this.jdbcTemplate.update(alarmStatusQuery, alarmStatusParams);
    }


    public void termsStatus(Long user_id, String terms, boolean isChecked) {
        String termsStatusQuery = "update user set "+ terms + " = ? where id = ?";
        Object[] termsStatusParams = new Object[]{isChecked, user_id};
        this.jdbcTemplate.update(termsStatusQuery, termsStatusParams);
    }

    public void updatePassword(String email, String encodedPassword) {
        String updatePasswordQuery = "update user set password = ? where email = ? and provider = 'none'";
        Object[] updatePasswordParams = new Object[]{encodedPassword, email};
        this.jdbcTemplate.update(updatePasswordQuery, updatePasswordParams);
    }

    public void updateFcmToken(Long user_id, String fcm_token) {
        String updateFcmTokenQuery = "update user set fcm_token = ? where id = ?";
        Object[] updateFcmTokenParams = new Object[]{fcm_token, user_id};
        this.jdbcTemplate.update(updateFcmTokenQuery, updateFcmTokenParams);
    }

    public int checkRefreshToken(Long id) {
        String checkRefreshTokenQuery = "select exists(select user_id from token where user_id = ?)";
        Long checkRefreshTokenParam = id;
        return this.jdbcTemplate.queryForObject(checkRefreshTokenQuery, int.class, checkRefreshTokenParam);
    }

    public void deleteRefreshToken(Long user_id) {
        String deleteRefreshTokenQuery = "delete from token where user_id = ?";
        Long deleteRefreshTokenParam = user_id;
        this.jdbcTemplate.update(deleteRefreshTokenQuery, deleteRefreshTokenParam);
    }

    public void updateProfileImgToDefault(Long user_id) {
        String updateProfileImgToDefaultQuery = "update user set profile_img_url = 'https://todoarybucket.s3.ap-northeast-2.amazonaws.com/todoary/users/admin/default_profile_img.jpg' where id = ?";
        this.jdbcTemplate.update(updateProfileImgToDefaultQuery,user_id);
    }

    public GetAlarmEnabledRes selectAlarmEnabledById (Long user_id){
        String selectAlarmEnabledByIdQuery="select id, alarm_todo, alarm_diary, alarm_remind from user where id=? and status=1";
        return this. jdbcTemplate.queryForObject(selectAlarmEnabledByIdQuery,
                (rs, rowNum) -> new GetAlarmEnabledRes(
                        rs.getLong("id"),
                        rs.getBoolean("alarm_todo"),
                        rs.getBoolean("alarm_diary"),
                        rs.getBoolean("alarm_remind"))
                ,user_id);
    }

    public int isDeleted(String email, String provider) {
        String isDeletedQuery = "select exists(select id from user where email = ? and provider = ? and status = 0)";
        Object[] isDeletedParams = new Object[]{email, provider};
        return this.jdbcTemplate.queryForObject(isDeletedQuery, int.class, isDeletedParams);
    }

    public void deleteByUserStatus(String target_date) {
        String deleteByUserStatusQuery = "\n" +
                "delete from user where status = 0 and DATE_FORMAT(DATE_ADD(created_at, INTERVAL 30 DAY), '%Y-%m-%d') = ?";

        this.jdbcTemplate.update(deleteByUserStatusQuery,target_date);
    }

}
