package com.allclear.tastytrack.domain.address.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.allclear.tastytrack.domain.address.entity.Address;
import com.allclear.tastytrack.domain.address.repository.AddressRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService{
	private AddressRepository addressRepository;

	@Override
	public Address getAddress(Long restaurantId) {
		Optional<Address> addressOpt = addressRepository.findById(restaurantId);
		if(addressOpt.isEmpty()) {
			throw new RuntimeException("조회된 레스토랑이 없습니다.");
		}
		return addressOpt.get();
	}
}
