package com.supplychain.ai.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.supplychain.ai.dto.AnalyticsSummaryDto;
import com.supplychain.ai.model.SupplyChainData;
import com.supplychain.ai.repository.SupplyChainDataRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final SupplyChainDataRepository dataRepository;
    private final AnalyticsService          analyticsService;

    public ReportService(SupplyChainDataRepository dataRepository, AnalyticsService analyticsService) {
        this.dataRepository  = dataRepository;
        this.analyticsService = analyticsService;
    }

    // ---------------------------------------------------------------
    //  PDF Report
    // ---------------------------------------------------------------
    public ByteArrayInputStream generatePdfReport() throws IOException {
        AnalyticsSummaryDto summary = analyticsService.getSummary();
        ByteArrayOutputStream out   = new ByteArrayOutputStream();

        PdfWriter   writer   = new PdfWriter(out);
        PdfDocument pdf      = new PdfDocument(writer);
        Document    document = new Document(pdf);

        DeviceRgb primary = new DeviceRgb(78, 115, 223);
        DeviceRgb dark    = new DeviceRgb(30, 41, 59);

        // Title
        document.add(new Paragraph("Supply Chain AI Agent")
                .setFontSize(26).setBold().setFontColor(primary).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("Executive Summary Report")
                .setFontSize(14).setFontColor(dark).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("Generated: " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm")))
                .setFontSize(10).setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER).setMarginBottom(20));

        // KPI Table (iText cells only – no POI Cell)
        document.add(new Paragraph("Key Performance Indicators")
                .setFontSize(14).setBold().setFontColor(dark).setMarginTop(10).setMarginBottom(8));

        Table kpiTable = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth();
        addPdfKpiCell(kpiTable, "Total Orders",         String.valueOf(summary.getTotalOrders()),                   primary);
        addPdfKpiCell(kpiTable, "Total Revenue",        String.format("$%,.2f", summary.getTotalSales()),           primary);
        addPdfKpiCell(kpiTable, "Net Profit",           String.format("$%,.2f", summary.getTotalProfit()),          primary);
        addPdfKpiCell(kpiTable, "Profit Margin",        String.format("%.2f%%", summary.getProfitMargin()),         primary);
        addPdfKpiCell(kpiTable, "Average Order Value",  String.format("$%,.2f", summary.getAvgSales()),             primary);
        addPdfKpiCell(kpiTable, "Total Units Sold",     String.valueOf(summary.getTotalQuantity()),                 primary);
        document.add(kpiTable);

        // Sales by Region
        if (summary.getSalesByRegion() != null && !summary.getSalesByRegion().isEmpty()) {
            document.add(new Paragraph("Sales by Region")
                    .setFontSize(14).setBold().setFontColor(dark).setMarginTop(16).setMarginBottom(8));
            Table regionTable = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth();
            regionTable.addHeaderCell(makePdfHeaderCell("Region",  primary));
            regionTable.addHeaderCell(makePdfHeaderCell("Sales",   primary));
            for (Map.Entry<String, Double> e : summary.getSalesByRegion().entrySet()) {
                regionTable.addCell(new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph(e.getKey())));
                regionTable.addCell(new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph(String.format("$%,.2f", e.getValue()))));
            }
            document.add(regionTable);
        }

        // Sales by Category
        if (summary.getSalesByCategory() != null && !summary.getSalesByCategory().isEmpty()) {
            document.add(new Paragraph("Sales by Category")
                    .setFontSize(14).setBold().setFontColor(dark).setMarginTop(16).setMarginBottom(8));
            Table catTable = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth();
            catTable.addHeaderCell(makePdfHeaderCell("Category", primary));
            catTable.addHeaderCell(makePdfHeaderCell("Sales",    primary));
            for (Map.Entry<String, Double> e : summary.getSalesByCategory().entrySet()) {
                catTable.addCell(new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph(e.getKey())));
                catTable.addCell(new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph(String.format("$%,.2f", e.getValue()))));
            }
            document.add(catTable);
        }

        document.add(new Paragraph(
                "\nThis report was automatically generated by Supply Chain AI Agent v1.0")
                .setFontSize(9).setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER).setMarginTop(24));

        document.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addPdfKpiCell(Table table, String label, String value, DeviceRgb accent) {
        com.itextpdf.layout.element.Cell cell = new com.itextpdf.layout.element.Cell();
        cell.add(new Paragraph(label).setFontSize(9).setFontColor(ColorConstants.GRAY));
        cell.add(new Paragraph(value).setFontSize(14).setBold().setFontColor(accent));
        cell.setPadding(12);
        table.addCell(cell);
    }

    private com.itextpdf.layout.element.Cell makePdfHeaderCell(String text, DeviceRgb bg) {
        return new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(text).setBold())
                .setBackgroundColor(bg)
                .setFontColor(ColorConstants.WHITE);
    }

    // ---------------------------------------------------------------
    //  Excel Report (Apache POI only – no iText Cell)
    // ---------------------------------------------------------------
    public ByteArrayInputStream generateExcelReport() throws IOException {
        List<SupplyChainData> data = dataRepository.findAll();

        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Supply Chain Data");

            // Header style
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);

            String[] columns = {
                "Order ID", "Order Date", "Ship Date", "Ship Mode",
                "Customer ID", "Customer Name", "Segment",
                "Country", "City", "State", "Region",
                "Category", "Sub-Category", "Product Name",
                "Sales", "Quantity", "Discount", "Profit", "Shipping Cost", "Priority"
            };

            // Create header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 4500);
            }

            // Data rows
            int rowIdx = 1;
            for (SupplyChainData item : data) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(nvl(item.getOrderId()));
                row.createCell(1).setCellValue(item.getOrderDate()  != null ? item.getOrderDate().toString()  : "");
                row.createCell(2).setCellValue(item.getShipDate()   != null ? item.getShipDate().toString()   : "");
                row.createCell(3).setCellValue(nvl(item.getShipMode()));
                row.createCell(4).setCellValue(nvl(item.getCustomerId()));
                row.createCell(5).setCellValue(nvl(item.getCustomerName()));
                row.createCell(6).setCellValue(nvl(item.getSegment()));
                row.createCell(7).setCellValue(nvl(item.getCountry()));
                row.createCell(8).setCellValue(nvl(item.getCity()));
                row.createCell(9).setCellValue(nvl(item.getState()));
                row.createCell(10).setCellValue(nvl(item.getRegion()));
                row.createCell(11).setCellValue(nvl(item.getCategory()));
                row.createCell(12).setCellValue(nvl(item.getSubCategory()));
                row.createCell(13).setCellValue(nvl(item.getProductName()));
                row.createCell(14).setCellValue(item.getSales()        != null ? item.getSales()        : 0.0);
                row.createCell(15).setCellValue(item.getQuantity()     != null ? item.getQuantity()     : 0);
                row.createCell(16).setCellValue(item.getDiscount()     != null ? item.getDiscount()     : 0.0);
                row.createCell(17).setCellValue(item.getProfit()       != null ? item.getProfit()       : 0.0);
                row.createCell(18).setCellValue(item.getShippingCost() != null ? item.getShippingCost() : 0.0);
                row.createCell(19).setCellValue(nvl(item.getOrderPriority()));
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    private String nvl(String s) { return s != null ? s : ""; }
}
