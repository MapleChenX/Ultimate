package personal.MapleChenX.lsp.interceptor;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import personal.MapleChenX.lsp.common.Const;

import java.io.IOException;


/**
 * 跨域配置过滤器，仅处理跨域，添加跨域响应头
 */
@Component
@Order(Const.ORDER_CORS) //两种过滤器方案哦，还有一种在SpringSecurityConfig里面可以看见
public class CorFilter extends HttpFilter {

    @Value("${const.default.cors.origin}")
    String origin;

    @Value("${const.default.cors.credentials}")
    boolean credentials;

    @Value("${const.default.cors.methods}")
    String methods;

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        this.addCorsHeader(request, response);
        chain.doFilter(request, response);
    }

    /**
     * 添加所有跨域相关响应头
     * @param request 请求
     * @param response 响应
     */
    private void addCorsHeader(HttpServletRequest request, HttpServletResponse response) {
        response.addHeader("Access-Control-Allow-Origin", this.resolveOrigin(request)); //告诉浏览器该相应可以被哪些站点访问，浏览器一看就放手了
        response.addHeader("Access-Control-Allow-Methods", this.resolveMethod()); //允许的方法
        response.addHeader("Access-Control-Allow-Headers", "Authorization, Content-Type"); //服务器允许 Authorization 和 Content-Type 这两个 HTTP 头在实际请求中被携带
        if(credentials) {
            response.addHeader("Access-Control-Allow-Credentials", "true"); //是否允许携带cookie
        }
    }

    /**
     * 解析配置文件中的请求方法
     * @return 解析得到的请求头值
     */
    private String resolveMethod(){
        return methods.equals("*") ? "GET, HEAD, POST, PUT, DELETE, OPTIONS, TRACE, PATCH" : methods;
    }

    /**
     * 解析配置文件中的请求原始站点
     * @param request 请求
     * @return 解析得到的请求头值
     */
    private String resolveOrigin(HttpServletRequest request){
        return origin.equals("*") ? request.getHeader("Origin") : origin;
    }
}
