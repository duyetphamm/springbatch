package org.jobs;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.StoredProcedureItemReader;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlInOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.mail.javamail.JavaMailSender;

import org.configuration.CommonBatchConfiguration;
import org.dtos.Customer;

@Configuration
public class JobConfig extends CommonBatchConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(JobConfig.class);
	
	@Autowired
	private JavaMailSender mailSender;
		
	@Bean
	public Job job1() {
		return jobBuilderFactory
				.get("job1")
				.incrementer(new RunIdIncrementer())
				.start(step1())
				.build();
	}
	
	public Step step1() {
		return stepBuilderFactory
				.getstep1
				.<Customer, Customer> chunk(1)
		        .reader(getCustomer())
		        .processor(processCustomer())
		        .writer(callCustomer())
				.build();
	}
	
	
	public StoredProcedureItemReader<Customer> getCustomer() {
		StoredProcedureItemReader<Customer> reader = new StoredProcedureItemReader<Customer>();
		SqlParameter[] parameters = { 
				new SqlInOutParameter("curmycursor", Types.REF_CURSOR)
				};      
		reader.setDataSource(dataSource);
		reader.setProcedureName("PACKAGE.GET_CUSTOMER");
		reader.setRowMapper(new BeanPropertyRowMapper<>(Customer.class));
		reader.setParameters(parameters);
		reader.setRefCursorPosition(1);   
		return reader;
	}
		
	public ItemProcessor<Customer, Customer> processCustomer() {
		return new ItemProcessor<Customer, Customer>() {

			private String fromMail = "admin@okulele.com";

			private String subject = "Batch Job";

			private String mailTemplate = "/templates/email.properties";

			@Override
			public Customer process(Customer customer) throws Exception {
				if (customer.getEmail() != null && customer.getEmail().length() > 0) {
					MimeMessage message = mailSender.createMimeMessage();
					MimeMultipart multipart = new MimeMultipart("related");
					BodyPart messageBodyPart = new MimeBodyPart();
					String htmlText = "";
					
					message.setFrom(new InternetAddress(fromMail));
					InternetAddress[] addresses = InternetAddress.parse(customer.getEmail().replaceAll(";",","));
			    	message.addRecipients(Message.RecipientType.TO, addresses);
			    	message.setSubject(subject);
			    		
					messageBodyPart = new MimeBodyPart();
					htmlText = StringMethod.readFileToString(mailTemplate);
					htmlText = htmlText.replaceAll("@custName", customer.getUsername());

					messageBodyPart.setContent(htmlText, "text/html");
					multipart.addBodyPart(messageBodyPart);
					
			    	message.setContent(multipart);
			    	message.setSubject(subject);
			    	customer.setMimeMessage(message);
				}
				return customer;
			}

		};
	}
	
	public CompositeItemWriter<Customer> remindCustomer() {
		List<ItemWriter<? super Customer>> delegates = new ArrayList<>(2);

		delegates.add(new ItemWriter<Customer>(){

			@Override
			public void write(List<? extends Customer> customerList) throws Exception {
				MimeMessage messages ;
				for (Customer customer : customerList) {
					if (customer.getEmail() != null && customer.getEmail().length() > 0) {		
						messages = customer.getMimeMessage();
						mailSender.send(messages);
					}
				}
			}
			
		});

		delegates.add(new JdbcBatchItemWriter<Customer>() {

			@Override
			public void write(List<? extends Customer> entrustCustomerList) throws Exception {
				JdbcTemplate template = new JdbcTemplate(dataSource);
				for (Customer customer : entrustCustomerList) {
					String sql = "INSERT INTO PHONEUSER (ID, PHONENUM) values(id.NEXTVAL,?)";
					if (customer.getPhone() != null && customer.getPhone().length() > 0) {

						template.update(sql, new Object[] {customer.getPhone()});
					}
				}
			}
		});

		CompositeItemWriter<Customer> processor = new CompositeItemWriter<Customer>();
		processor.setDelegates(delegates);

		return processor;
	}	
}
