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

### Hot keys
- Ctrl + Shift + n: search file
- Ctrl + Alt + b: show all implementation of the interface
- Ctrl + Alt + Left: back to last cursor position



