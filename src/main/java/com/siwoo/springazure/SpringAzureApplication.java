package com.siwoo.springazure;

import com.siwoo.springazure.model.Command;
import com.siwoo.springazure.repository.CommandRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.siwoo.springazure.repository")
public class SpringAzureApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAzureApplication.class, args);
    }

    @Component
    private static class AppVariables {
        @Autowired
        private Environment environment;

        private static String[] names = {"db_url", "db_username", "db_password", "env"};

        public Pair[] getVariables() {
            Pair[] pairs = new Pair[names.length];
            for (int i=0; i<names.length; i++)
                pairs[i] = new Pair(names[i], environment.getProperty(names[i]));
            return pairs;
        }

        public String get(String key) {
            checkNotNull(key);
            Pair[] pairs = getVariables();
            for (Pair pair: pairs)
                if (key.equals(pair.key))
                    return pair.value;
            throw new RuntimeException(String.format("Not found variable %s", key));
        }
    }

    @Getter @Setter @ToString
    private static class Pair {
        private String key, value;

        public Pair(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    @RestController
    private static class MainController {

        @Autowired
        private AppVariables appVariables;

        @Autowired
        private CommandRepository commandRepository;

        @GetMapping("/")
        public ResponseEntity<List<Pair>> greet() {
            List<Pair> variables = Arrays.stream(appVariables.getVariables()).collect(Collectors.toList());
            return ResponseEntity.ok(variables);
        }

        @GetMapping("/commands")
        public ResponseEntity<List<Command>> commands() {
            return ResponseEntity.ok(commandRepository.findAll());
        }

    }
}
