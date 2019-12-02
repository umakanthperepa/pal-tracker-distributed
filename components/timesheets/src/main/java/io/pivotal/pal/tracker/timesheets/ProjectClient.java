package io.pivotal.pal.tracker.timesheets;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestOperations;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProjectClient {

    private final Logger PROJECT_CLIENT_LOGGER = LoggerFactory.getLogger(ProjectClient.class.getName());
    private final RestOperations restOperations;
    private final String endpoint;
    private Map<Long, ProjectInfo> procjectClientCache = new ConcurrentHashMap<Long, ProjectInfo>();


    public ProjectClient(RestOperations restOperations, String registrationServerEndpoint) {
        this.restOperations = restOperations;
        this.endpoint = registrationServerEndpoint;
    }
    @HystrixCommand(fallbackMethod = "getProjectFromCache")
    public ProjectInfo getProject(long projectId) {

        ProjectInfo projectInfo = restOperations.getForObject(endpoint + "/projects/" + projectId, ProjectInfo.class);
        procjectClientCache.put(projectId, projectInfo);

        return projectInfo;
    }

    public ProjectInfo getProjectFromCache(long projectId) {

        PROJECT_CLIENT_LOGGER.warn("Returning Cached Data for Project {}", projectId);

        return procjectClientCache.get(projectId);
    }
}
