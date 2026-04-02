package com.connection;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.io.OutputStream;
import java.sql.*;
import java.time.LocalTime;

public class AttendanceServer {

    Connection conn = null;
    PreparedStatement pst = null;
    
    public void startServer(int port) {
        try {
            // Port 8080 wage ekak open karanawa
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

            // AttendanceServer.java athule thiyena Handler eka mehema wenas karanna
            server.createContext("/mark", exchange -> {
                String query = exchange.getRequestURI().getQuery();
                String nicNo = query.split("=")[1]; // Mobile eken enne NIC eka

                // DB Logic eka call karanna
                boolean success = updateAttendanceStatus(nicNo);

                String response = success ? "Present Marked!" : "Student or Class Not Found";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            });

            server.setExecutor(null);
            server.start();
            System.out.println("Server started on port " + port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean updateAttendanceStatus(String nic) {
        
        // Oyage DB connection class eka
        conn = DbConnection.connect();
        
        try {
            // Complex SQL Query ekak liyamu okkoma check wenna
            String sql = "UPDATE attendance a "
                    + "JOIN students s ON a.student_id = s.student_id "
                    + "JOIN classes c ON a.class_id = c.class_id "
                    + "SET a.status = 'Present' "
                    + "WHERE s.nic_no = ? "
                    + "AND a.date = CURDATE() "
                    + "AND a.status = 'Absent' "
                    + "AND c.day = DAYNAME(CURDATE()) "
                    + // Ada Sunday nam Sunday classes witharai
                    "AND ? BETWEEN SUBTIME(c.start_time, '01:00:00') AND ADDTIME(c.start_time, '00:30:00')";
            // Class start wenna payakata kalin saha winadi 30k yanakan scan karanna puluwan

            pst = conn.prepareStatement(sql);
            pst.setString(1, nic);

            // Dan thiyena time eka pass karanawa
            LocalTime now = LocalTime.now();
            pst.setTime(2, java.sql.Time.valueOf(now));

            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0; // Update wuna nam true ewanawa

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
