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
public class SavingsController {

    private final SavingsGoalRepository goalRepo;
    private final UserRepository userRepo;

    public SavingsController(SavingsGoalRepository goalRepo, UserRepository userRepo) {
        this.goalRepo = goalRepo;
        this.userRepo = userRepo;
    }

    @GetMapping("/savings")
    public String savings(HttpSession session, Model model) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        User user = userRepo.findById(userId).orElse(null);

        List<SavingsGoal> goals = goalRepo.findByUserId(userId);
        int activeGoalCount = 0;

        for (SavingsGoal g : goals) {
            if (g.getSavedAmount() < g.getTargetAmount()) {
                activeGoalCount++;
            }
        }

        model.addAttribute("goalCount", activeGoalCount);


        double totalSaved = goals.stream()
                .mapToDouble(SavingsGoal::getSavedAmount)
                .sum();

        model.addAttribute("goals", goals);
        model.addAttribute("totalSaved", totalSaved);
        model.addAttribute("walletBalance", user.getWalletBalance());

        return "savings";
    }
    @PostMapping("/create-goal")
    public String createGoal(@RequestParam String name,
                             @RequestParam double targetAmount,
                             HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        User user = userRepo.findById(userId).orElse(null);

        goalRepo.save(new SavingsGoal(name, targetAmount, user));

        return "redirect:/savings";
    }
    @PostMapping("/add-to-goal")
    public String addToGoal(@RequestParam Long goalId,
                            @RequestParam double amount,
                            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        User user = userRepo.findById(userId).orElse(null);

        SavingsGoal goal = goalRepo.findById(goalId).orElse(null);

        if (user.getWalletBalance() < amount) {
            return "redirect:/savings?error=lowbalance";
        }

        user.setWalletBalance(user.getWalletBalance() - amount);
        goal.setSavedAmount(goal.getSavedAmount() + amount);

        userRepo.save(user);
        goalRepo.save(goal);

        return "redirect:/savings";
    }
    @PostMapping("/savings/add-money")
    public String addMoneyToGoal(@RequestParam Long goalId,
                                 @RequestParam double amount,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        User user = userRepo.findById(userId).orElse(null);
        SavingsGoal goal = goalRepo.findById(goalId).orElse(null);

        if (user == null || goal == null) {
            redirectAttributes.addFlashAttribute("error", "Invalid request");
            return "redirect:/savings";
        }

        // 🚫 Wallet check
        if (user.getWalletBalance() < amount) {
            redirectAttributes.addFlashAttribute("error", "Insufficient wallet balance");
            return "redirect:/savings";
        }

        // 🚫 Prevent exceeding goal target
        double remaining = goal.getTargetAmount() - goal.getSavedAmount();

        if (amount > remaining) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "You can add only ₹" + remaining + " more to reach this goal"
            );
            return "redirect:/savings";
        }

        // ✅ Apply transaction
        user.setWalletBalance(user.getWalletBalance() - amount);
        goal.setSavedAmount(goal.getSavedAmount() + amount);

        userRepo.save(user);
        goalRepo.save(goal);

        redirectAttributes.addFlashAttribute("success", "Money added successfully");

        return "redirect:/savings";
    }


}
