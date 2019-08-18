package com.wlarein.ad.service.impl;

import com.wlarein.ad.dao.CreativeRepository;
import com.wlarein.ad.entity.Creative;
import com.wlarein.ad.service.ICreativeService;
import com.wlarein.ad.vo.CreativeRequest;
import com.wlarein.ad.vo.CreativeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreativeServiceImpl implements ICreativeService {

    private final CreativeRepository creativeRepository;

    @Autowired
    public CreativeServiceImpl(CreativeRepository creativeRepository) {
        this.creativeRepository = creativeRepository;
    }

    @Override
    public CreativeResponse createCreative(CreativeRequest request) {

        Creative creative = creativeRepository.save(
                request.convertToEntity()
        );

        return new CreativeResponse(creative.getId(), creative.getName());
    }
}