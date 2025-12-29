package com.penny.pennytracker;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
@Controller
public class FormController {
    private final UserRepository userRepository;

    @Autowired
    public FormController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @GetMapping("/savemyinfo")
    public String showForm() {
        // return the template name you actually have under templates/
        return "form";               // -> src/main/resources/templates/form.html
    }
    @PostMapping("/submitinfo")
    @ResponseBody
    public String saveInfo(@RequestParam String email,
                           @RequestParam String password
    ) {
        if (this.userRepository == null) {
            return "ERROR: userRepository is NULL (not injected)";
        }

        // Use 'email' variable (mapped from form name="gmail")
        User user = new User(email,password);
        this.userRepository.save(user);

        return "Saved into database successfully";
        // Use 'email' variable (mapped from form name="gmail")


    }
}
