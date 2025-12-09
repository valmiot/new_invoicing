package com.tsh.erechnung.controller;

import com.tsh.erechnung.model.Invoice;
import com.tsh.erechnung.service.InvoiceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<?> createInvoice(Authentication auth, @Valid @RequestBody Invoice invoice) throws Exception {
        String userId = (String) auth.getPrincipal();
        Invoice created = invoiceService.createInvoice(userId, invoice);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Invoice>> listInvoices(Authentication auth) {
        String userId = (String) auth.getPrincipal();
        return ResponseEntity.ok(invoiceService.listForUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoice(Authentication auth, @PathVariable String id) {
        String userId = (String) auth.getPrincipal();
        return ResponseEntity.ok(invoiceService.getForUser(userId, id));
    }

    @GetMapping("/{id}/download-pdf")
    public ResponseEntity<?> downloadPdf(Authentication auth, @PathVariable String id) throws MalformedURLException {
        String userId = (String) auth.getPrincipal();
        Invoice inv = invoiceService.getForUser(userId, id);
        if (inv.getPdfPath() == null) return ResponseEntity.notFound().build();
        Path p = Paths.get(inv.getPdfPath());
        UrlResource res = new UrlResource(p.toUri());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"invoice_" + inv.getInvoiceNumber() + ".pdf\"")
                .body(res);
    }

    @GetMapping("/{id}/download-xml")
    public ResponseEntity<?> downloadXml(Authentication auth, @PathVariable String id) throws MalformedURLException {
        String userId = (String) auth.getPrincipal();
        Invoice inv = invoiceService.getForUser(userId, id);
        if (inv.getXmlPath() == null) return ResponseEntity.notFound().build();
        Path p = Paths.get(inv.getXmlPath());
        UrlResource res = new UrlResource(p.toUri());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"invoice_" + inv.getInvoiceNumber() + ".xml\"")
                .body(res);
    }
}
