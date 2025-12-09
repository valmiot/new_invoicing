package com.tsh.erechnung.repository;

import com.tsh.erechnung.model.Invoice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends MongoRepository<Invoice, String> {
    List<Invoice> findByUserIdOrderByCreatedAtDesc(String userId);
    Optional<Invoice> findByUserIdAndInvoiceNumber(String userId, String invoiceNumber);

    @Query(value = "{ 'userId' : ?0 }", sort = "{ 'createdAt': -1 }")
    List<Invoice> findLatestByUser(String userId, Pageable p);
}
