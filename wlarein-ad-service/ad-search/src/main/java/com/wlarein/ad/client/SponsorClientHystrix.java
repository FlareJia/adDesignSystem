package com.wlarein.ad.client;

import com.wlarein.ad.client.vo.AdPlan;
import com.wlarein.ad.client.vo.AdPlanGetRequest;
import com.wlarein.ad.vo.CommonResponse;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 实现一个断路器，当调用ad-sponsor中的服务出错时就会服务降级
 * 并返回一个错误CommonResponse
 */
@Component
public class SponsorClientHystrix implements SponsorClient {

    @Override
    public CommonResponse<List<AdPlan>> getAdPlans(
            AdPlanGetRequest request) {
        return new CommonResponse<>(-1,
                "eureka-client-ad-sponsor error");
    }
}
