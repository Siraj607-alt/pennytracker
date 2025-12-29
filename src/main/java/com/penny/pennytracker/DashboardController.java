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
import org.springframework.beans.factory.annotation.Autowired;


@Controller
public class DashboardController {

    private final ExpenseRepository expenseRepo;
    private final UserRepository repo;

    public DashboardController(ExpenseRepository expenseRepo,UserRepository repo) {
        this.expenseRepo = expenseRepo;
        this.repo = repo;
    }
    @ModelAttribute("emoji")
    public EmojiUtil emojiUtil() {
        return new EmojiUtil();
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/doLogin";

        // Load user
        User user = repo.findById(userId).orElse(null);
        if (user == null) return "redirect:/doLogin";

        // Send user object to HTML (IMPORTANT)
        model.addAttribute("user", user);

        // Send email
        model.addAttribute("email", user.getEmail());

        // Send expenses if needed
        List<Expense> expenses = expenseRepo.findByUserIdOrderByDateAsc(userId);
        model.addAttribute("expenses", expenses);

        return "dashboard";
    }

    @GetMapping("/my-expenses")
    public String myExpenses(
            @RequestParam(required = false) Integer year,
            HttpSession session,
            Model model) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        // Default year = current year
        if (year == null) year = LocalDate.now().getYear();
        model.addAttribute("selectedYear", year);

        // Fetch ALL expenses in ASC order
        List<Expense> allExpenses = expenseRepo.findByUserIdOrderByDateAsc(userId);

        // Filter manually (NO lambda)
        List<Expense> expenses = new ArrayList<>();
        for (Expense e : allExpenses) {
            if (e.getDate().getYear() == year) {
                expenses.add(e);
            }
        }

        model.addAttribute("expenses", expenses);

        // Build list of years 2000 → current year
        List<Integer> years = new ArrayList<>();
        for (int y = 2000; y <= LocalDate.now().getYear(); y++) {
            years.add(y);
        }
        model.addAttribute("years", years);

        // ---------------------------
        // TREND LOGIC FOR THIS YEAR
        // ---------------------------

        Map<String, String> trendMap = new HashMap<>();

        // Last days inside the selected year
        LocalDate yearEnd = LocalDate.of(year, 12, 31);
        LocalDate last30 = yearEnd.minusDays(30);
        LocalDate prev30 = yearEnd.minusDays(60);

        Map<String, Double> recent = new HashMap<>();
        Map<String, Double> previous = new HashMap<>();

        // Categorize expenses
        for (Expense e : expenses) {
            LocalDate d = e.getDate();
            String cat = e.getCategory();
            double amt = e.getAmount();

            if (d.isAfter(last30)) {
                recent.put(cat, recent.getOrDefault(cat, 0.0) + amt);
            } else if (d.isAfter(prev30)) {
                previous.put(cat, previous.getOrDefault(cat, 0.0) + amt);
            }
        }

        // Build trend
        for (String cat : recent.keySet()) {
            double r = recent.getOrDefault(cat, 0.0);
            double p = previous.getOrDefault(cat, 0.0);

            if (p == 0 && r == 0) {
                trendMap.put(cat, "No previous data");
            } else if (p == 0) {
                trendMap.put(cat, "↑ Increasing");
            } else if (r > p) {
                trendMap.put(cat, "↑ Increasing");
            } else if (r < p) {
                trendMap.put(cat, "↓ Decreasing");
            } else {
                trendMap.put(cat, "→ Stable");
            }
        }

        model.addAttribute("trendMap", trendMap);
        // Build latestId map → ensures trend only shows once per category
        Map<String, Long> latestId = new HashMap<>();
        for (Expense e : expenses) {
            latestId.put(e.getCategory(), e.getId());
        }
        model.addAttribute("latestId", latestId);


        return "my-expenses";
    }

    @GetMapping("/api/monthly-expenses")
    @ResponseBody
    public Map<String, Double> getMonthlyExpenses(
            @RequestParam int year,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Map.of(); // empty
        }

        List<Object[]> result = expenseRepo.sumByMonthForYear(userId, year);
        System.out.println("MONTHLY QUERY RESULT = ");
        for (Object[] row : result) {
            System.out.println("Month = " + row[0] + " | Total = " + row[1]);
        }


        Map<String, Double> data = new LinkedHashMap<>();
        String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

        // initialize all months to 0
        for (String m : months) data.put(m, 0.0);

        // fill real values
        for (Object[] row : result) {
            int monthNumber = ((Number) row[0]).intValue();
            double total = ((Number) row[1]).doubleValue();
            data.put(months[monthNumber - 1], total);
        }

        return data;
    }
    @GetMapping("/api/expense-data")
    @ResponseBody
    public Map<String, Object> expenseData(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        Map<String, Object> map = new HashMap<>();

        List<Expense> expenses = expenseRepo.findByUserIdOrderByDateAsc(userId);

        // datewise
        Map<String, Double> dateWise = new LinkedHashMap<>();
        for (Expense e : expenses) {
            dateWise.merge(e.getDate().toString(), e.getAmount(), Double::sum);
        }

        // category wise
        Map<String, Double> categoryWise = new HashMap<>();
        for (Expense e : expenses) {
            categoryWise.merge(e.getCategory(), e.getAmount(), Double::sum);
        }

        map.put("dateWise", dateWise);
        map.put("categoryWise", categoryWise);

        return map;
    }
    @GetMapping("/api/year-summary")
    @ResponseBody
    public Map<String, Object> getYearSummary(
            @RequestParam int year,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Map.of();

        // Fetch all expenses of selected year
        List<Expense> expenses = expenseRepo.findByUserIdOrderByDateAsc(userId);

        double total = 0;
        Map<String, Double> categoryTotals = new HashMap<>();
        Map<Integer, Double> monthTotals = new HashMap<>();

        for (Expense e : expenses) {
            if (e.getDate().getYear() == year) {
                total += e.getAmount();

                // category totals
                categoryTotals.merge(e.getCategory(), e.getAmount(), Double::sum);

                // monthly totals
                int m = e.getDate().getMonthValue();
                monthTotals.merge(m, e.getAmount(), Double::sum);
            }
        }

        // determine highest category
        String topCategory = categoryTotals.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("None");

        // determine highest month
        int topMonth = monthTotals.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(0);

        Map<String, Object> summary = new HashMap<>();
        summary.put("total", total);
        summary.put("topCategory", topCategory);
        summary.put("topMonth", topMonth);

        return summary;
    }
    @GetMapping("/api/expenses-by-year")
    @ResponseBody
    public List<Expense> expensesByYear(@RequestParam int year, HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return List.of();

        return expenseRepo.findByUserIdOrderByDateAsc(userId)
                .stream()
                .filter(e -> e.getDate().getYear() == year)
                .toList();
    }
    @GetMapping("/api/expense-data-year")
    @ResponseBody
    public Map<String, Double> getExpenseDataByYear(
            @RequestParam int year,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Map.of();

        List<Expense> expenses = expenseRepo.findByUserIdOrderByDateAsc(userId)
                .stream()
                .filter(e -> e.getDate().getYear() == year)
                .toList();

        Map<String, Double> categoryWise = new HashMap<>();

        for (Expense e : expenses) {
            categoryWise.merge(e.getCategory(), e.getAmount(), Double::sum);
        }

        return categoryWise;
    }
    @GetMapping("/api/last-expense")
    @ResponseBody
    public Map<String, Object> lastExpense(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        var exp = expenseRepo.findTopByUserIdOrderByDateDesc(userId);
        if (exp == null) return Map.of();

        return Map.of(
                "amount", exp.getAmount(),
                "category", exp.getCategory()
        );
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();  // Remove user session
        return "redirect:/doLogin";  // Send user back to login
    }
    @PostMapping("/wallet/add")
    public String addMoney(@RequestParam double amount,
                           HttpSession session,
                           RedirectAttributes r) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        User user = repo.findById(userId).orElse(null);
        if (user == null) return "redirect:/login";

        user.setWalletBalance(user.getWalletBalance() + amount);
        repo.save(user);

        r.addFlashAttribute("walletUpdated", true);
        return "redirect:/dashboard";
    }
    @GetMapping("/api/monthly-summary")
    @ResponseBody
    public Map<String, Object> getMonthlySummary(HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        try {
            Long userId = (Long) session.getAttribute("userId");

            // 🔐 Session safety
            if (userId == null) {
                response.put("month", LocalDate.now().getMonth().toString());
                response.put("total", 0);
                return response;
            }

            LocalDate now = LocalDate.now();
            int month = now.getMonthValue();
            int year = now.getYear();

            Double total = expenseRepo.getMonthlyTotal(userId, month, year);

            response.put("month", now.getMonth().toString());
            response.put("total", total != null ? total : 0);

            return response;

        } catch (Exception e) {
            // 🛑 Prevent WhiteLabel forever
            response.put("month", LocalDate.now().getMonth().toString());
            response.put("total", 0);
            return response;
        }
    }






}

