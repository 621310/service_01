package com.lyl.service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lyl.service.common.JsonResult;
import com.lyl.service.entity.User;
import com.lyl.service.service.UserService;
import com.lyl.service.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 20996
 * 自定义WebSecurityConfigurerAdapter,进而实现对springSecurity更多的自定义配置
 */
@Configuration
public class MyWebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserService userService;

    private final MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;

    public MyWebSecurityConfig(MyAuthenticationSuccessHandler myAuthenticationSuccessHandler) {
        this.myAuthenticationSuccessHandler = myAuthenticationSuccessHandler;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
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

    @Override
    public void configure(WebSecurity web) throws Exception {
        // 全局配置：忽略url
        web.ignoring().antMatchers("/resource/testredis");
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
                .loginPage("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(new AuthenticationSuccessHandler() {
                    //自定义登陆成功返回
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                        response.setContentType("application/json;charset=utf-8");
                        PrintWriter pw = response.getWriter();
                        Map<String, Object> map = new HashMap<String, Object>();
                        Map<String,Object> result = new HashMap<>();
                        User userInfo = (User)authentication.getPrincipal();
                        result.put("result",userInfo);
                        result.put("token", JwtUtil.CreateToken(userInfo.getId()));
                        map.put("status", 200);
                        map.put("msg", result);
                        pw.write(new ObjectMapper().writeValueAsString(map));
                        pw.flush();
                        pw.close();
                    }
                })
                .failureHandler(new AuthenticationFailureHandler() {
                    //自定义登录失败返回json
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException, IOException {
                        response.setContentType("application/json;charset=utf-8");
                        PrintWriter pw = response.getWriter();
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("status", 401);
                        if (exception instanceof LockedException) {
                            map.put("msg", "账户被锁定，登陆失败！");
                        } else if (exception instanceof BadCredentialsException) {
                            map.put("msg", "账户或者密码错误，登陆失败！");
                        } else if (exception instanceof DisabledException) {
                            map.put("msg", "账户被禁用，登陆失败！");
                        } else if (exception instanceof AccountExpiredException) {
                            map.put("msg", "账户已过期，登陆失败！");
                        } else if (exception instanceof CredentialsExpiredException) {
                            map.put("msg", "密码已过期，登陆失败！");
                        } else {
                            map.put("msg", "登陆失败！");
                        }
                        pw.write(new ObjectMapper().writeValueAsString(map));
                        pw.flush();
                        pw.close();
                    }
                })
                .permitAll()
                .and()
                .csrf().disable().exceptionHandling().authenticationEntryPoint(
                        //自定义未登录返回Json
                        // Spring Security 中的一个接口 AuthenticationEntryPoint ，
                        // 该接口有一个实现类：LoginUrlAuthenticationEntryPoint ，该类中有一个方法 commence，如下
                        //直接重写这个方法，在方法中返回 JSON 即可，
                new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest req, HttpServletResponse resp, AuthenticationException e) throws IOException, ServletException {
                        resp.setContentType("application/json;charset=utf-8");
                        PrintWriter out = resp.getWriter();
                        JsonResult<Map<String,String>> respBean = new JsonResult<>();
                        respBean.setCode("401");
                        respBean.setMsg("请登录");
                        out.write(new ObjectMapper().writeValueAsString(respBean));
                        out.flush();
                        out.close();
                    }
                }
        );
    }
}
