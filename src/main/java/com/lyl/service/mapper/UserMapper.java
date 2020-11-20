package com.lyl.service.mapper;


import com.lyl.service.entity.Role;
import com.lyl.service.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


import java.util.List;

/**
 * @author 20996
 */

@Mapper
public interface UserMapper {

    User loadUser(@Param("username") String username);

    List<Role> getUserRoleByUId(@Param("id")String id);

}
