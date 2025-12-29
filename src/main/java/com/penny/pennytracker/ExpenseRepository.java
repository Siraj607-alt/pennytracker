package com.penny.pennytracker;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // Fetch expenses belonging to a specific user
    List<Expense> findByUserId(Long userId);
    List<Expense> findByUserIdOrderByDateAsc(Long userId);
    Expense findTopByUserIdOrderByDateDesc(Long userId);

    @Query("""
       SELECT FUNCTION('month', e.date), SUM(e.amount)
       FROM Expense e
       WHERE e.user.id = :uid AND FUNCTION('year', e.date) = :year
       GROUP BY FUNCTION('month', e.date)
       ORDER BY FUNCTION('month', e.date)
       """)
    List<Object[]> sumByMonthForYear(@Param("uid") Long userId,
                                     @Param("year") int year);
    @Query("""
        SELECT COALESCE(SUM(e.amount), 0)
        FROM Expense e
        WHERE e.user.id = :userId
          AND MONTH(e.date) = :month
          AND YEAR(e.date) = :year
    """)
    Double getMonthlyTotal(@Param("userId") Long userId,
                           @Param("month") int month,
                           @Param("year") int year);
}




    // Optional filters for dashboard or reports


