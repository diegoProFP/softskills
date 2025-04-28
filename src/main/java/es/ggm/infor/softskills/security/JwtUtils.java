package es.ggm.infor.softskills.security;

import es.ggm.infor.moodleintegration.dto.UsuarioMoodleDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.Authentication;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JwtUtils {

    private static final String CLAIM_USUARIO_USERID = "userid";
    private static final String CLAIM_USUARIO_USERNAME = "username";
    private static final String CLAIM_USUARIO_FIRSTNAME = "firstname";
    private static final String CLAIM_USUARIO_LASTNAME = "lastname";
    private static final String CLAIM_USUARIO_FULLNAME = "fullname";
    private static final String CLAIM_USUARIO_LANG = "lang";
    private static final String CLAIM_USUARIO_SITENAME = "sitename";
    private static final String CLAIM_USUARIO_SITEURL = "siteurl";
    private static final String CLAIM_USUARIO_USERPICTUREURL = "userPictureUrl";


    public static String generateToken(Authentication authentication, UsuarioMoodleDTO userInfo, SecretKey secretKey) {
        List<String> roles = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toList());
        Map<String, Object> userClaims = convertUserInfoToClaims(userInfo);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("roles", roles)
                .addClaims(userClaims) // Añade los claims del UsuarioDTO
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(secretKey)
                .compact();
    }


    private static Map<String, Object> convertUserInfoToClaims(UsuarioMoodleDTO userInfo) {
        return Map.of(
                CLAIM_USUARIO_USERID, userInfo.getUserid(),
                CLAIM_USUARIO_USERNAME, userInfo.getUsername(),
                CLAIM_USUARIO_FIRSTNAME, userInfo.getFirstname(),
                CLAIM_USUARIO_LASTNAME, userInfo.getLastname(),
                CLAIM_USUARIO_FULLNAME, userInfo.getFullname(),
                CLAIM_USUARIO_LANG, userInfo.getLang(),
                CLAIM_USUARIO_SITENAME, userInfo.getSitename(),
                CLAIM_USUARIO_SITEURL, userInfo.getSiteurl(),
                CLAIM_USUARIO_USERPICTUREURL, userInfo.getUserPictureUrl()
        );
    }

    public static UsuarioMoodleDTO convertClaimsToUserInfo(Claims claims) {
        UsuarioMoodleDTO userInfo = new UsuarioMoodleDTO();
        userInfo.setUserid(claims.get(CLAIM_USUARIO_USERID, Long.class));
        userInfo.setUsername(claims.get(CLAIM_USUARIO_USERNAME, String.class));
        userInfo.setFirstname(claims.get(CLAIM_USUARIO_FIRSTNAME, String.class));
        userInfo.setLastname(claims.get(CLAIM_USUARIO_LASTNAME, String.class));
        userInfo.setFullname(claims.get(CLAIM_USUARIO_FULLNAME, String.class));
        userInfo.setLang(claims.get(CLAIM_USUARIO_LANG, String.class));
        userInfo.setSitename(claims.get(CLAIM_USUARIO_SITENAME, String.class));
        userInfo.setSiteurl(claims.get(CLAIM_USUARIO_SITEURL, String.class));
        userInfo.setUserPictureUrl(claims.get(CLAIM_USUARIO_USERPICTUREURL, String.class));
        return userInfo;
    }


}
