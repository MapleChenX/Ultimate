package personal.MapleChenX.lsp.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class FlowLimitUtils {
    @Resource
    StringRedisTemplate redis;

    public boolean isLimit(String countKey, String blockKey, int freq, int countPeriod, int blockTime){
        Long freqTrue = Optional.ofNullable(redis.opsForValue().increment(countKey)).orElse(0L);
        if(freqTrue > freq){
            redis.opsForValue().set(blockKey, "请求过快", blockTime, TimeUnit.SECONDS);
            return false; // 请求被限制
        }else{
            if (freqTrue == 1) { // 如果是第一次计数，设置过期时间
                redis.expire(countKey, countPeriod, TimeUnit.SECONDS);
            }
            return true; // 请求没有被限制
        }
    }


}
