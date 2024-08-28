package com.allclear.tastytrack.domain.address.service;

import com.allclear.tastytrack.domain.address.entity.Address;

public interface AddressService {
	public Address getAddress(Long restaurantId);
}
