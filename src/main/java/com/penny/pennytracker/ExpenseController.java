package com.penny.pennytracker;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



@Controller
public class ExpenseController {

    private final ExpenseRepository expenseRepo;
    private final UserRepository userRepo;

    public ExpenseController(ExpenseRepository expenseRepo, UserRepository userRepo) {
        this.expenseRepo = expenseRepo;
        this.userRepo = userRepo;
    }
    @PostMapping("/add-expense")
    public String addExpense(
            @RequestParam double amount,
            @RequestParam String category,
            @RequestParam String description,
            @RequestParam String date,
            HttpSession session,
            RedirectAttributes r   // ✅ IMPORTANT
    ) {

        Long userId = (Long) session.getAttribute("userId");
        User user = userRepo.findById(userId).orElse(null);

        if (user == null) {
            r.addFlashAttribute("error", "User session expired. Please login again.");
            return "redirect:/login";
        }

        // ✅ WALLET BALANCE CHECK (CRITICAL FIX)
        if (amount > user.getWalletBalance()) {
            r.addFlashAttribute(
                    "error",
                    "Wallet balance is low. Cannot add expense greater than available balance."
            );
            return "redirect:/dashboard";
        }

        // ✅ Deduct from wallet safely
        double newBalance = user.getWalletBalance() - amount;
        user.setWalletBalance(newBalance);
        userRepo.save(user);

        // ✅ Save expense
        Expense e = new Expense(
                user,
                amount,
                category,
                description,
                LocalDate.parse(date)
        );
        expenseRepo.save(e);

        r.addFlashAttribute("expenseSaved", true);

        return "redirect:/dashboard";
    }

}
