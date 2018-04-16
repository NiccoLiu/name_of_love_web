package com.love.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.love.model.User;

/**
 * DAO类
 * 
 * @author generator
 */
public interface UserDAO extends BaseMapper<User> {

    /**
     * 分页查询
     * 
     * @param page page info
     * @param user search criteria
     * @param orderByField order's field
     * @param isAsc true is asc ,or not
     * @return list
     */
    List<User> getUserPage(@Param("page") Page<User> page, @Param("user") User user,
            @Param("orderByField") String orderByField, @Param("isAsc") boolean isAsc);

    int updateStep(@Param("unionid") String unionid, @Param("step") long step);

    int updateZero();
}
