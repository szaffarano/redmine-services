package redmine.rest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import redmine.rest.domain.Users;
import utils.SecurityUtils;

import static java.lang.String.format;

@RestController
public class UserController {
    public static final String APIKEY_PATTERN = "redmine.%s.api-key";
    public static final String URL_PATTERN = "redmine.%s.url";

    private Logger logger = LoggerFactory.getLogger(Application.class);

    @Autowired
    private Environment env;

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @RequestMapping(
            path = "/{redmine-instance}/user",
            method = RequestMethod.GET,
            produces = "application/json"
    )
    public List<Users.User> user(@PathVariable("redmine-instance") String redmineInstance) {

        String currentUser = SecurityUtils.currentUser().get();
        logger.debug("[{}] About to query users of redmine '{}'", currentUser, redmineInstance);

        if (!SecurityUtils.hasAuthority(redmineInstance)) {
            throw new Exceptions.ForbiddenException(format("Not allowed to query %s", redmineInstance));
        }

        String url = env.getProperty(format(URL_PATTERN, redmineInstance));
        String apiKey = env.getProperty(format(APIKEY_PATTERN, redmineInstance));

        if (url == null) {
            throw new Exceptions.NotConfiguredException(format("Configuration for '%s' not found", redmineInstance));
        }

        logger.debug("[{}] getting users from {}", currentUser, url);

        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Redmine-API-Key", apiKey);
        HttpEntity entity = new HttpEntity(headers);

        ResponseEntity<Users> resp = rt.exchange(format("%s/users.json", url), HttpMethod.GET, entity, Users.class);

        return resp.getBody().getUsers();
    }
}