package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.DBConnection1;

@WebServlet("/CloseAccountServlet")
public class CloseAccountServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accountNo = request.getParameter("account_no");
        String password = request.getParameter("password");

        Connection conn = DBConnection1.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // Check account details
            String query = "SELECT balance, temp_password FROM Customer WHERE account_no = ?";
            ps = conn.prepareStatement(query);
            ps.setString(1, accountNo);
            rs = ps.executeQuery();

            if (rs.next()) {
                double balance = rs.getDouble("balance");
                String dbPassword = rs.getString("temp_password");

                if (!password.equals(dbPassword)) {
                    response.getWriter().println("Incorrect password.");
                    return;
                }

                if (balance != 0) {
                    response.getWriter().println("Account balance is not zero. Please withdraw all the money before closing the account.");
                    return;
                }

                // Delete related transactions
                String deleteTransactionsQuery = "DELETE FROM Transaction WHERE account_no = ?";
                ps = conn.prepareStatement(deleteTransactionsQuery);
                ps.setString(1, accountNo);
                ps.executeUpdate();

                // Delete account
                String deleteQuery = "DELETE FROM Customer WHERE account_no = ?";
                ps = conn.prepareStatement(deleteQuery);
                ps.setString(1, accountNo);
                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0) {
                    response.getWriter().println("Account closed successfully.");
                } else {
                    response.getWriter().println("Account not found.");
                }
            } else {
                response.getWriter().println("Account not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("An error occurred while processing your request.");
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}