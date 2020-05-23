package x.mvmn.study.scdf.scdftest.gcppubsub.scdftestgcppubsubgroovytransformprocessor;

import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ScdftestGcppubsubGroovyTransformProcessorConfiguration.class)
public class ScdftestGcppubsubGroovyTransformProcessorApplication {
	public static void main(String[] args) {
		SpringApplication.run(ScdftestGcppubsubGroovyTransformProcessorApplication.class, args);
	}

	@Bean
	@Qualifier("groovyTransformExceptionHandler")
	public Consumer<Throwable> groovyTransformExceptionHandler() {
		return t -> {}; // Do nothing
	}
}
