package com.penny.pennytracker.controller;

import com.penny.pennytracker.User;
import com.penny.pennytracker.UserRepository;
import com.penny.pennytracker.service.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Random;

@Controller
public class ForgotPasswordController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private EmailService emailService;

    /* ========== PAGE LOADS ========== */

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    @GetMapping("/verify-otp")
    public String verifyOtpPage() {
        return "verify-otp";
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(HttpSession session, RedirectAttributes redirect) {

        if (session.getAttribute("resetEmail") == null) {
            redirect.addFlashAttribute("error", "Session expired. Try again.");
            return "redirect:/login";
        }

        return "reset-password";
    }

    /* ========== ACTIONS ========== */

    @PostMapping("/forgot-password/send-otp")
    public String sendOtp(@RequestParam String email,
                          HttpSession session,
                          RedirectAttributes redirect) {

        User user = userRepo.findByEmailIgnoreCase(email);
        if (user == null) {
            redirect.addFlashAttribute("error", "Email not registered");
            return "redirect:/forgot-password";
        }

        int otp = new Random().nextInt(900000) + 100000;

        session.setAttribute("resetEmail", email);
        session.setAttribute("resetOtp", otp);

        emailService.sendOtpEmail(email, otp);

        return "redirect:/verify-otp";
    }
    @PostMapping("/forgot-password/verify-otp")
    public String verifyOtp(@RequestParam int otp,
                            HttpSession session,
                            RedirectAttributes redirect) {

        Integer savedOtp = (Integer) session.getAttribute("resetOtp");

        if (savedOtp == null || otp != savedOtp) {
            redirect.addFlashAttribute(
                    "error",
                    "Invalid verification code. Please try again."
            );
            return "redirect:/verify-otp";
        }

        return "redirect:/reset-password";
    }


    @PostMapping("/forgot-password/reset")
    public String resetPassword(@RequestParam String newPassword,
                                HttpSession session,
                                RedirectAttributes redirect) {

        String email = (String) session.getAttribute("resetEmail");
        if (email == null) {
            redirect.addFlashAttribute("error", "Session expired");
            return "redirect:/login";
        }

        User user = userRepo.findByEmailIgnoreCase(email);
        user.setPassword(newPassword); // (hash later)
        userRepo.save(user);

        session.invalidate();

        redirect.addFlashAttribute("success", "Password reset successful");
        return "redirect:/reset-password";

    }
    @GetMapping("/login")
    public String loginPage() {
        return "form"; // login.html
    }

}
