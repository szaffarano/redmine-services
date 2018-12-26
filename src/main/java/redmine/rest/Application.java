package redmine.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.userdetails.User;
import utils.SecurityUtils;

import javax.naming.InvalidNameException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

import static org.springframework.security.core.authority.AuthorityUtils.NO_AUTHORITIES;
import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

@SpringBootApplication
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class Application extends WebSecurityConfigurerAdapter {
    Logger logger = LoggerFactory.getLogger(Application.class);

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .x509()
                .subjectPrincipalRegex("CN=(.*?)(?:,|$)")
                .authenticationUserDetailsService(authenticationUserDetailsService())
        ;
    }

    @Bean
    public AuthenticationUserDetailsService authenticationUserDetailsService() {
        return new AuthenticationUserDetailsService() {
            @Override
            public UserDetails loadUserDetails(Authentication token) throws UsernameNotFoundException {
                if (!X509Certificate.class.isInstance(token.getCredentials())) {
                    throw new UsernameNotFoundException("Unknown certificate info");
                }

                X509Certificate cert = X509Certificate.class.cast(token.getCredentials());

                Map<String, String> dn = null;
                try {
                    dn = SecurityUtils.getDN(cert);
                } catch (InvalidNameException e) {
                    throw new UsernameNotFoundException("Invalid certificate DN");
                }

                logger.debug("cn: {}, ou: {}", dn.get("cn"), dn.get("ou"));

                List<GrantedAuthority> grantedAuthorities = NO_AUTHORITIES;
                if (dn.containsKey("cn") && dn.containsKey("ou")) {
                    grantedAuthorities = createAuthorityList("ROLE_USER", dn.get("ou"));
                }

                return new User(dn.get("cn"), "", grantedAuthorities);
            }
        };
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) {
                return new User(username, "",
                        AuthorityUtils
                                .commaSeparatedStringToAuthorityList("ROLE_USER"));
            }
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}