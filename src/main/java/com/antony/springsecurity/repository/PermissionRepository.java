package com.antony.springsecurity.repository;

import com.antony.springsecurity.bean.Permission;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface PermissionRepository extends CrudRepository<Permission, Long> {
    /***
     SELECT p.* FROM tb_user AS u
     LEFT JOIN tb_user_role AS ur ON u.id = ur.user_id
     LEFT JOIN tb_role AS r ON r.id = ur.role_id
     LEFT JOIN tb_role_permission AS rp ON r.id = rp.role_id
     LEFT JOIN tb_permission AS p ON p.id = rp.permission_id
     WHERE u.id = 1
     */
    @Query(nativeQuery = true, value = "SELECT p.* FROM tb_user AS u \n" +
            "\tLEFT JOIN tb_user_role AS ur ON u.id = ur.user_id\n" +
            "\tLEFT JOIN tb_role AS r ON r.id = ur.role_id\n" +
            "\tLEFT JOIN tb_role_permission AS rp ON r.id = rp.role_id\n" +
            "\tLEFT JOIN tb_permission AS p ON p.id = rp.permission_id\n" +
            "WHERE u.id = :userId")
    List<Permission> selectByUserId(Long userId);
}
