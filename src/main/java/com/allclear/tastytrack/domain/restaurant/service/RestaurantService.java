package com.allclear.tastytrack.domain.restaurant.service;

import com.allclear.tastytrack.domain.restaurant.dto.RestaurantSearch;
import com.allclear.tastytrack.domain.restaurant.entity.Restaurant;

import java.util.List;

public interface RestaurantService {
	Restaurant getRestaurant(int id);
	List<Restaurant> getRestaurantSearchByRegion(String dosi, String sgg, String type);

}
