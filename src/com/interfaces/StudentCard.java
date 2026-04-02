/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.interfaces;

import com.connection.DbConnection;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.text.Document;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author Theekshana
 */
public class StudentCard extends javax.swing.JPanel {

    Connection conn = null;
    PreparedStatement pst = null;
    Students parentPanel;

    FlatSVGIcon update_icon = new FlatSVGIcon("com/images/Update.svg", 30, 30);
    FlatSVGIcon delete_icon = new FlatSVGIcon("com/images/Delete.svg", 30, 30);
    FlatSVGIcon print_icon = new FlatSVGIcon("com/images/Print.svg", 30, 30);
    FlatSVGIcon add_icon = new FlatSVGIcon("com/images/Add.svg", 30, 30);

    public StudentCard(String student_id, String name, String birthday, String school, String nic_no, String gender, String address, String student_contact_no, String email, String parent_name, String parent_contact_no, String subject_stream, String profile_picture, byte[] qr_code, ArrayList<String> subjectList, Students aThis) {
        initComponents();

        conn = DbConnection.connect();

//        FlatSVGIcon myIcon = new FlatSVGIcon(profile_picture, 128, 110);
        if (profile_picture != null && !profile_picture.isEmpty()) {
            // String path එක ImageIcon එකක් බවට හරවනවා
            javax.swing.ImageIcon imgIcon = new javax.swing.ImageIcon(profile_picture);
            jLabel1.setIcon(imgIcon);
        } else {
            // පින්තූරයක් නැතිනම් default icon එකක් සෙට් කරන්න පුළුවන්
            jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/user-3296.svg")));
        }

// 2. Label ekata icon eka set karanna
        profile_image_path.setText(profile_picture);
        student_id_box.setText(student_id);
        student_name_box.setText(name);
        birthday_box.setText(birthday);
        school_box.setText(school);
        nic_no_box.setText(nic_no);
        gender_box.setText(gender);
        address_box.setText(address);
        student_contact_no_box.setText(student_contact_no);
        email_box.setText(email);
        parent_name_box.setText(parent_name);
        parent_contact_no_box.setText(parent_contact_no);
        subject_stream_box.setText(subject_stream);
        subjects_box.setText("<html><body style='width: 300px;'>" + String.join("<br/>", subjectList) + "</body></html>");

        if (qr_code != null) {
            ImageIcon qr_image = new ImageIcon(qr_code);
            qr_code_box.setIcon(qr_image);
        }

        this.parentPanel = aThis;

    }

    public BufferedImage generateQRCode(String text, int width, int height) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    //Print Student ID
    public void Print_Card() {
        try {
            String home = System.getProperty("user.home");
            String filePath = home + "/Downloads/Student_Card_" + nic_no_box.getText() + ".pdf";

            Rectangle pageSize = new Rectangle(242, 153);
            Document document = new Document(pageSize, 0, 0, 0, 0);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // **1. HEADER SECTION**
            PdfPTable headerTable = new PdfPTable(1);
            headerTable.setWidthPercentage(100);
            PdfPCell titleCell = new PdfPCell(new Phrase("Student ID Card",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Font.NORMAL, BaseColor.WHITE)));
            titleCell.setBackgroundColor(new BaseColor(11, 45, 114));
            titleCell.setBorder(Rectangle.NO_BORDER);
            titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            titleCell.setPadding(8);
            headerTable.addCell(titleCell);
            document.add(headerTable);

            // **2. BODY SECTION (Layout Table)**
            PdfPTable bodyTable = new PdfPTable(2);
            bodyTable.setWidthPercentage(95);
            bodyTable.setWidths(new float[]{1.2f, 2.5f});
            bodyTable.setSpacingBefore(10f);
            bodyTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            // --- Left Side: Profile Photo (Supports JPG, PNG, JPEG) ---
            try {
                String photoPath = profile_image_path.getText();

                if (photoPath != null && !photoPath.isEmpty()) {
                    // Direct file path eken image eka load kireema
                    Image profileImg = Image.getInstance(photoPath);
                    profileImg.scaleToFit(65, 75);

                    PdfPCell photoCell = new PdfPCell(profileImg, false);
                    photoCell.setBorder(Rectangle.NO_BORDER);
                    photoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    bodyTable.addCell(photoCell);
                } else {
                    throw new Exception("Path is empty");
                }
            } catch (Exception e) {
                PdfPCell empty = new PdfPCell(new Phrase("No Photo", FontFactory.getFont(FontFactory.HELVETICA, 7)));
                empty.setBorder(Rectangle.NO_BORDER);
                empty.setVerticalAlignment(Element.ALIGN_MIDDLE);
                bodyTable.addCell(empty);
            }

            // --- Right Side: QR & Details ---
            PdfPTable rightSideTable = new PdfPTable(1);

            // QR Code - High Quality (600x600)
            BufferedImage qrImage = generateQRCode(nic_no_box.getText(), 600, 600);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            javax.imageio.ImageIO.write(qrImage, "png", baos);
            Image pdfQrImage = Image.getInstance(baos.toByteArray());
            pdfQrImage.scaleToFit(40, 40);

            PdfPCell qrCell = new PdfPCell(pdfQrImage, false);
            qrCell.setBorder(Rectangle.NO_BORDER);
            qrCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            qrCell.setPaddingBottom(5);
            rightSideTable.addCell(qrCell);

            // Details Table (Border-less)
            PdfPTable detailsTable = new PdfPTable(2);
            detailsTable.setWidths(new float[]{0.9f, 2.0f});
            detailsTable.getDefaultCell().setBorder(Rectangle.NO_BORDER); // Hama cell ekakama border ain kala

            Font fBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 6);
            Font fNorm = FontFactory.getFont(FontFactory.HELVETICA, 6);

            addDetailRow(detailsTable, "Name", ": " + student_name_box.getText(), fBold, fNorm);
            addDetailRow(detailsTable, "Subject", ": " + subject_stream_box.getText(), fBold, fNorm);
            addDetailRow(detailsTable, "Contact No", ": " + student_contact_no_box.getText(), fBold, fNorm);

            PdfPCell detailsCellContainer = new PdfPCell(detailsTable);
            detailsCellContainer.setBorder(Rectangle.NO_BORDER);
            rightSideTable.addCell(detailsCellContainer);

            bodyTable.addCell(rightSideTable);
            document.add(bodyTable);

            // **3. FOOTER SECTION**
            PdfPTable footerTable = new PdfPTable(1);
            footerTable.setWidthPercentage(100);
            footerTable.setSpacingBefore(15f);

            String footerText = "www.gurumandala.lk | 077 775 8004 | 076 747 3738";
            PdfPCell footerCell = new PdfPCell(new Phrase(footerText,
                    FontFactory.getFont(FontFactory.HELVETICA, 6, Font.NORMAL, BaseColor.WHITE)));
            footerCell.setBackgroundColor(new BaseColor(11, 45, 114));
            footerCell.setBorder(Rectangle.NO_BORDER);
            footerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerCell.setPadding(4);
            footerTable.addCell(footerCell);

            document.add(footerTable);

            document.close();
            JOptionPane.showMessageDialog(null, "ID Card PDF Downloaded Successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Printing Error: " + e.getMessage());
        }
    }

    private void addDetailRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(valueCell);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        label1 = new javax.swing.JLabel();
        label3 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        student_name_box = new javax.swing.JLabel();
        label16 = new javax.swing.JLabel();
        birthday_box = new javax.swing.JLabel();
        school_box = new javax.swing.JLabel();
        label17 = new javax.swing.JLabel();
        nic_no_box = new javax.swing.JLabel();
        label18 = new javax.swing.JLabel();
        gender_box = new javax.swing.JLabel();
        label19 = new javax.swing.JLabel();
        address_box = new javax.swing.JLabel();
        label20 = new javax.swing.JLabel();
        student_contact_no_box = new javax.swing.JLabel();
        label21 = new javax.swing.JLabel();
        email_box = new javax.swing.JLabel();
        label22 = new javax.swing.JLabel();
        parent_name_box = new javax.swing.JLabel();
        label23 = new javax.swing.JLabel();
        parent_contact_no_box = new javax.swing.JLabel();
        label24 = new javax.swing.JLabel();
        subject_stream_box = new javax.swing.JLabel();
        label25 = new javax.swing.JLabel();
        subjects_box = new javax.swing.JLabel();
        label26 = new javax.swing.JLabel();
        student_id_box = new javax.swing.JLabel();
        qr_code_box = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        profile_image_path = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();

        jPanel1.setBackground(new java.awt.Color(191, 201, 209));
        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel1.setBackground(new java.awt.Color(28, 77, 141));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 626, Short.MAX_VALUE)
        );

        jPanel3.setBackground(new java.awt.Color(28, 77, 141));

        label1.setForeground(new java.awt.Color(255, 255, 255));
        label1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label1.setText("Student ID");

        label3.setForeground(new java.awt.Color(255, 255, 255));
        label3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label3.setText("Full Name");

        jButton1.setBackground(new java.awt.Color(246, 48, 73));
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Delete");
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.setIcon(delete_icon);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(168, 223, 142));
        jButton2.setForeground(new java.awt.Color(0, 0, 0));
        jButton2.setText("Print");
        jButton2.setIcon(print_icon);
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        student_name_box.setForeground(new java.awt.Color(255, 255, 255));
        student_name_box.setText("jLabel2");

        label16.setForeground(new java.awt.Color(255, 255, 255));
        label16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label16.setText("Birth Date");

        birthday_box.setForeground(new java.awt.Color(255, 255, 255));
        birthday_box.setText("jLabel2");

        school_box.setForeground(new java.awt.Color(255, 255, 255));
        school_box.setText("jLabel2");

        label17.setForeground(new java.awt.Color(255, 255, 255));
        label17.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label17.setText("School");

        nic_no_box.setForeground(new java.awt.Color(255, 255, 255));
        nic_no_box.setText("jLabel2");

        label18.setForeground(new java.awt.Color(255, 255, 255));
        label18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label18.setText("NIC No");

        gender_box.setForeground(new java.awt.Color(255, 255, 255));
        gender_box.setText("jLabel2");

        label19.setForeground(new java.awt.Color(255, 255, 255));
        label19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label19.setText("Gender");

        address_box.setForeground(new java.awt.Color(255, 255, 255));
        address_box.setText("jLabel2");

        label20.setForeground(new java.awt.Color(255, 255, 255));
        label20.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label20.setText("Address");

        student_contact_no_box.setForeground(new java.awt.Color(255, 255, 255));
        student_contact_no_box.setText("jLabel2");

        label21.setForeground(new java.awt.Color(255, 255, 255));
        label21.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label21.setText("Student Contact No");

        email_box.setForeground(new java.awt.Color(255, 255, 255));
        email_box.setText("jLabel2");

        label22.setForeground(new java.awt.Color(255, 255, 255));
        label22.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label22.setText("Email");

        parent_name_box.setForeground(new java.awt.Color(255, 255, 255));
        parent_name_box.setText("jLabel2");

        label23.setForeground(new java.awt.Color(255, 255, 255));
        label23.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label23.setText("Parent Name");

        parent_contact_no_box.setForeground(new java.awt.Color(255, 255, 255));
        parent_contact_no_box.setText("jLabel2");

        label24.setForeground(new java.awt.Color(255, 255, 255));
        label24.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label24.setText("Parent Contact No");

        subject_stream_box.setForeground(new java.awt.Color(255, 255, 255));
        subject_stream_box.setText("jLabel2");

        label25.setForeground(new java.awt.Color(255, 255, 255));
        label25.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label25.setText("Subject Stream");

        subjects_box.setForeground(new java.awt.Color(255, 255, 255));
        subjects_box.setText("jLabel2");

        label26.setForeground(new java.awt.Color(255, 255, 255));
        label26.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        label26.setText("Subjects");

        student_id_box.setForeground(new java.awt.Color(255, 255, 255));
        student_id_box.setText("jLabel2");

        qr_code_box.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        qr_code_box.setText("jLabel2");

        jButton3.setBackground(new java.awt.Color(245, 242, 242));
        jButton3.setForeground(new java.awt.Color(0, 0, 0));
        jButton3.setText("Add Subjects");
        jButton3.setIcon(add_icon);
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        profile_image_path.setText("jLabel2");
        profile_image_path.setVisible(false);

        jButton4.setBackground(new java.awt.Color(255, 179, 63));
        jButton4.setForeground(new java.awt.Color(0, 0, 0));
        jButton4.setText("Update");
        jButton4.setIcon(update_icon);
        jButton4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(label1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label21, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                    .addComponent(label22, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                    .addComponent(label23, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                    .addComponent(label24, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                    .addComponent(label25, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                    .addComponent(label26, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE))
                .addGap(40, 40, 40)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(birthday_box, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
                    .addComponent(school_box, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(nic_no_box, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(gender_box, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(address_box, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(student_contact_no_box, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(email_box, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(parent_name_box, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(parent_contact_no_box, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(subject_stream_box, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(subjects_box, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(student_name_box, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(student_id_box, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0)
                .addComponent(qr_code_box, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(profile_image_path)))
                .addGap(16, 16, 16))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(46, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label1)
                    .addComponent(student_id_box))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label3)
                    .addComponent(student_name_box))
                .addGap(21, 21, 21)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label16)
                    .addComponent(birthday_box))
                .addGap(21, 21, 21)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label17)
                    .addComponent(school_box))
                .addGap(21, 21, 21)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label18)
                    .addComponent(nic_no_box))
                .addGap(21, 21, 21)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label19)
                    .addComponent(gender_box))
                .addGap(21, 21, 21)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label20)
                    .addComponent(address_box))
                .addGap(21, 21, 21)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label21)
                    .addComponent(student_contact_no_box))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label22)
                    .addComponent(email_box))
                .addGap(21, 21, 21)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label23)
                    .addComponent(parent_name_box))
                .addGap(21, 21, 21)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label24)
                    .addComponent(parent_contact_no_box))
                .addGap(21, 21, 21)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label25)
                    .addComponent(subject_stream_box))
                .addGap(21, 21, 21)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label26)
                    .addComponent(subjects_box))
                .addGap(100, 100, 100))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(131, 131, 131)
                .addComponent(profile_image_path)
                .addGap(103, 103, 103)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton4)
                .addGap(10, 10, 10)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(qr_code_box, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1193, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 630, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        delete_class();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        Print_Card();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        String student_id = student_id_box.getText();
        String subject_stream = subject_stream_box.getText();

        Add_Subjects m1 = new Add_Subjects(student_id, subject_stream, parentPanel);
        m1.setVisible(true);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        String name = student_name_box.getText();
        String birth_date = birthday_box.getText();
        String school = school_box.getText();
        String nic_no = nic_no_box.getText();
        String gender = gender_box.getText();
        String address = address_box.getText();
        String student_contact_no = student_contact_no_box.getText();
        String email = email_box.getText();
        String parent_name = parent_name_box.getText();
        String parent_contact_no = parent_contact_no_box.getText();
        String subject_stream = subject_stream_box.getText();
        
    }//GEN-LAST:event_jButton4ActionPerformed

    public void delete_class() {

        String student_id = student_id_box.getText();
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure want to Delete?", "Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {

                String query = "DELETE FROM students WHERE student_id = ?";

                pst = conn.prepareStatement(query);
                pst.setString(1, student_id);
                int result = pst.executeUpdate();

                if (result > 0) {
                    JOptionPane.showMessageDialog(null, "Student Deleted Successfully!");
                }

                if (parentPanel != null) {
                    parentPanel.view_data(); // Methanadi main panel eka refresh wenawa!
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel address_box;
    private javax.swing.JLabel birthday_box;
    private javax.swing.JLabel email_box;
    private javax.swing.JLabel gender_box;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel label1;
    private javax.swing.JLabel label16;
    private javax.swing.JLabel label17;
    private javax.swing.JLabel label18;
    private javax.swing.JLabel label19;
    private javax.swing.JLabel label20;
    private javax.swing.JLabel label21;
    private javax.swing.JLabel label22;
    private javax.swing.JLabel label23;
    private javax.swing.JLabel label24;
    private javax.swing.JLabel label25;
    private javax.swing.JLabel label26;
    private javax.swing.JLabel label3;
    private javax.swing.JLabel nic_no_box;
    private javax.swing.JLabel parent_contact_no_box;
    private javax.swing.JLabel parent_name_box;
    private javax.swing.JLabel profile_image_path;
    private javax.swing.JLabel qr_code_box;
    private javax.swing.JLabel school_box;
    private javax.swing.JLabel student_contact_no_box;
    private javax.swing.JLabel student_id_box;
    private javax.swing.JLabel student_name_box;
    private javax.swing.JLabel subject_stream_box;
    private javax.swing.JLabel subjects_box;
    // End of variables declaration//GEN-END:variables
}
