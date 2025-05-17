package com.riftstats.configs;

import feign.Client;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.optionals.OptionalDecoder;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public Decoder feignDecoder() {
        return new OptionalDecoder(new GsonDecoder());
    }

    @Bean
    public Encoder feignEncoder() {
        return new GsonEncoder();
    }

    @Bean
    public Client feignClient() {
        return new Client.Default(null, null);
    }

    @Bean
    public feign.Contract feignContract() {
        return new SpringMvcContract();
    }
}
