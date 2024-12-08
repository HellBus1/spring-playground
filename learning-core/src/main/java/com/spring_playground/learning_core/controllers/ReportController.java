package com.spring_playground.learning_core.controllers;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

@RestController
public class ReportController {

    @GetMapping("/generate-pdf")
    public void generatePdf(HttpServletResponse response) throws Exception {
        // Simulate large memory usage
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            Map<String, Object> data = new HashMap<>();
            data.put("field", "value" + i);
            dataList.add(data);
        }

        // Use JasperReports to generate a PDF
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dataList);

        // Create a basic JasperReport template (minimal setup)
        JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("/report_template.jrxml"));
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), dataSource);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=report.pdf");
        OutputStream out = response.getOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, out);
    }
}
