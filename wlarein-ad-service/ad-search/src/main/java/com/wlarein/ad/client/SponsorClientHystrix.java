package com.wlarein.ad.client;

import com.wlarein.ad.client.vo.AdPlan;
import com.wlarein.ad.client.vo.AdPlanGetRequest;
import com.wlarein.ad.vo.CommonResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SponsorClientHystrix implements SponsorClient{
    @Override
    public CommonResponse<List<AdPlan>> getAdPlans(AdPlanGetRequest request) {
        return new CommonResponse<>(-1, "eureka-client-ad-sponsor error");
    }
}
