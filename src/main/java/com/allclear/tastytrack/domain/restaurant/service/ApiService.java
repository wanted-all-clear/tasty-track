package com.allclear.tastytrack.domain.restaurant.service;

import org.springframework.stereotype.Service;

@Service
public interface ApiService {

    void getRawRestaurants(String startIndex, String endIndex);

}
