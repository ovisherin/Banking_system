package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import dao.CustomerDao;
import dao.DBConnection1;
import model.Customer;
import model.Transaction;

@WebServlet("/CustomerController")
public class CustomerController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        CustomerDao customerDao = new CustomerDao();

        if ("login".equals(action)) {
            String accountNo = request.getParameter("accountNo");
            String password = request.getParameter("password");

            Customer customer = customerDao.validateCustomer(accountNo, password);

            if (customer != null) {
                request.getSession().setAttribute("customer", customer); // Store customer in session
                request.getRequestDispatcher("customerDashboard.jsp").forward(request, response);
            } else {
                response.sendRedirect("login.jsp?error=InvalidAccountNoOrPassword");
            }
            return; // Ensure method exits after handling the login action
        }

        if ("deposit".equals(action)) {
            handleDeposit(request, response, customerDao);
        } else if ("withdraw".equals(action)) {
            handleWithdraw(request, response, customerDao);
        } else if ("transfer".equals(action)) {
            handleTransfer(request, response, customerDao);
        } else if ("viewTransactions".equals(action)) {
            handleViewTransactions(request, response, customerDao);
        } else if ("logout".equals(action)) {
            request.getSession().invalidate();
            response.sendRedirect("index.jsp");
        }
        else if ("changePassword".equals(action)) {
            handleChangePassword(request, response, customerDao);
        }
    }

    private void handleDeposit(HttpServletRequest request, HttpServletResponse response, CustomerDao customerDao) throws IOException, ServletException {
        String accountNo = request.getParameter("accountNo");
        double amount = Double.parseDouble(request.getParameter("amount"));

        Customer customer = customerDao.getCustomerDetails(accountNo);

        if (customer != null) {
            double currentBalance = customer.getBalance();
            double newBalance = currentBalance + amount;

            boolean success = customerDao.updateCustomerBalance(accountNo, newBalance);

            if (success) {
                customer.setBalance(newBalance);
                request.getSession().setAttribute("customer", customer);

                Transaction transaction = new Transaction();
                transaction.setAccountNo(accountNo);
                transaction.setAmount(amount);
                transaction.setTransactionType("DEPOSIT");
                transaction.setTransactionDate(new Timestamp(System.currentTimeMillis()));

                try {
                    CustomerDao.addTransaction(transaction);
                    response.sendRedirect("customerDashboard.jsp?success=DepositSuccessful");
                } catch (SQLException e) {
                    e.printStackTrace();
                    response.sendRedirect("customerDashboard.jsp?error=TransactionRecordFailed");
                }
            } else {
                response.sendRedirect("customerDashboard.jsp?error=DepositFailed");
            }
        } else {
            response.sendRedirect("customerDashboard.jsp?error=CustomerNotFound");
        }
    }

    private void handleWithdraw(HttpServletRequest request, HttpServletResponse response, CustomerDao customerDao) throws IOException, ServletException {
        String accountNo = request.getParameter("accountNo");
        double amount = Double.parseDouble(request.getParameter("amount"));

        Customer customer = customerDao.getCustomerDetails(accountNo);

        if (customer != null) {
            double currentBalance = customer.getBalance();
            if (currentBalance >= amount) {
                double newBalance = currentBalance - amount;

                boolean success = customerDao.updateCustomerBalance(accountNo, newBalance);

                if (success) {
                    customer.setBalance(newBalance);
                    request.getSession().setAttribute("customer", customer);

                    Transaction transaction = new Transaction();
                    transaction.setAccountNo(accountNo);
                    transaction.setAmount(amount);
                    transaction.setTransactionType("WITHDRAW");
                    transaction.setTransactionDate(new Timestamp(System.currentTimeMillis()));

                    try {
                        CustomerDao.addTransaction(transaction);
                        response.sendRedirect("customerDashboard.jsp?success=WithdrawSuccessful");
                    } catch (SQLException e) {
                        e.printStackTrace();
                        response.sendRedirect("customerDashboard.jsp?error=TransactionRecordFailed");
                    }
                } else {
                    response.sendRedirect("customerDashboard.jsp?error=WithdrawFailed");
                }
            } else {
                response.sendRedirect("customerDashboard.jsp?error=InsufficientBalance");
            }
        } else {
            response.sendRedirect("customerDashboard.jsp?error=CustomerNotFound");
        }
    }

    private void handleTransfer(HttpServletRequest request, HttpServletResponse response, CustomerDao customerDao) throws IOException, ServletException {
        String accountNo = request.getParameter("accountNo");
        String transferAccountNo = request.getParameter("transferAccountNo");
        double amount = Double.parseDouble(request.getParameter("transferAmount"));

        Customer sender = customerDao.getCustomerDetails(accountNo);
        Customer receiver = customerDao.getCustomerDetails(transferAccountNo);

        if (sender != null && receiver != null) {
            double senderBalance = sender.getBalance();
            if (senderBalance >= amount) {
                double receiverBalance = receiver.getBalance();

                Connection conn = null;
                try {
                    conn = DBConnection1.getConnection();
                    conn.setAutoCommit(false);

                    boolean successSender = customerDao.updateCustomerBalance(accountNo, senderBalance - amount);
                    boolean successReceiver = customerDao.updateCustomerBalance(transferAccountNo, receiverBalance + amount);

                    if (successSender==true && successReceiver==true) {
                        Transaction senderTrans = new Transaction();
                        senderTrans.setAccountNo(accountNo);
                        senderTrans.setAmount(-amount);
                        senderTrans.setTransactionType("TRANSFER TO " + transferAccountNo);
                        senderTrans.setTransactionDate(new Timestamp(System.currentTimeMillis()));

                        Transaction receiverTrans = new Transaction();
                        receiverTrans.setAccountNo(transferAccountNo);
                        receiverTrans.setAmount(amount);
                        receiverTrans.setTransactionType("TRANSFER FROM " + accountNo);
                        receiverTrans.setTransactionDate(new Timestamp(System.currentTimeMillis()));

                        boolean senderTransSuccess = CustomerDao.addTransaction(senderTrans);
                        boolean receiverTransSuccess = CustomerDao.addTransaction(receiverTrans);

                        if (senderTransSuccess && receiverTransSuccess) {
                            conn.commit();
                            sender.setBalance(senderBalance - amount);
                            receiver.setBalance(receiverBalance + amount);
                            request.getSession().setAttribute("customer", sender);
                            response.sendRedirect("customerDashboard.jsp?success=TransferSuccessful");
                        } else {
                            throw new SQLException("Failed to record transactions");
                        }
                    } else {
                        throw new SQLException("Failed to update balances");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    if (conn != null) {
                        try {
                            conn.rollback();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                    response.sendRedirect("customerDashboard.jsp?error=TransferFailed");
                } finally {
                    if (conn != null) {
                        try {
                            conn.setAutoCommit(true);
                            conn.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                response.sendRedirect("customerDashboard.jsp?error=InsufficientBalance");
            }
        } else {
            response.sendRedirect("customerDashboard.jsp?error=CustomerNotFound");
        }
    }

    private void handleViewTransactions(HttpServletRequest request, HttpServletResponse response, CustomerDao customerDao) throws ServletException, IOException {
        Customer customer = (Customer) request.getSession().getAttribute("customer");
        String accountNo = customer.getAccountNo();

        List<Transaction> transactions = customerDao.getTransactions(accountNo);

        if (transactions != null) {
            request.setAttribute("transactions", transactions);
            request.getRequestDispatcher("transactions.jsp").forward(request, response);
        } else {
            response.sendRedirect("customerDashboard.jsp?error=FailedToRetrieveTransactions");
        }
    }
    private void handleChangePassword(HttpServletRequest request, HttpServletResponse response, CustomerDao customerDao) throws IOException, ServletException {
        String accountNo = request.getParameter("accountNo");
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        Customer customer = customerDao.validateCustomer(accountNo, currentPassword);

        if (customer != null) {
            if (newPassword.equals(confirmPassword)) {
                boolean success = customerDao.updatePassword(accountNo, newPassword);
                if (success) {
                    response.sendRedirect("customerDashboard.jsp?success=PasswordChanged");
                } else {
                    response.sendRedirect("customerDashboard.jsp?error=PasswordChangeFailed");
                }
            } else {
                response.sendRedirect("customerDashboard.jsp?error=PasswordsMismatch");
            }
        } else {
            response.sendRedirect("customerDashboard.jsp?error=InvalidCurrentPassword");
        }
    }
}
