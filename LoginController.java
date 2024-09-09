package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import dao.AdminDao;
import dao.CustomerDao;
import model.Customer;

@WebServlet("/LoginController")
public class LoginController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        if ("admin".equals(role)) {
            AdminDao adminDao = new AdminDao();
            if (adminDao.validateAdmin(username, password)) {
                HttpSession session = request.getSession();
                session.setAttribute("role", "admin");
                response.sendRedirect("adminDashboard.jsp");
            } else {
                response.sendRedirect("login.jsp?error=InvalidCredentials");
            }
        } else if ("customer".equals(role)) {
            CustomerDao customerDao = new CustomerDao();
            Customer customer = customerDao.validateCustomer(username, password);
            if (customer != null) {
                HttpSession session = request.getSession();
                session.setAttribute("role", "customer");
                session.setAttribute("customer", customer); 
                response.sendRedirect("customerDashboard.jsp");
            } else {
                response.sendRedirect("index.jsp?error=Invalid Credentials");
            }
        }
    }
}
