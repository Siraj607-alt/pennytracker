package com.penny.pennytracker;

import java.util.List;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.penny.pennytracker.EmojiUtil;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;
import java.util.LinkedHashMap;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class ProfileController {

    private final UserRepository userRepo;
    private final ExpenseRepository expenseRepo;
    private final SavingsGoalRepository goalRepo;

    public ProfileController(UserRepository userRepo,
                             ExpenseRepository expenseRepo,
                             SavingsGoalRepository goalRepo) {
        this.userRepo = userRepo;
        this.expenseRepo = expenseRepo;
        this.goalRepo = goalRepo;
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/doLogin";

        User user = userRepo.findById(userId).orElse(null);
        if (user == null) return "redirect:/doLogin";

        // 🔹 Wallet
        model.addAttribute("walletBalance", user.getWalletBalance());

        // 🔹 Total expenses
        double totalSpend = expenseRepo.findByUserId(userId)
                .stream()
                .mapToDouble(Expense::getAmount)
                .sum();

        // 🔹 Active goals
        long activeGoals = goalRepo.findByUser(user)
                .stream()
                .filter(g -> g.getSavedAmount() < g.getTargetAmount())
                .count();

        model.addAttribute("user", user);
        model.addAttribute("totalSpend", totalSpend);
        model.addAttribute("activeGoals", activeGoals);

        return "profile";
    }
    @GetMapping("/profile/edit")
    public String editProfile(HttpSession session, Model model) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/doLogin";

        User user = userRepo.findById(userId).orElse(null);
        if (user == null) return "redirect:/doLogin";

        model.addAttribute("user", user);
        model.addAttribute("editMode", true);

        return "profile";
    }
    @PostMapping("/profile/update")
    public String updateProfile(
            @RequestParam String profession,
            @RequestParam Integer monthlyIncome,
            @RequestParam String goal,
            HttpSession session,
            RedirectAttributes redirect) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/doLogin";

        User user = userRepo.findById(userId).orElse(null);
        if (user == null) return "redirect:/doLogin";

        user.setProfession(profession);
        user.setMonthlyIncome(monthlyIncome);
        user.setGoal(goal);

        userRepo.save(user);

        // Optional: update session if needed
        session.setAttribute("user", user);

        redirect.addFlashAttribute("success",
                "Profile updated successfully");

        return "redirect:/profile";
    }


}
