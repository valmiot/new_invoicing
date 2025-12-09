package com.tsh.erechnung.service;

import com.tsh.erechnung.model.Invoice;
import com.tsh.erechnung.model.InvoiceItem;
import com.tsh.erechnung.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private XmlService xmlService;

    public Invoice createInvoice(String userId, Invoice invoice) throws Exception {
        invoice.setUserId(userId);
        invoice.setInvoiceDate(invoice.getInvoiceDate() == null ? LocalDate.now() : invoice.getInvoiceDate());
        invoice.setCreatedAt(Instant.now());
        invoice.setUpdatedAt(Instant.now());
        // generate invoiceNumber (YYYY-UUID short)
        String num = invoice.getInvoiceDate().getYear() + "-" + UUID.randomUUID().toString().substring(0,8);
        invoice.setInvoiceNumber(num);

        calculateTotals(invoice);

        Invoice saved = invoiceRepository.save(invoice);

        // generate files
        var pdfPath = pdfService.generatePdf(saved);
        saved.setPdfPath(pdfPath.toString());
        var xmlPath = xmlService.generateXml(saved);
        saved.setXmlPath(xmlPath.toString());

        saved = invoiceRepository.save(saved);
        return saved;
    }

    public List<Invoice> listForUser(String userId) {
        return invoiceRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Invoice getForUser(String userId, String id) {
        Invoice inv = invoiceRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invoice not found"));
        if (!userId.equals(inv.getUserId())) throw new SecurityException("Forbidden");
        return inv;
    }

    private void calculateTotals(Invoice invoice) {
        double net = 0.0;
        double tax = 0.0;
        for (InvoiceItem it : invoice.getItems()) {
            double subtotal = it.getQuantity() * it.getUnitPrice();
            net += subtotal;
            // Basic tax parsing e.g. "19%" -> 0.19
            try {
                String s = it.getTaxRate().replace("%", "").trim();
                double rate = Double.parseDouble(s) / 100.0;
                tax += subtotal * rate;
            } catch (Exception e) {
                // ignore invalid, treat as 0
            }
        }
        invoice.setNetAmount(net);
        invoice.setTaxAmount(tax);
        invoice.setTotalAmount(net + tax);
    }
}
