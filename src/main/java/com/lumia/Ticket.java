package com.lumia;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Array;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Ticket {
    public ArrayList<String> articlesSumUp = new ArrayList<String>();
    public double totalPrice;

    public Ticket(List<Article> articles, double totalPrice) {
        this.totalPrice = totalPrice;

        for (Article article : articles) {
            articlesSumUp.add(String.valueOf(article.getAmount()) + "x " + String.valueOf(article.getName()));
            articlesSumUp.add(String.valueOf(article.getTotalPrice()) + "€ (u. " + String.valueOf(article.getUnitPrice()) + ")");
        }
    }

    public boolean print() {
        String filename = "ticket";

        try (PdfWriter writer = new PdfWriter(filename + ".pdf");
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            com.itextpdf.layout.element.Paragraph title = new com.itextpdf.layout.element.Paragraph("------ Mili ------");

            LocalDate today = LocalDate.now();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            String dateString = today.format(formatter);

            com.itextpdf.layout.element.Paragraph date = new com.itextpdf.layout.element.Paragraph(dateString);
            document.add(date);
            document.add(title);

            for (String articleSummary : articlesSumUp) {
                document.add(new com.itextpdf.layout.element.Paragraph(articleSummary));
            }

            document.add(new com.itextpdf.layout.element.Paragraph("------------------"));
            document.add(new com.itextpdf.layout.element.Paragraph("Total: " + String.format("%.2f", totalPrice) + "€"));
        } catch (IOException e) {
            return false;
        }

        try {
            Process process = new ProcessBuilder("magick", "-density", "300", filename + ".pdf", filename + ".ps").start();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("########## Printing with convert instead ##########");
            try {
                Process process = new ProcessBuilder("convert", "-density", "300", filename + ".pdf", filename + ".ps").start();
                process.waitFor();
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }

        try {
            Process process = new ProcessBuilder("lpr", "-P", "POS58", filename + ".ps").start();
            process.waitFor();
        } catch (Exception e) {
            System.out.println("Printer error");
            return false;
        }

        File pdfFile = new File(filename+ ".pdf");
        File psFile = new File(filename+ ".ps");
        pdfFile.delete();
        psFile.delete();

        return true;
    }
}
