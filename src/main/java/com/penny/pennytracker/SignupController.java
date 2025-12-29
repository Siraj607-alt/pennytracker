package com.penny.pennytracker;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;


@Controller
public class SignupController {

    private final UserRepository repo;
    @Autowired
    public SignupController(UserRepository repo) {
        this.repo = repo;
    }
    @GetMapping("/signup")
    public String showSignupPage() {
        return "signup";   // signup.html
    }
    @GetMapping("/doLogin")
    public String showLogin() {
        return "form";   // loads form.html
    }
    @PostMapping("/doLogin")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        User user = repo.findByEmailIgnoreCase(email);


        // USER NOT FOUND
        if (user == null) {
            model.addAttribute("loginError", "No account found. Please sign up first.");
            return "form";   // Reload login page with the error
        }

        // WRONG PASSWORD
        if (!user.getPassword().equals(password)) {
            model.addAttribute("loginError", "Email and password did not match.");
            return "form";
        }

        // SUCCESS
        session.setAttribute("userId", user.getId());
        session.setAttribute("email", user.getEmail());

        return "redirect:/dashboard";
    }
    @PostMapping("/signup")
    public String signup(@RequestParam String email,
                         @RequestParam String password,
                         Model model) {

        User existing = repo.findByEmail(email);

        if (existing != null) {
            model.addAttribute("signupExists", true);
            return "signup";
        }

        if (email == null || email.isBlank()) {
            model.addAttribute("signupError", "Email cannot be empty");
            return "signup";
        }

        if (password == null || password.isBlank()) {
            model.addAttribute("signupError", "Password cannot be empty");
            return "signup";
        }

        User user = new User(email, password);
        repo.save(user);

        model.addAttribute("signupSuccess", true);
        return "signup";
    }


    @PostMapping("/save-profile")
    public String saveProfile(
            HttpSession session,
            @RequestParam String profession,
            @RequestParam Integer monthlyIncome,
            @RequestParam String goal
    ) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        User user = repo.findById(userId).orElse(null);
        if (user == null) return "redirect:/login";

        user.setProfession(profession);
        user.setMonthlyIncome(monthlyIncome);
        user.setGoal(goal);

        repo.save(user);

        return "redirect:/profile";
    }


}
