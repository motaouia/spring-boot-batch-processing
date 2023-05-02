package org.medmota.batchprocessing.processor;

import org.medmota.batchprocessing.entities.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class CustomerProcessor implements ItemProcessor<Customer, Customer> {
	
	private Logger logger = LoggerFactory.getLogger(CustomerProcessor.class);

	@Override
	public Customer process(final Customer customer) throws Exception {
		final String firstName = customer.getFirstName().toUpperCase();
		final String lastName = customer.getLastName().toUpperCase();

		Customer transformedCustomer = new Customer(lastName, firstName);
		// logs the person entity to the application logs
		logger.info("Converting (" + customer + ") into (" + transformedCustomer + ")");

		return transformedCustomer;
	}

}
