package com.quitsmoking.platform.api;



import com.quitsmoking.platform.dto.*;
import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.ForgotPassword;
import com.quitsmoking.platform.repository.AuthenticationRepository;
import com.quitsmoking.platform.repository.ForgotPasswordRepository;
import com.quitsmoking.platform.service.AuthenticationService;
import com.quitsmoking.platform.service.EmailService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;

@RestController
@RequestMapping("api")
@SecurityRequirement(name ="api")
@CrossOrigin("*")
public class AuthenticationAPI {
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ForgotPasswordRepository forgotPasswordRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @PostMapping("/register")
    public ResponseEntity<Account> register(@RequestBody @Valid RegisterRequest registerRequest){
        Account newAccount = authenticationService.register(registerRequest);
        return ResponseEntity.ok(newAccount);
    }

    @PostMapping("/login")
    public ResponseEntity<UserAccountResponse> login(@RequestBody @Valid LoginRequest loginRequest){
        UserAccountResponse account = authenticationService.login(loginRequest);
        return ResponseEntity.ok(account);
    }

    @PostMapping("/verifyMail/{email}")
    public ResponseEntity<String> verifyEmail(@PathVariable String email){
        Account account = AuthenticationRepository.findByEmail().orElseThro(()-> new AccountNotFoundException("khong tim thay"))
        int otp = otpGenerator();
        MailBody mailBody = MailBody.builder()
                .to(email)
                .text("this is your otp forgot password request: "+otp)
                .subject("OTP for Forgot Password")
                .build();

        ForgotPassword fp = ForgotPassword.builder()
                .otp(otp)
                .expirationTime(new Date(System.currentTimeMillis()*70 *1000))
                .account(account)
                .build();

        emailService.sendSimpleMessage(mailBody);
        forgotPasswordRepository.save(fp);
        return ResponseEntity.ok("OTP sent successfully");
    }

    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<String> verifyOtp(@PathVariable Integer otp,@PathVariable String email){
        Account account = AuthenticationRepository.findByEmail()
                .orElseThrow(()-> new AccountNotFoundException("khong tim thay"));
        ForgotPassword fp = forgotPasswordRepository.findNyOtpAndAccount(otp, account)
                .orElseThrow(()-> new RuntimeException("Invalid OTP for email"+email));
        if (fp.getExpirationTime().before(Date.from(Instant.now()))) {
            forgotPasswordRepository.deteteById(fp.getFpid()) ;
            return new ResponseEntitY<>("OTP has expired! ", HttpStatus.EXPECTATION_FAILED);

        }
        return ResponseEntity.ok( "0TP verified!");
        }
    }

@PostMapping("/changePassword/{email}")
public ResponseEntity<String> changePasswordHandler(@RequestBody ChangePassword changePassword, @PathVariable String email) {
    if (!Objects.equats(changePassword.password(), changepassword.repeatpassword())) {
        return new ResponseEntitY<>("please enter password again ", HttpStatus.EXPECTATION_FAILED))
}
    String encodedPassword = PasswordEncoder.encode(changePassword.password());
    AuthenticationRepository.updatePassword(email, encodedPassword);
    return ResponseEntity.ok("Password changed successfully");

}


    private Integer otpGenerator(){
       Romdom ramdom = new Ramdom();
       return ramdom.nextInt(100_000,999_999);
    }



}
