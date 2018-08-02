package io.kf.coordinator.service.release;

import com.google.common.base.Joiner;
import io.kf.coordinator.dto.ReleaseResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class ReleaseService {

  private static final Joiner PATH_JOINER = Joiner.on("/");
  private static final String RELEASES = "releases";
  private final String serverUrl;
  private RestTemplate rest = new RestTemplate();

  @Autowired
  public ReleaseService(
      @Value("${release-coordinator.url}")
      @NonNull String serverUrl) {
    this.serverUrl = serverUrl;
  }

  private String getReleaseUrl(String releaseId){
    return PATH_JOINER.join(serverUrl, RELEASES, releaseId);
  }

  public Optional<ReleaseResponse> getRelease(@NonNull String releaseId){
    ResponseEntity<ReleaseResponse> response;
    try{
      response = rest.getForEntity(getReleaseUrl(releaseId), ReleaseResponse.class);
    } catch (HttpClientErrorException e){
      if (e.getStatusCode().equals(NOT_FOUND)){
        return Optional.empty();
      }
      throw e;
    }
    return Optional.of(response.getBody());
  }

}