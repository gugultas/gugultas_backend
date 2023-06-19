package com.serbest.magazine.backend.service.impl;

import com.serbest.magazine.backend.service.MailService;
import com.serbest.magazine.backend.dto.auth.*;
import com.serbest.magazine.backend.dto.general.MessageResponseDTO;
import com.serbest.magazine.backend.entity.Author;
import com.serbest.magazine.backend.entity.RefreshToken;
import com.serbest.magazine.backend.entity.Role;
import com.serbest.magazine.backend.exception.CustomApplicationException;
import com.serbest.magazine.backend.exception.ResourceNotFoundException;
import com.serbest.magazine.backend.exception.TokenRefreshException;
import com.serbest.magazine.backend.mapper.UserMapper;
import com.serbest.magazine.backend.repository.AuthorRepository;
import com.serbest.magazine.backend.repository.RoleRepository;
import com.serbest.magazine.backend.security.jwt.JwtUtils;
import com.serbest.magazine.backend.security.services.RefreshTokenService;
import com.serbest.magazine.backend.security.services.UserDetailsImpl;
import com.serbest.magazine.backend.service.AuthService;
import io.jsonwebtoken.lang.Assert;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    @Value("${magazine.frontend.link}")
    private String clientLink;
    private final AuthorRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;


    public AuthServiceImpl(AuthorRepository userRepository, AuthenticationManager authenticationManager,
                           RoleRepository roleRepository, UserMapper userMapper, JwtUtils jwtUtils, RefreshTokenService refreshTokenService, MailService mailService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public RegisterResponseDTO register(RegisterRequestDTO requestDTO) {

        // add check for username exists in database
        if (userRepository.existsByUsername(requestDTO.getUsername())) {
            throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "Username is already exists!");
        }

        // add check for email exists in database
        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "Email is already exists!");
        }

        Integer authors = userRepository.findAll().size();

        if (authors == 0) {
            Author user = userMapper.registerRequestDTOToUser(requestDTO);
            Set<Role> roles = new HashSet<>();

            Role newAdminRole = roleRepository.save(new Role("ROLE_ADMIN"));
            Role newEditorRole = roleRepository.save(new Role("ROLE_EDITOR"));
            Role newAuthorRole = roleRepository.save(new Role("ROLE_AUTHOR"));
            Role newUserRole = roleRepository.save(new Role("ROLE_USER"));

            roles.add(newAdminRole);
            roles.add(newEditorRole);
            roles.add(newAuthorRole);
            roles.add(newUserRole);
            user.setRoles(roles);

            Author newUser = userRepository.save(user);

            String token = jwtUtils.generateValidationToken(newUser);

            mailService.sendEmailWithMimeMessage(requestDTO.getEmail(), "Hesap Aktivasyonu",
                    buildEmailForEmailActivation(requestDTO.getUsername(), clientLink + "/activation/" + token));

            return new RegisterResponseDTO(newUser.getUsername());

        }

        Author user = userMapper.registerRequestDTOToUser(requestDTO);

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("ROLE_USER").get();
        roles.add(userRole);
        user.setRoles(roles);

        Author newUser = userRepository.save(user);

        String token = jwtUtils.generateValidationToken(newUser);

        mailService.sendEmailWithMimeMessage(requestDTO.getEmail(), "Hesap Aktivasyonu",
                buildEmailForEmailActivation(requestDTO.getUsername(), clientLink + "/activation/" + token));

        return new RegisterResponseDTO(newUser.getUsername());
    }

    @Override
    public MessageResponseDTO sendActivation(String email) {

        Author newUser = userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("Author", "email", email)
        );

        String token = jwtUtils.generateValidationToken(newUser);

        mailService.sendEmailWithMimeMessage(newUser.getEmail(), "Hesap Aktivasyonu",
                buildEmailForEmailActivation(newUser.getUsername(), clientLink + "/activation/" + token));

        return new MessageResponseDTO("Aktivasyon kodu email adresinize gönderilmiştir.");
    }

    @Override
    public MessageResponseDTO confirmActivation(String token) {
        if (token != null && jwtUtils.validateActivationToken(token)) {
            String email = jwtUtils.getUserEmailFromJwtActivationToken(token);

            Author author = userRepository.findByEmail(email).orElseThrow(
                    () -> new ResourceNotFoundException("Author", "email", email)
            );

            author.setEnabled(true);

            userRepository.save(author);

            return new MessageResponseDTO("Hesabınız başarıyla aktive edilmiştir.");
        }

        return new MessageResponseDTO("Hesabınız aktive olamadı. Lütfen tekrar deneyin.");

    }

    @Override
    public JWTAuthResponse login(HttpServletRequest request, LoginRequestDTO loginDto) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getUsernameOrEmail(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String accessToken = jwtUtils.generateAccessToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
        ResponseCookie jwtRefreshCookie = jwtUtils.generateRefreshJwtCookie(refreshToken.getToken());

        JWTAuthResponse jwtAuthResponse = new JWTAuthResponse();
        jwtAuthResponse.setAccessToken(accessToken);
        jwtAuthResponse.setRefreshTokenCookie(jwtRefreshCookie);
        jwtAuthResponse.setRoles(roles);
        jwtAuthResponse.setUserId(userDetails.getId());
        jwtAuthResponse.setEmail(userDetails.getEmail());
        jwtAuthResponse.setUsername(userDetails.getUsername());
        jwtAuthResponse.setImage(userDetails.getImage());

        return jwtAuthResponse;
    }

    @Override
    public RefreshTokenResponseDTO refreshTokenHandle(HttpServletRequest request) {
        String refreshToken = jwtUtils.getJwtRefreshFromCookies(request);

        if ((refreshToken != null) && (refreshToken.length() > 0)) {
            return refreshTokenService.findByToken(refreshToken)
                    .map(refreshTokenService::verifyExpiration)
                    .map(RefreshToken::getAuthor)
                    .map(author -> {
                        String accessToken = jwtUtils.generateAccessToken(author);
                        RefreshTokenResponseDTO refreshTokenResponseDTO = userMapper.authorToRefreshTokenResponseDTO(author);
                        refreshTokenResponseDTO.setAccessToken(accessToken);
                        return refreshTokenResponseDTO;
                    })
                    .orElseThrow(() -> new TokenRefreshException(refreshToken,
                            "Refresh token is not in database!"));
        }

        return new RefreshTokenResponseDTO(null, null, null, null, "Refresh Token is empty!");
    }

    @Override
    public MessageResponseDTO forgotPassword(ForgotPasswordRequestDTO requestDTO) {
        validateAndSanitizeFieldName("email", requestDTO.getEmail());

        Author newUser = userRepository.findByEmail(requestDTO.getEmail()).orElseThrow(
                () -> new ResourceNotFoundException("Author", "email", requestDTO.getEmail())
        );

        String token = jwtUtils.generateValidationToken(newUser);

        mailService.sendEmailWithMimeMessage(newUser.getEmail(), "Şifre Resetleme",
                buildEmailForPasswordReset(newUser.getUsername(), clientLink + "/resetPassword/" + token));

        return new MessageResponseDTO("Şifre sıfırlama isteğiniz email adresinize gönderilmiştir. " +
                "Lütfen kontrol ediniz");
    }

    @Override
    public MessageResponseDTO resetPassword(String token, ResetPasswordRequestDTO requestDTO) {
        validateAndSanitizeFieldName("password", requestDTO.getPassword());

        if (token != null && jwtUtils.validateActivationToken(token)) {
            String email = jwtUtils.getUserEmailFromJwtActivationToken(token);

            Author author = userRepository.findByEmail(email).orElseThrow(
                    () -> new ResourceNotFoundException("Author", "email", email)
            );

            author.setPassword(passwordEncoder.encode(requestDTO.getPassword()));

            userRepository.save(author);

            return new MessageResponseDTO("Şifreniz başarıyla değiştirildi. Yeni şifrenizle giriş yapabilirsiniz.");
        }

        return new MessageResponseDTO("Şifre değiştirilemedi. Lütfen tekrar deneyin.");
    }

    @Override
    public ResponseCookie logout() {
        Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principle.toString() != "anonymousUser") {
            String username = ((UserDetailsImpl) principle).getUsername();
            refreshTokenService.deleteByUsername(username);
        }

        ResponseCookie jwtRefreshCookie = jwtUtils.getCleanJwtRefreshCookie();
        return jwtRefreshCookie;
    }

    private String buildEmailForEmailActivation(String name, String link) {
        return "<div style=\"max-width: 700px; margin:auto; border: 10px solid #51545b; padding: 50px 20px; font-size: 110%;\">\n" +
                " <h2 style=\"text-align: center; text-transform: uppercase;color: #51545B;\">Gugultaş'tan Mesaj Var</h2>\n" +
                " <p>\n" +
                "   Merhaba " + name + ". Öncelikle dergimize üye olmaya karar verdiğiniz için kıvanç duyuyoruz. " +
                "   Dergimizden tam anlamıyla yararlanmak ve gerçek bir kişi olduğunuzdan emin olmak için hesabınızı aktive etmenizi rica ediyoruz. " +
                "   Aşağıdaki butona tıklayarak hesabınızı aktif hale getirebilirsiniz ancak acele edin çünkü size verdiğimiz bu link kısa bir süre içinde geçersiz hale gelecek. " +
                "   Eğer link geçersiz hale geldiyse , profil sayfanıza gidip tekrardan aktivaston linkini elde edebilirsiniz. \n" +
                "  </p>\n" +
                "  \n" +
                "  <a href=" + link + " style=\"background: #350664 ; border-radius: 7px ; text-decoration: none; color: #fffcfc ; padding: 10px 20px; margin: 10px 0; display: inline-block;\">Email Onayla </a>\n" +
                "  \n" +
                "  <p>Eğer yukarıdaki buton herhangi bir nedenle çalışmıyor ise aşağıdaki linke tıklayın:</p>\n" +
                "  \n" +
                "  <div>" + link + "</div>\n" +
                "  </div>";
    }

    private String buildEmailForPasswordReset(String name, String link) {
        return "<div style=\"max-width: 700px; margin:auto; border: 10px solid #51545b; padding: 50px 20px; font-size: 110%;\">\n" +
                " <h2 style=\"text-align: center; text-transform: uppercase;color: #51545B;\">Gugultaş'tan Mesaj Var</h2>\n" +
                " <p>\n" +
                "   Merhaba " + name + ". Duyduk ki şifrenizi unutmuşsunuz. Endişelenmeyin , aşağıdaki butona tıklayarak şifrenizi değiştirebilir ve yeni şifrenize kavuşabilirsiniz." +
                "  </p>\n" +
                "  \n" +
                "  <a href=" + link + " style=\"background: #350664 ; border-radius: 7px ; text-decoration: none; color: #fffcfc ; padding: 10px 20px; margin: 10px 0; display: inline-block;\">Şifreyi Resetle </a>\n" +
                "  \n" +
                "  <p>Eğer yukarıdaki buton herhangi bir nedenle çalışmıyor ise aşağıdaki linke tıklayın:</p>\n" +
                "  \n" +
                "  <div>" + link + "</div>\n" +
                "  </div>";
    }

    private void validateAndSanitizeFieldName(String fieldName, String fieldValue) {
        Assert.notNull(fieldValue, "Provide a valid " + fieldName + " , please.");
        if (fieldValue.isEmpty() || fieldValue.isBlank()) {
            throw new IllegalArgumentException("Provide a valid " + fieldName + " , please.");
        }
    }

}
