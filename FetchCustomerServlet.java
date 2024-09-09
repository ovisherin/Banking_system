package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.CustomerDao;
import model.Customer;

@WebServlet("/FetchCustomerServlet")
public class FetchCustomerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accountNo = request.getParameter("accountNo");
        CustomerDao customerDao = new CustomerDao();
        Customer customer = customerDao.getCustomerByAccno(accountNo);
        
        if (customer != null) {
            request.setAttribute("customer", customer);
            request.getRequestDispatcher("updateCustomer.jsp").forward(request, response);
        } else {
            response.sendRedirect("adminDashboard.jsp?error=CustomerNotFound");
        }
    }
}