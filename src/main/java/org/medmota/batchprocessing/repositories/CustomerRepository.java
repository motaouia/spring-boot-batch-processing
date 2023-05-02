package org.medmota.batchprocessing.repositories;

import org.medmota.batchprocessing.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
