package org.medmota.batchprocessing.config;

import org.medmota.batchprocessing.entities.Customer;
import org.medmota.batchprocessing.processor.CustomerProcessor;
import org.medmota.batchprocessing.repositories.CustomerRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	@Lazy
	private CustomerRepository customerRepository;

	@Bean
	public FlatFileItemReader<Customer> reader() {
		return new FlatFileItemReaderBuilder<Customer>().name("customerReader")
				.resource(new ClassPathResource("data.csv")).delimited().names(new String[] { "firstName", "lastName" })
				.fieldSetMapper(new BeanWrapperFieldSetMapper<Customer>() {
					{
						setTargetType(Customer.class);
					}
				}).build();
	}

	@Bean
	public RepositoryItemWriter<Customer> writer() {
		RepositoryItemWriter<Customer> iwriter = new RepositoryItemWriter<>();
		iwriter.setRepository(customerRepository);
		iwriter.setMethodName("save");
		return iwriter;
	}

	// Creates an instance of PersonProcessor that converts one data form to
	// another. In our case the data form is maintained.
	@Bean
	public CustomerProcessor processor() {
		return new CustomerProcessor();
	}

	// Batch jobs are built from steps. A step contains the reader, processor and
	// the writer.
	@Bean
	public Step step1(ItemReader<Customer> itemReader, ItemWriter<Customer> itemWriter) throws Exception {

		return this.stepBuilderFactory.get("step1").<Customer, Customer>chunk(5).reader(itemReader)
				.processor(processor()).writer(itemWriter).build();

		// return new StepBuilder("step1, jo)
	}

	@Bean
	public Job customerUpdateJob(JobCompletionNotificationListener listener, Step step1) throws Exception {

		return this.jobBuilderFactory.get("customerUpdateJob").incrementer(new RunIdIncrementer()).listener(listener)
				.start(step1).build();
	}

}
