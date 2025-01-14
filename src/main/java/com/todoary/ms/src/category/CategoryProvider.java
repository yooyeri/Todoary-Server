package com.todoary.ms.src.category;

import com.todoary.ms.src.category.dto.GetCategoryRes;
import com.todoary.ms.src.category.model.Category;
import com.todoary.ms.src.user.UserProvider;
import com.todoary.ms.util.BaseException;
import com.todoary.ms.util.BaseResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CategoryProvider {
    public final CategoryDao categoryDao;
    private final UserProvider userProvider;

    @Autowired
    public CategoryProvider(CategoryDao categoryDao, UserProvider userProvider) {
        this.categoryDao = categoryDao;
        this.userProvider = userProvider;
    }

    public void assertUsersCategoryValidById(Long userId, Long categoryId) throws BaseException {
        if (!checkUsersCategoryById(userId, categoryId))
            throw new BaseException(BaseResponseStatus.USERS_CATEGORY_NOT_EXISTS);
    }

    public boolean checkUsersCategoryById(Long user_id, Long categoryId) throws BaseException {
        try {
            return (categoryDao.selectExistsUsersCategoryById(user_id, categoryId) == 1);
        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public boolean checkCategoryDuplicate(Long user_id, String title) throws BaseException {
        try {
            return (categoryDao.selectExistsCategoryTitle(user_id, title) == 1);
        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public boolean checkCategoryEdit(Long user_id,Long categoryId, String title) throws BaseException {
        try {
            return (categoryDao.selectExistsCategoryEdit(user_id, categoryId, title) == 1);
        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public List<GetCategoryRes> retrieveById(Long user_id) throws BaseException {
        if (userProvider.checkId(user_id) == 0) throw new BaseException(BaseResponseStatus.USERS_EMPTY_USER_ID);
        try {
            return categoryDao.selectById(user_id);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

}
