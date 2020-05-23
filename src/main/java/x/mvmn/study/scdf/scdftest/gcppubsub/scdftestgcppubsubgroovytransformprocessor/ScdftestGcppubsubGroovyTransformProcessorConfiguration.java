package x.mvmn.study.scdf.scdftest.gcppubsub.scdftestgcppubsubgroovytransformprocessor;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.app.groovy.transform.processor.GroovyTransformProcessorProperties;
import org.springframework.cloud.stream.app.groovy.transform.processor.ScriptVariableGeneratorConfiguration;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.groovy.GroovyScriptExecutingMessageProcessor;
import org.springframework.integration.handler.MessageProcessor;
import org.springframework.integration.scripting.ScriptVariableGenerator;
import org.springframework.messaging.Message;
import org.springframework.scripting.support.ResourceScriptSource;

@EnableBinding(Processor.class)
@EnableConfigurationProperties(GroovyTransformProcessorProperties.class)
@Import(ScriptVariableGeneratorConfiguration.class)
public class ScdftestGcppubsubGroovyTransformProcessorConfiguration {

	@Autowired
	private ScriptVariableGenerator scriptVariableGenerator;

	@Autowired
	private GroovyTransformProcessorProperties properties;

	@Autowired(required = false)
	@Qualifier("groovyTransformExceptionHandler")
	private Consumer<Throwable> exceptionHandler;

	@Bean
	@ServiceActivator(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)
	public MessageProcessor<?> transformer() {
		GroovyScriptExecutingMessageProcessor groovyProcessor = new GroovyScriptExecutingMessageProcessor(
				new ResourceScriptSource(properties.getScript()), scriptVariableGenerator);
		return new MessageProcessor<Object>() {
			private final Logger LOGGER = LoggerFactory.getLogger(GroovyScriptExecutingMessageProcessor.class);

			@Override
			public Object processMessage(Message<?> message) {
				try {
					return groovyProcessor.processMessage(message);
				} catch (Throwable t) {
					LOGGER.error("Error processing message with GroovyScriptExecutingMessageProcessor", t);
					if (exceptionHandler != null) {
						try {
							exceptionHandler.accept(t);
							return null;
						} catch (Exception e) {
							LOGGER.error("Error on handling exception", e);
							throw t;
						}
					} else {
						// Currently PubSub client seems to do nothing on exceptions, not even logging them - this will be further investigated and a bug would
						// be filed for PubSub client if necessary
						throw t;
					}
				}
			}
		};
	}
}
