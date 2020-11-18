package com.lyl.service.config;

import com.lyl.service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author 20996
 * 自定义WebSecurityConfigurerAdapter,进而实现对springSecurity更多的自定义配置
 */
@Configuration
public class MyWebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserService userService;

    @Bean
    PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }

//    配置了四个用户admin,root,user,supAdmin,密码都是123456，用户角色为roles后面的
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                .withUser("admin").password("123456").roles("admin","user")
//                .and()
//                .withUser("root").password("123456").roles("api","user")
//                .and()
//                .withUser("user").password("123456").roles("user")
//                .and()
//                .withUser("supAdmin").password("123456").roles("db","admin");
//    }

    //基于数据库配置用户信息


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

    /**    用户/admin/**必须具备admin角色 ，
         *     访问/api/**要有api或者admin角色，
         *    访问/db/**必须具备admin和db角色
         *    anyRequest,authenticated 任何请求必须通过登录认证
         */

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/admin/**")
                .hasRole("admin")
                .antMatchers("/api/**")
                .access("hasAnyRole('api','admin')")
                .antMatchers("/db/**")
                .access("hasRole('admin') and hasRole('db')")
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginProcessingUrl("/login")
                .permitAll()
                .and()
                .csrf()
                .disable();
    }
}
