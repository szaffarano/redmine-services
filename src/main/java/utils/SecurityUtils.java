package utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SecurityUtils {
    public static boolean hasAuthority(String name) {
        SecurityContext ctx = SecurityContextHolder.getContext();
        if (ctx == null) {
            return false;
        }

        Authentication auth = ctx.getAuthentication();
        if (auth == null) {
            return false;
        }

        return auth.getAuthorities()
                .stream()
                .filter(a -> ((GrantedAuthority) a).getAuthority().equals(name))
                .count() != 0;
    }

    public static Map<String, String> getDN(X509Certificate cert) throws InvalidNameException {
        List<Rdn> rdns = new LdapName(cert.getSubjectX500Principal().getName()).getRdns();

        return rdns.stream()
                .collect(Collectors.toMap(r -> r.getType().toLowerCase(), r -> r.getValue().toString()));
    }

    public static Optional<String> currentUser() {
        SecurityContext ctx = SecurityContextHolder.getContext();
        if (ctx == null) {
            return Optional.empty();
        }

        Authentication auth = ctx.getAuthentication();
        if (auth == null) {
            return Optional.empty();
        }

        return Optional.of(auth.getName());
    }
}
