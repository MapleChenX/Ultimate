package personal.MapleChenX.lsp.interceptor;


import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import personal.MapleChenX.lsp.common.Const;
import personal.MapleChenX.lsp.common.model.ret.ResponseTemplate;
import personal.MapleChenX.lsp.utils.FlowLimitUtils;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * 限流控制过滤器
 * 防止用户高频请求接口，借助Redis进行限流
 */
@Slf4j
@Component
@Order(Const.ORDER_OF_FLOW_LIMIT)
public class FlowLimitingFilter extends HttpFilter {

    @Resource
    StringRedisTemplate template;
    //指定时间内最大请求次数限制
    @Value("${const.default.flowLimit.freq}")
    int freq;
    //计数时间周期
    @Value("${const.default.flowLimit.countPeriod}")
    int countPeriod;
    //超出请求限制封禁时间
    @Value("${const.default.flowLimit.blockTime}")
    int blockTime;

    @Resource
    FlowLimitUtils flowLimitUtils;

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String address = request.getRemoteAddr();
        if (!doCount(address)) //如果请求被限制
            this.writeBlockMessage(response); //返回操作频繁
        else
            chain.doFilter(request, response);
    }

    /**
     * @return false就是被限制
     */
    private boolean doCount(String address) {
        String blockKey = Const.FLOW_LIMIT_BLOCK + address;
        synchronized (address.intern()) {
            if(Boolean.TRUE.equals(template.hasKey(blockKey))) //封禁
                return false;
            String countKey = Const.FLOW_LIMIT_COUNT + address; //计数
            return flowLimitUtils.isLimit(countKey, blockKey, freq, countPeriod, blockTime);
        }
    }

    private void writeBlockMessage(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.write(ResponseTemplate.forbidden("操作频繁，请稍后再试").toJsonString());
    }
}
