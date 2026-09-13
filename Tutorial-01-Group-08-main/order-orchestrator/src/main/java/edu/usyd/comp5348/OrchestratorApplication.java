package edu.usyd.comp5348;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class OrchestratorApplication {
    public static void main(String[] args){ SpringApplication.run(OrchestratorApplication.class, args); }
}
