package de.sasch.lexware;

import de.sasch.lexware.service.BankDTO;
import io.swagger.v3.oas.models.media.JsonSchema;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
@RegisterReflectionForBinding({BankDTO.class, JsonSchema.class})
public class LexwareApplication {

    public static void main(String[] args) {
        SpringApplication.run(LexwareApplication.class, args);
    }

}
