package com.tsh.erechnung.service;

import com.tsh.erechnung.model.Invoice;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class XmlService {
    private final Path xmlDir;

    public XmlService(@Value("${app.files.xml-dir}") String xmlDirStr) throws Exception {
        this.xmlDir = Paths.get(xmlDirStr);
        Files.createDirectories(this.xmlDir);
    }

    public Path generateXml(Invoice invoice) throws Exception {
        // Simple JAXB marshalling of a minimal wrapper. For XRechnung, generate
        // proper JAXB-annotated classes conforming to the XSD (this is minimal).
        Path out = xmlDir.resolve("invoice_" + invoice.getInvoiceNumber() + "_" + invoice.getId() + ".xml");

        // For simplicity build a minimal XML structure via JAXB on a small wrapper:
        InvoiceXmlWrapper wrapper = new InvoiceXmlWrapper(invoice);

        JAXBContext ctx = JAXBContext.newInstance(InvoiceXmlWrapper.class);
        Marshaller m = ctx.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        try (OutputStream os = Files.newOutputStream(out)) {
            m.marshal(wrapper, os);
        }
        return out;
    }

    // Minimal wrapper class for JAXB output (declared as static inner class)
    @jakarta.xml.bind.annotation.XmlRootElement(name = "Invoice")
    public static class InvoiceXmlWrapper {
        private Invoice invoice;
        public InvoiceXmlWrapper() {}
        public InvoiceXmlWrapper(Invoice invoice) { this.invoice = invoice; }

        @jakarta.xml.bind.annotation.XmlElement(name = "InvoiceNumber")
        public String getInvoiceNumber() { return invoice.getInvoiceNumber(); }
        @jakarta.xml.bind.annotation.XmlElement(name = "InvoiceDate")
        public String getInvoiceDate() { return invoice.getInvoiceDate().toString(); }
        @jakarta.xml.bind.annotation.XmlElement(name = "Total")
        public double getTotal() { return invoice.getTotalAmount(); }
    }
}
