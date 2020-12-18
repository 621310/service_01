package com.lyl.service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lyl.service.common.JsonResult;
import com.lyl.service.util.JwtUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * Spring security 自定义登陆成功返回json
 */
@Component("MyAuthenticationSuccessHandler")
public class MyAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws  ServletException, IOException {
        String currentUser = authentication.getName();
        logger.info("用户"+currentUser+"登录成功");
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        JsonResult<Map<String,String>> respBean = new JsonResult<>();
        respBean.setCode("200");
        respBean.setMsg("登陆成功");
        out.write(new ObjectMapper().writeValueAsString(respBean));
    }
}
