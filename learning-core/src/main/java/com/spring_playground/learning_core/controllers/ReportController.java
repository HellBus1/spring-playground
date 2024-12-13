package com.spring_playground.learning_core.controllers;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

@RestController
public class ReportController {

    @GetMapping("/generate-pdf")
    public void generatePdf(HttpServletResponse response) throws Exception {
        // Start timing
        long startTime = System.nanoTime();

        try {
            // Simulate large memory usage
            List<Map<String, Object>> dataList = new ArrayList<>();
            for (int i = 0; i < 10000; i++) {
                Map<String, Object> data = new HashMap<>();
                data.put("id", i); // Match fields from the JRXML report
                data.put("name", "Name " + i);
                data.put("email", "email" + i + "@example.com");
                data.put("createdDate", new Date()); // Current date
                data.put("amount", Math.random() * 1000); // Random amount
                data.put("status", (i % 2 == 0) ? "Active" : "Inactive");
                dataList.add(data);
            }

            // Use JasperReports to generate a PDF
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dataList);

            // Compile the JasperReport template
            JasperReport jasperReport = JasperCompileManager.compileReport(
                    getClass().getResourceAsStream("/report_template.jrxml"));

            // Fill the report with data
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), dataSource);

            // Set response headers for PDF
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=report.pdf");

            // Stream the generated PDF to the response output stream
            OutputStream out = response.getOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, out);

        } finally {
            // End timing and log the duration
            long endTime = System.nanoTime();
            long executionTimeMillis = (endTime - startTime) / 1_000_000; // Convert to milliseconds
            System.out.println("Execution time: " + executionTimeMillis + " ms");
        }
    }
}
