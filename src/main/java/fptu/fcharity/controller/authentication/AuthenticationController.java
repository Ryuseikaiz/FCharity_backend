package fptu.fcharity.controller.authentication;

import fptu.fcharity.dto.authentication.*;
import fptu.fcharity.entity.User;
import fptu.fcharity.service.manage.user.UserService;
import fptu.fcharity.service.authentication.AuthenticationService;
import fptu.fcharity.service.authentication.JwtService;
import fptu.fcharity.response.authentication.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final UserDetailsService userDetailsService;


    public AuthenticationController(JwtService jwtService,
                                    AuthenticationManager authenticationManager,
                                    AuthenticationService authenticationService,
                                    UserDetailsService userDetailsService,
                                    UserService userService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.userService = userService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/reset-password-otp")
    public ResponseEntity<?> sendResetPasswordOTP(@RequestBody ResendOTPDto resendOTPDto) {
         authenticationService.sendResetPwdOTPCode(resendOTPDto.getEmail(),"Reset your FCHARITY password");
        return ResponseEntity.ok(true);
    }
    @PostMapping("/verify-reset-password-otp")
    public ResponseEntity<?> verifyResetPassword(@RequestBody VerifyUserDto verifyUserDto) {
        User u = userService.findUserByEmail(verifyUserDto.getEmail());
        return ResponseEntity.ok(authenticationService.verifyEmail(u, verifyUserDto.getVerificationCode()));
    }
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        return ResponseEntity.ok(authenticationService.resetPassword(resetPasswordDto));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody LoginUserDto loginUserDto){
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        String refreshToken = jwtService.generateRefreshToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse(jwtToken,refreshToken);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDto verifyUserDto) {
        authenticationService.verifyUser(verifyUserDto);
        return ResponseEntity.ok(true);
    }

    @PostMapping("/resendOTP")
    public ResponseEntity<?> resendVerificationCode(@RequestBody ResendOTPDto resendOTPDto) {
            authenticationService.resendVerificationCode(resendOTPDto.getEmail(),"Verify your email address");
            return ResponseEntity.ok(true);
    }

    @PostMapping("/google-login")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        User user = authenticationService.googleLogin(token);
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        System.out.println("Refresh token: "+refreshToken);
        LoginResponse loginResponse = new LoginResponse(jwtToken,refreshToken);
        return ResponseEntity.ok(loginResponse);
    }
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Refresh token is required");
        }

        try {
            String username = jwtService.extractUsername(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(refreshToken, userDetails)) {
                String newAccessToken = jwtService.generateToken(userDetails);
                LoginResponse loginResponse = new LoginResponse(newAccessToken,refreshToken);
                return ResponseEntity.ok(loginResponse);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }
    }
}