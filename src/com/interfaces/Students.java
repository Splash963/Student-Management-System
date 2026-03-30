/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.interfaces;

import com.connection.DbConnection;
import java.awt.Dimension;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.ImageIcon;

/**
 *
 * @author Theekshana
 */
public class Students extends javax.swing.JPanel {

    Connection conn = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    public Students() {
        initComponents();
        conn = DbConnection.connect();
        view_data();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        data_box = new javax.swing.JTextField();
        value_box = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        mainPanel = new javax.swing.JPanel();

        data_box.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                data_boxCaretUpdate(evt);
            }
        });

        value_box.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Name", "ID", "NIC", "Batch", "Subjects", "Subject Stream" }));

        jLabel1.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Manage Students");

        jButton1.setBackground(new java.awt.Color(72, 161, 17));
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Add Student");
        jButton1.setBorder(new RoundedBorder(20));
        jButton1.setContentAreaFilled(false);
        jButton1.setOpaque(false);
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 225, Short.MAX_VALUE)
                .addComponent(value_box, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(data_box, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(data_box, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(value_box, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        mainPanel.setLayout(new javax.swing.BoxLayout(mainPanel, javax.swing.BoxLayout.Y_AXIS));
        jScrollPane1.setViewportView(mainPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 686, Short.MAX_VALUE)
                    .addGap(0, 0, 0)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 344, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addGap(98, 98, 98)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Add_Student m1 = new Add_Student(this);
        m1.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void data_boxCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_data_boxCaretUpdate
        search_data();
    }//GEN-LAST:event_data_boxCaretUpdate

    public void view_data() {
        try {
            // 1. Mulu student list ekama gannawa
            String sql = "SELECT * FROM students";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();

            mainPanel.removeAll();

            while (rs.next()) {
                // Student details rs eken ganna
                String student_id = rs.getString("student_id");
                String name = rs.getString("name");
                String birthday = rs.getString("birthday");
                String school = rs.getString("school");
                String nic_no = rs.getString("nic_no");
                String gender = rs.getString("gender");
                String address = rs.getString("address");
                String student_contact_no = rs.getString("student_contact_no");
                String email = rs.getString("email");
                String parent_name = rs.getString("parent_name");
                String parent_contact_no = rs.getString("parent_contact_no");
                String subject_stream = rs.getString("subject_stream");
                String profile_picture = rs.getString("profile_picture");
                byte[] qr_code = rs.getBytes("qr_code");

                // --- Aluth Classes details ganna kote ---
                ArrayList<String> subjectList = new ArrayList<>();

                // Me query eken students_classes saha classes table dekama join karanawa
                String subjectSql = "SELECT c.subject, c.day, c.start_time, c.end_time "
                        + "FROM students_classes sc "
                        + "INNER JOIN classes c ON sc.class_id = c.class_id "
                        + "WHERE sc.student_id = ?";

                PreparedStatement pstSubjects = conn.prepareStatement(subjectSql);
                pstSubjects.setString(1, student_id);
                ResultSet rsSubjects = pstSubjects.executeQuery();

                while (rsSubjects.next()) {
                    // Subject eka saha welaawa ekata ekathu karala list ekata danawa
                    String info = rsSubjects.getString("subject") + " (" + rsSubjects.getString("day") + " | " + " " + rsSubjects.getString("start_time") + " To " + rsSubjects.getString("end_time") + ")";
                    subjectList.add(info);
                }

                // Result sets close kirima (Memory management)
                rsSubjects.close();
                pstSubjects.close();
                // ---------------------------------------

                // Dan me 'subjectList' eka StudentCard ekata pass karanna
                // Note: Oyage StudentCard class eke anthima parameter eka ArrayList<String> wenna ona
                StudentCard card = new StudentCard(
                        student_id, name, birthday, school, nic_no, gender, address,
                        student_contact_no, email, parent_name, parent_contact_no,
                        subject_stream, profile_picture, qr_code, subjectList, this
                );

                mainPanel.add(card);
                mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }

            mainPanel.revalidate();
            mainPanel.repaint();
            jScrollPane1.getVerticalScrollBar().setUnitIncrement(30);

        } catch (Exception e) {
            e.printStackTrace(); // Error ekak awoth console eke balaganna
        }
    }

    public void search_data() {
        String value = value_box.getSelectedItem().toString();
        String data = data_box.getText();
        String query = null;

        // 1. Query eka define karanna
        if (value.equals("Name")) {
            query = "SELECT * FROM students WHERE name LIKE ?";
        } else if (value.equals("ID")) {
            query = "SELECT * FROM students WHERE id = ?";
        }

        try {
            // 2. Clear previous results (Aluth search ekedi parana card ain karanna ona)
            mainPanel.removeAll();

            pst = conn.prepareStatement(query);

            if (value.equals("Name")) {
                pst.setString(1, data + "%");
            } else {
                pst.setString(1, data);
            }

            rs = pst.executeQuery();

            // 3. Loop through result set and create cards
            while (rs.next()) {
                // StudentCard class eke constructor ekata ResultSet eka hari values hari yawanna
                // Oyaage StudentCard class eke hadapu widiyata meka wenas karanna
                StudentCard card = new StudentCard();

                // Card ekata data set karanna (Oya card eke hadala thiyena methods anuwa)
                card.setStudentData(
                        rs.getString("name"),
                        rs.getString("student_id"),
                        rs.getString("school") // thawa thiyena field danna
                );

                mainPanel.add(card); // Card eka panel ekata add karanawa
            }

            // 4. Refresh the UI
            mainPanel.revalidate();
            mainPanel.repaint();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField data_box;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JComboBox<String> value_box;
    // End of variables declaration//GEN-END:variables
}
