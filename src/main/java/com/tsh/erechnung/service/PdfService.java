package com.tsh.erechnung.service;

import com.tsh.erechnung.model.Invoice;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class PdfService {

    private final SpringTemplateEngine templateEngine;
    private final Path pdfDir;

    public PdfService(SpringTemplateEngine templateEngine,
                      @Value("${app.files.pdf-dir}") String pdfDirStr) throws IOException {
        this.templateEngine = templateEngine;
        this.pdfDir = Paths.get(pdfDirStr);
        Files.createDirectories(this.pdfDir);
    }

    public Path generatePdf(Invoice invoice) throws IOException {
        Context ctx = new Context();
        ctx.setVariable("invoice", invoice);

        String html = templateEngine.process("invoice", ctx);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.withHtmlContent(html, null);
        builder.toStream(baos);
        try {
            builder.run();
        } catch (Exception e) {
            throw new IOException("PDF generation failed", e);
        }

        String fname = "invoice_" + invoice.getInvoiceNumber() + "_" + invoice.getId() + ".pdf";
        Path out = pdfDir.resolve(fname);
        try (OutputStream os = Files.newOutputStream(out)) {
            baos.writeTo(os);
        }
        return out;
    }
}
