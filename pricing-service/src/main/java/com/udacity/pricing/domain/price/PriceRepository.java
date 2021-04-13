package com.udacity.pricing.domain.price;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;


@Repository
public interface PriceRepository extends CrudRepository<Price, Long> {

    Price findByVehicleId(@Param("vehicle_id") Long vehicleId);

}
