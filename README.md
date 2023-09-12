### 1. DefaultLoginPageGeneratingFilter#generateLoginPageHtml
### 2. Config user and password based on application.yaml
class: UserDetailsServiceAutoConfiguration
```java
@ConfigurationProperties(prefix = "spring.security")
public class SecurityProperties {
```
```java
public static class User {
    private String name = "user";
    private String password = UUID.randomUUID().toString();
```
### 3. Config user and password based on UserDetailsService
```java
public interface UserDetailsService {
	UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
```
### 4. java.lang.IllegalArgumentException: There is no PasswordEncoder mapped for the id "null"
- The way of encoder is defined in the class `PasswordEncoderFactories`.
- When no passwordEncoder
  ```java
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new User("antony", "{noop}liferay", // No encoder
                AuthorityUtils.commaSeparatedStringToAuthorityList("admin, user"));
    }
  ```
- Add encoder to config `return new BCryptPasswordEncoder();`
### 5. Using database, RBAC（Role-Based Access Control）
- Create table
```sql
CREATE TABLE `tb_user` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`username` varchar(50) NOT NULL,
`password` varchar(64) NOT NULL,
`phone` varchar(20) DEFAULT NULL,
`email` varchar(50) DEFAULT NULL,
`created` datetime NOT NULL,
`updated` datetime NOT NULL,
PRIMARY KEY (`id`),
UNIQUE KEY `username` (`username`) USING BTREE,
UNIQUE KEY `phone` (`phone`) USING BTREE,
UNIQUE KEY `email` (`email`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

insert into `tb_user`(`username`,`password`,`phone`,`email`,`created`,`updated`) values
  ('antony','$2a$10$cGn4X.xPC/0KFh1aP.lWM.sa4kztOA19BehaidZZ2VqrFFx8rRvZK','18686868686','123456@gmail.com',NOW(),NOW());

```
```sql
CREATE TABLE `tb_role` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`parent_id` bigint(20) DEFAULT NULL,
`name` varchar(64) NOT NULL,
`enname` varchar(64) NOT NULL,
`description` varchar(200) DEFAULT NULL,
`created` datetime NOT NULL,
`updated` datetime NOT NULL,
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

insert into `tb_role`(`parent_id`,`name`,`enname`,`created`,`updated`)
values (0,'admin','admin',NOW(),NOW());
```
```sql
CREATE TABLE `tb_user_role` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`user_id` bigint(20) NOT NULL,
`role_id` bigint(20) NOT NULL,
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

insert into `tb_user_role`(`user_id`,`role_id`) values
  (1,1);
```
```sql
CREATE TABLE `tb_permission` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`parent_id` bigint(20) DEFAULT NULL,
`name` varchar(64) NOT NULL,
`enname` varchar(64) NOT NULL,
`url` varchar(255) NOT NULL,
`description` varchar(200) DEFAULT NULL,
`created` datetime NOT NULL,
`updated` datetime NOT NULL,
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

insert into `tb_permission`
(`parent_id`,`name`,`enname`,`url`,`description`,`created`,`updated`) values
(0,'系统管理','System','/',NULL,NOW(),NOW()),
(1,'用户管理','SystemUser','/users/',NULL,NOW(),NOW()),
(2,'查看用户','SystemUserView','',NULL,NOW(),NOW()),
(2,'新增用户','SystemUserInsert','',NULL,NOW(),NOW()),
(2,'编辑用户','SystemUserUpdate','',NULL,NOW(),NOW()),
(2,'删除用户','SystemUserDelete','',NULL,NOW(),NOW()),
(1,'内容管理','SystemContent','/contents/',NULL,NOW(),NOW()),
(3,'查看内容','SystemContentView','/contents/view/**',NULL,NOW(),NOW()),
(3,'新增内容','SystemContentInsert','/contents/insert/**',NULL,NOW(),NOW()),
(3,'编辑内容','SystemContentUpdate','/contents/update/**',NULL,NOW(),NOW()),
(3,'删除内容','SystemContentDelete','/contents/delete/**',NULL,NOW(),NOW());
```
```sql
CREATE TABLE `tb_role_permission` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`role_id` bigint(20) NOT NULL,
`permission_id` bigint(20) NOT NULL,
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8;

insert into `tb_role_permission`(`role_id`,`permission_id`) values
(1,1),
(1,2),
(1,3),
(1,4),
(1,5),
(1,6),
(1,7),
(1,8),
(1,9),
(1,10),
(1,11);
```

### Hot keys
- Ctrl + Shift + n: search file
- Ctrl + Alt + b: show all implementation of the interface
- Ctrl + Alt + Left: back to last cursor position
- Ctrl + Alt + Shift + u or Ctrl + Alt + u: show class infrastructure



