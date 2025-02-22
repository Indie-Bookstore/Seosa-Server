package com.seosa.seosa.domain.auth.local.Controller;

import com.seosa.seosa.domain.auth.local.Service.SignupService;
import com.seosa.seosa.domain.auth.local.dto.SignupDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ResponseBody
public class SignupController {

    private final SignupService signupService;

    public SignupController(SignupService signupService) {
        this.signupService = signupService;
    }

    @PostMapping("/signup")
    public String signup(@RequestBody SignupDTO signupDTO) {
        if (signupDTO.getEmail() == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        signupService.signupProcess(signupDTO);

        return "success";
    }
}
