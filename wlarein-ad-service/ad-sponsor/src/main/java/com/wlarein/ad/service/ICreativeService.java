package com.wlarein.ad.service;

import com.wlarein.ad.vo.CreativeRequest;
import com.wlarein.ad.vo.CreativeResponse;

public interface ICreativeService {

    CreativeResponse createCreative(CreativeRequest request);
}