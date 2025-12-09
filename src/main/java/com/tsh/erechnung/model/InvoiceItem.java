package com.tsh.erechnung.model;

import lombok.Data;

@Data
public class InvoiceItem {
    private String description;
    private int quantity;
    private double unitPrice;
    private String taxRate; // e.g., "19%"
}
