package com.booking.repository;

import com.booking.dataModel.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    Payment findByTransactionId(String transactionId);

    @Query("select p from Payment p join fetch p.booking where p.transactionId = :transactionId")
    Payment findPaymentWithBooking(@Param("transactionId") String transactionId);
}
