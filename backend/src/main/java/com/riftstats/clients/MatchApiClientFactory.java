package com.riftstats.clients;

import com.riftstats.enums.RegionGroup;
import feign.Client;
import feign.Contract;
import feign.Feign;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MatchApiClientFactory {
    private final Decoder decoder;
    private final Encoder encoder;
    private final Client client;
    private final Contract contract;

    @Autowired
    public MatchApiClientFactory(Decoder decoder, Encoder encoder, Client client, Contract contract) {
        this.decoder = decoder;
        this.encoder = encoder;
        this.client = client;
        this.contract = contract;
    }

    public MatchApiClient getClientForRegion(RegionGroup regionGroup) {
        String baseUrl = String.format("https://%s.api.riotgames.com", regionGroup.getValue());
        return Feign.builder()
                .client(client)
                .encoder(encoder)
                .decoder(decoder)
                .contract(contract)
                .target(MatchApiClient.class, baseUrl);
    }
}