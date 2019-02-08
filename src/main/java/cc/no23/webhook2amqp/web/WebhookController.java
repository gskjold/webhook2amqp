package cc.no23.webhook2amqp.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Gunnar Skjold @ Origin AS on 08.02.19.
 */
@RestController
public class WebhookController {
	private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@RequestMapping(value = "/{exchange}/{routingKey}")
	public ResponseEntity webhook(@PathVariable String exchange, @PathVariable String routingKey,
	                              @RequestBody(required = false) String body,
	                              @RequestParam(required = false) String payload
	) {
		logger.info("Received message for '{}' with routing key '{}'", exchange, routingKey);
		logger.debug("Message was", body);
		try {
			if(body != null) {
				rabbitTemplate.convertAndSend(exchange, routingKey, body);
			}
			if(payload != null) {
				rabbitTemplate.convertAndSend(exchange, routingKey, payload);
			}
			return ResponseEntity.status(HttpStatus.OK).build();
		} catch (Exception e) {
			logger.error("Unable to send to AMQP", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
