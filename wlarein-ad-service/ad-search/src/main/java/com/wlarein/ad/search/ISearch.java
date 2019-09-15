package com.wlarein.ad.search;

import com.wlarein.ad.search.vo.SearchRequest;
import com.wlarein.ad.search.vo.SearchResponse;

public interface ISearch {

    SearchResponse fetchAds(SearchRequest request);
}
