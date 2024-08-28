package com.allclear.tastytrack.domain.address.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.allclear.tastytrack.domain.address.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
