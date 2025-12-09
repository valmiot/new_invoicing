package com.tsh.erechnung.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
@Document(collection = "invoices")
@CompoundIndex(name = "user_invoice_idx", def = "{'userId': 1, 'invoiceNumber': 1}", unique = true)
public class Invoice {
    @Id
    private String id;

    private String userId;
    private String invoiceNumber;
    private String subject; // short title
    private LocalDate invoiceDate;
    private List<InvoiceItem> items;

    private double netAmount;
    private double taxAmount;
    private double totalAmount;

    private String pdfPath;
    private String xmlPath;

    private Instant createdAt;
    private Instant updatedAt;
}
