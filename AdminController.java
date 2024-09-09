package controller;

import java.io.IOException;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import dao.CustomerDao;
import model.Customer;

@WebServlet("/AdminController")
public class AdminController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        CustomerDao customerDao = new CustomerDao();
        
        if ("create".equals(action)) {
            Customer customer = new Customer();
            customer.setFullName(request.getParameter("fullName"));
            customer.setAddress(request.getParameter("address"));
            customer.setMobileNo(request.getParameter("mobileNo"));
            customer.setEmailId(request.getParameter("emailId"));
            customer.setAccountType(request.getParameter("accountType"));
            customer.setBalance(Double.parseDouble(request.getParameter("initialBalance")));
            customer.setDateOfBirth(request.getParameter("dateOfBirth"));
            customer.setIdProof(request.getParameter("idProof"));
            customer.setAccountNo(generateAccountNo());
            customer.setTempPassword(generateTempPassword());
            
            if (customerDao.addCustomer(customer)) {
                request.setAttribute("newCustomer", customer);
                request.getRequestDispatcher("/customerCreated.jsp").forward(request, response);
            } else {
                response.sendRedirect("adminDashboard.jsp?error=CreationFailed");
            }
        }else if ("edit".equals(action)) {
            String accountNo = request.getParameter("accountNo");
            Customer customer = customerDao.getCustomerByAccno(accountNo);
            if (customer != null) {
                request.setAttribute("customer", customer);
                request.getRequestDispatcher("/updateCustomer.jsp").forward(request, response);
            } else {
                response.sendRedirect("adminDashboard.jsp?error=CustomerNotFound");
            }
        }
        
        
        else if ("update".equals(action)) {
            String accountNo = request.getParameter("accountNo");
            Customer customer = new Customer();
            customer.setAccountNo(accountNo);
            customer.setFullName(request.getParameter("fullName"));
            customer.setAddress(request.getParameter("address"));
            customer.setMobileNo(request.getParameter("mobileNo"));
            customer.setEmailId(request.getParameter("emailId"));
            customer.setAccountType(request.getParameter("accountType"));
            customer.setDateOfBirth(request.getParameter("dateOfBirth"));
            customer.setIdProof(request.getParameter("idProof"));
            
//            if (customerDao.updateCustomer(customer)) {
//                response.sendRedirect("adminDashboard.jsp?success=CustomerUpdated");
//            } else {
//                response.sendRedirect("adminDashboard.jsp?error=UpdateFailed");
//            }
//        }
//        }
            if (customerDao.updateCustomer(customer)) {
                response.sendRedirect("adminDashboard.jsp?success=CustomerUpdated");
            } else {
                response.sendRedirect("adminDashboard.jsp?error=UpdateFailed");
            }
        } else if ("delete".equals(action)) {
            String accountNo = request.getParameter("accountNo");
            if (customerDao.deleteCustomer(accountNo)) {
                response.sendRedirect("adminDashboard.jsp?success=CustomerDeleted");
            } else {
                response.sendRedirect("adminDashboard.jsp?error=DeleteFailed");
            }
        }
    }
 
        
        

  
    private String generateAccountNo() {
        return "Trisha" + new Random().nextInt(999999999);
    }

    private String generateTempPassword() {
        return String.valueOf(new Random().nextInt(999999));
    }
}