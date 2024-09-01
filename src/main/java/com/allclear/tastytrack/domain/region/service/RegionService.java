package com.allclear.tastytrack.domain.region.service;

import com.allclear.tastytrack.domain.region.dto.RegionGroupResponse;
import com.allclear.tastytrack.domain.region.dto.RegionResponse;

import java.util.List;

public interface RegionService {

    List<RegionResponse> getRegionList();

}
