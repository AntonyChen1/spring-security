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
(0,'System','System','/',NULL,NOW(),NOW()),
(1,'SystemUser','SystemUser','/users/',NULL,NOW(),NOW()),
(2,'SystemUserView','SystemUserView','',NULL,NOW(),NOW()),
(2,'SystemUserInsert','SystemUserInsert','',NULL,NOW(),NOW()),
(2,'SystemUserUpdate','SystemUserUpdate','',NULL,NOW(),NOW()),
(2,'SystemUserDelete','SystemUserDelete','',NULL,NOW(),NOW()),
(1,'SystemContent','SystemContent','/contents/',NULL,NOW(),NOW()),
(3,'SystemContentView','SystemContentView','/contents/view/**',NULL,NOW(),NOW()),
(3,'SystemContentInsert','SystemContentInsert','/contents/insert/**',NULL,NOW(),NOW()),
(3,'SystemContentUpdate','SystemContentUpdate','/contents/update/**',NULL,NOW(),NOW()),
(3,'SystemContentDelete','SystemContentDelete','/contents/delete/**',NULL,NOW(),NOW());
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
### 6. Default value when login by form
```java
public class UsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
	public static final String SPRING_SECURITY_FORM_USERNAME_KEY = "username";
	public static final String SPRING_SECURITY_FORM_PASSWORD_KEY = "password";
    private boolean postOnly = true;
```

### 7. Tomcat session timeout, minimum 60s, default 30m
```java
private long getSessionTimeoutInMinutes() {
    Duration sessionTimeout = getSession().getTimeout();
    if (isZeroOrLess(sessionTimeout)) {
        return 0;
    }
    return Math.max(sessionTimeout.toMinutes(), 1);
}
```
```java
package org.springframework.boot.web.servlet.server;

public class Session {
	@DurationUnit(ChronoUnit.SECONDS)
	private Duration timeout = Duration.ofMinutes(30);
```
### 8. When login users exceed the maximum, throw excdption or expire the previous
```java
if (this.exceptionIfMaximumExceeded || (sessions == null)) {
    throw new SessionAuthenticationException(
            this.messages.getMessage("ConcurrentSessionControlAuthenticationStrategy.exceededAllowed",
                    new Object[] { allowableSessions }, "Maximum sessions of {0} for this principal exceeded"));
}
```
```java
List<SessionInformation> sessionsToBeExpired = sessions.subList(0, maximumSessionsExceededBy);
for (SessionInformation session : sessionsToBeExpired) {
    session.expireNow();
}
```
### 9. Remember me
`org.springframework.jdbc.BadSqlGrammarException: PreparedStatementCallback; bad SQL grammar [insert into persistent_logins (username, series, token, last_used) values(?,?,?,?)]`
```java
RememberMeAuthenticationFilter
    Authentication rememberMeAuth = this.rememberMeServices.autoLogin(request, response);
```
```sql
create table persistent_logins (
username varchar(64) not null,
series varchar(64) primary key,
token varchar(64) not null,
last_used timestamp not null
)
```
### 10. Logout
- default url
```java
public final class LogoutConfigurer<H extends HttpSecurityBuilder<H>>
		extends AbstractHttpConfigurer<LogoutConfigurer<H>, H> {
	private List<LogoutHandler> logoutHandlers = new ArrayList<>();
	private SecurityContextLogoutHandler contextLogoutHandler = new SecurityContextLogoutHandler();
	private String logoutSuccessUrl = "/login?logout";
	private LogoutSuccessHandler logoutSuccessHandler;
	private String logoutUrl = "/logout";
```
- customer url
```java
http.logout(logout -> logout
  .logoutUrl("/mylogout")
  .logoutSuccessUrl("/logout.html")
```
- logout handler
```java
public class SecurityContextLogoutHandler implements LogoutHandler {
  public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    session.invalidate();
    context.setAuthentication(null);
```
- Customize what to do when logout successfully
```java
public interface LogoutSuccessHandler {
	void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException;
}
```
### 11. Enable csrf (cross site request forgery)
- Login should add hidden input _csrf.token
- Logout can't use get method

### Hot keys
- Ctrl + Shift + n: search file
- Ctrl + Alt + b: show all implementation of the interface
- Ctrl + Alt + Left: back to last cursor position
- Ctrl + Alt + Shift + u or Ctrl + Alt + u: show class infrastructure



