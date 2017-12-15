package com.lxr.iot;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.springframework.boot.SpringApplication.run;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class ClientApplication
{
    public static void main(String[] args) {
        ConfigurableApplicationContext run = run(ClientApplication.class, args);
    }
}
