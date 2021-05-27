package nii.ps.interfaceNSI.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)  // Включает разделение по ролям
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .formLogin()
                    .loginPage("/login")
                    .defaultSuccessUrl("/admin", true)    // здесь вводим логин
                    .successHandler(new LoginHandler())
                .and()

                .logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/login")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .permitAll()
                .and()

                .exceptionHandling()
                    .accessDeniedPage("/error/404")
                .and()

                .authorizeRequests()
                    .mvcMatchers("/css/**", "/img/**", "/js/**", "/encode/*", "/login").permitAll()
                    .anyRequest().authenticated()
                .and()
                .csrf().disable()
                ;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //auth.inMemoryAuthentication()
        //        .passwordEncoder(new BCryptPasswordEncoder())
        //        .withUser("admin").password("$2a$10$CNX4EjAQXwPAo6gCWRPE9eZcax7k5hHthyIk1SmDRN3bpB71n7Bou").authorities("ADMIN")
        //        .and()
        //        .withUser("user").password("$2a$10$I0LyKl3Zo9CMXGGfdyee6uHcAuhnlj/LI4hVREZT2KiXV80fOuECS").authorities("USER")
        //        .and()
        //        .withUser("user1").password("$2a$10$R63GO4OUEraszy2wY9WkOeCJ2OJ9V9iPWhS2kT49toZTczTHdpMTO").authorities("USER")
        //;
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(passwordEncoder)
                .usersByUsernameQuery("select username,password,enabled from login_users where username = ?")
                .authoritiesByUsernameQuery("select username,authority from login_users where username = ?")
        ;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
