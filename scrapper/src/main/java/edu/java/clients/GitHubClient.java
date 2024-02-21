package edu.java.clients;

import edu.java.dto.github.RepositoryActivityResponse;
import edu.java.dto.github.RepositoryGeneralInfoResponse;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(accept = "application/vnd.github+json")
public interface GitHubClient {

    @GetExchange(url = "/repos/{owner}/{repositoryName}/activity")
    List<RepositoryActivityResponse> getActivityListResponse(
        @PathVariable String owner,
        @PathVariable String repositoryName
    );

    @GetExchange(url = "/repos/{owner}/{repositoryName}")
    RepositoryGeneralInfoResponse getGeneralInfoResponse(
        @PathVariable String owner,
        @PathVariable String repositoryName
    );

}
