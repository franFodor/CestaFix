package fer.proinz.prijave.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fer.proinz.prijave.dto.ReportRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GeoConversionService {

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    @Retryable(backoff = @Backoff(delay = 1000, multiplier = 2))
    public ReportRequestDto convertCoordinatesToAddress(ReportRequestDto reportRequest) throws JsonProcessingException {
        String apiUrl = "https://geocode.maps.co/reverse?" +
                        "lat=" + reportRequest.getLatitude() +
                        "&lon=" + reportRequest.getLongitude() +
                        "&api_key=659fe6b1cbeef330011151kjl43d55e";

        JsonNode jsonNode = objectMapper.readTree(restTemplate.getForObject(apiUrl, String.class));
        String address = jsonNode.get("display_name").asText();
        reportRequest.setAddress(address);

        return reportRequest;
    }

    @Retryable(backoff = @Backoff(delay = 1000, multiplier = 2))
    public ReportRequestDto convertAddressToCoordinates(ReportRequestDto reportRequest) throws JsonProcessingException {
        String apiUrl = "https://geocode.maps.co/search?q=" +
                        reportRequest.getAddress() +
                        "&api_key=659fe6b1cbeef330011151kjl43d55e";

        JsonNode[] jsonNodes = objectMapper.readValue(restTemplate.getForObject(apiUrl, String.class), JsonNode[].class);
        reportRequest.setLatitude(jsonNodes[0].get("lat").asDouble());
        reportRequest.setLongitude(jsonNodes[0].get("lon").asDouble());

        return  reportRequest;
    }

}
