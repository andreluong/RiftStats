package com.riftstats.clients;

import com.riftstats.enums.RegionCode;
import feign.Client;
import feign.Contract;
import feign.Feign;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LeagueApiClientFactory {
    private final Decoder decoder;
    private final Encoder encoder;
    private final Client client;
    private final Contract contract;

    @Autowired
    public LeagueApiClientFactory(Decoder decoder, Encoder encoder, Client client, Contract contract) {
        this.decoder = decoder;
        this.encoder = encoder;
        this.client = client;
        this.contract = contract;
    }

    public LeagueApiClient getClientForRegion(RegionCode regionCode) {
        String baseUrl = String.format("https://%s.api.riotgames.com", regionCode.getValue());
        return Feign.builder()
                .client(client)
                .encoder(encoder)
                .decoder(decoder)
                .contract(contract)
                .target(LeagueApiClient.class, baseUrl);
    }
}