package fer.proinz.prijave.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GeoConversionService {

    private final RestTemplate restTemplate;

    public String convertCoordinatesToAddress(Double latitude, Double longitude) {
        String apiUrl = "https://geocode.maps.co/reverse?lat=" + latitude + "&lon=" + longitude + "&api_key=659fe6b1cbeef330011151kjl43d55e";

        return restTemplate.getForObject(apiUrl, String.class);
    }

    public String convertAddressToCoordinates(String address) {
        String apiUrl = "https://geocode.maps.co/search?q=" + address + "&api_key=659fe6b1cbeef330011151kjl43d55e";

        return restTemplate.getForObject(apiUrl, String.class);
    }

}
