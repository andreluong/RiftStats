package com.riftstats;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableRabbit
@EnableJpaRepositories
@EnableFeignClients
public class RiftStatsApplication {

	public static void main(String[] args) {
		SpringApplication.run(RiftStatsApplication.class, args);
	}
}
