package ch.fhnw.digi.demo;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.stereotype.Component;

@Component
public class Publisher {

	@Autowired
	private SimpleUi simpleUi;

	@Autowired
	private JmsTemplate jmsTemplate;

	@PostConstruct
	void run() {

		int counter = 0;

		while (true) {

			jmsTemplate.setMessageConverter(jacksonJmsMessageConverter());

			final String correlationID = "message" + counter;

			// publish a new GreeterMessage to the channel "greetRequests"
			jmsTemplate.convertAndSend("greetRequests", new GreeterMessage("Person " + counter), m -> {
				m.setStringProperty("someHeaderField", "someImportantValue");
				m.setJMSCorrelationID(correlationID);
				return m;
			});

			// just so we see someting in our gui
			simpleUi.setMessage("published Message " + counter);

			counter++;

			// wait for 2 seconds
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
		}
	}

	// used to convert our java messagage object into a JSON String that can be sent
	public MessageConverter jacksonJmsMessageConverter() {
		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		converter.setTargetType(MessageType.TEXT);
		converter.setTypeIdPropertyName("_type");
		return converter;
	}

}
