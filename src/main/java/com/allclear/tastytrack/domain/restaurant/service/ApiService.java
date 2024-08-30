package com.allclear.tastytrack.domain.restaurant.service;

import org.springframework.stereotype.Service;

@Service
public interface ApiService {

    void fetchRawRestaurants(String startIndex, String endIndex) throws Exception;

}
