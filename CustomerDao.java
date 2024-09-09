package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import model.Customer;
import model.Transaction;

public class CustomerDao {

    // Existing addCustomer method
    public boolean addCustomer(Customer customer) {
        try (Connection connection = DBConnection1.getConnection()) {
            String query = "INSERT INTO Customer (full_name, address, mobile_no, email_id, account_type, balance, date_of_birth, id_proof, account_no, temp_password) VALUES (?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, customer.getFullName());
            statement.setString(2, customer.getAddress());
            statement.setString(3, customer.getMobileNo());
            statement.setString(4, customer.getEmailId());
            statement.setString(5, customer.getAccountType());  
            statement.setDouble(6, customer.getBalance());
            statement.setString(7, customer.getDateOfBirth());
            statement.setString(8, customer.getIdProof());
            statement.setString(9, customer.getAccountNo());
            statement.setString(10, customer.getTempPassword());

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Method to retrieve all customers from the database
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String query = "SELECT * FROM Customer";

        try (Connection connection = DBConnection1.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(resultSet.getString("customer_id"));  // Ensure this line is correct
                customer.setFullName(resultSet.getString("full_name"));
                customer.setAddress(resultSet.getString("address"));
                customer.setMobileNo(resultSet.getString("mobile_no"));
                customer.setEmailId(resultSet.getString("email_id"));
                customer.setAccountType(resultSet.getString("account_type"));  // Ensure this line is correct
                customer.setBalance(resultSet.getDouble("balance"));
                customer.setDateOfBirth(resultSet.getString("date_of_birth"));
                customer.setIdProof(resultSet.getString("id_proof"));
                customer.setAccountNo(resultSet.getString("account_no"));
                customer.setTempPassword(resultSet.getString("temp_password"));

                customers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }
  //Methods for validatecustomer
    public Customer validateCustomer(String accountno, String password) {
        try (Connection connection = DBConnection1.getConnection()) {
            String query = "SELECT * FROM customer WHERE account_no =? AND temp_password =?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, accountno);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(resultSet.getString("customer_id"));  // Ensure this line is correct
                customer.setFullName(resultSet.getString("full_name"));
                customer.setAddress(resultSet.getString("address"));
                customer.setMobileNo(resultSet.getString("mobile_no"));
                customer.setEmailId(resultSet.getString("email_id"));
                customer.setAccountType(resultSet.getString("account_type"));  // Ensure this line is correct
                customer.setBalance(resultSet.getDouble("balance"));
                customer.setDateOfBirth(resultSet.getString("date_of_birth"));
                customer.setIdProof(resultSet.getString("id_proof"));
                customer.setAccountNo(resultSet.getString("account_no"));
                customer.setTempPassword(resultSet.getString("temp_password"));
                return customer;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
  //Methods for getcustomerdetails
    public Customer getCustomerDetails(String accountNo) {
        try (Connection connection = DBConnection1.getConnection()) {
            String query = "SELECT * FROM customer WHERE account_no =?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, accountNo);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(resultSet.getString("customer_id"));  // Ensure this line is correct
                customer.setFullName(resultSet.getString("full_name"));
                customer.setAddress(resultSet.getString("address"));
                customer.setMobileNo(resultSet.getString("mobile_no"));
                customer.setEmailId(resultSet.getString("email_id"));
                customer.setAccountType(resultSet.getString("account_type"));  // Ensure this line is correct
                customer.setBalance(resultSet.getDouble("balance"));
                customer.setDateOfBirth(resultSet.getString("date_of_birth"));
                customer.setIdProof(resultSet.getString("id_proof"));
                customer.setAccountNo(resultSet.getString("account_no"));
                customer.setTempPassword(resultSet.getString("temp_password"));
                return customer;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }           // Method to update customer balance
        public boolean updateCustomerBalance(String accountNo, double newBalance) {
            try (Connection connection = DBConnection1.getConnection()) {
                String query = "UPDATE customer SET balance = ? WHERE account_no = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setDouble(1, newBalance);
                statement.setString(2, accountNo);

                int rowsUpdated = statement.executeUpdate();
                return rowsUpdated > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }
        public List<Transaction> getTransactions(String accountNo) {
            List<Transaction> transactions = new ArrayList<>();
            String query = "SELECT * FROM transaction WHERE account_no = ? ORDER BY transaction_date DESC";

            try (Connection connection = DBConnection1.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setString(1, accountNo);

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        Transaction transaction = new Transaction();
                        transaction.setTransactionId(resultSet.getInt("transaction_id"));
                        transaction.setAccountNo(resultSet.getString("account_no"));
                        transaction.setAmount(resultSet.getDouble("amount"));
                        transaction.setTransactionType(resultSet.getString("transaction_type"));
                        transaction.setTransactionDate(resultSet.getTimestamp("transaction_date"));

                        transactions.add(transaction);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return transactions;
        }
      //Methods for updatecustomer

		public boolean updateCustomer(Customer customer) {
		    try (Connection connection = DBConnection1.getConnection()) {
		        String query = "UPDATE Customer SET full_name = ?, address = ?, mobile_no = ?, email_id = ?, account_type = ?, date_of_birth = ?, id_proof = ? WHERE account_no = ?";
		        PreparedStatement statement = connection.prepareStatement(query);
		        statement.setString(1, customer.getFullName());
		        statement.setString(2, customer.getAddress());
		        statement.setString(3, customer.getMobileNo());
		        statement.setString(4, customer.getEmailId());
		        statement.setString(5, customer.getAccountType());
		        statement.setString(6, customer.getDateOfBirth());
		        statement.setString(7, customer.getIdProof());
		        statement.setString(8, customer.getAccountNo());

		        int rowsUpdated = statement.executeUpdate();
		        return rowsUpdated > 0;
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		    return false;
		}
		//Methods for getcustomerbyaccno
		public Customer getCustomerByAccno(String accNo) {
			try (Connection connection = DBConnection1.getConnection()){
				String query = "Select * from customer where account_no = ? ";
				PreparedStatement statement = connection.prepareStatement(query);
	            statement.setString(1, accNo);
	            ResultSet resultSet = statement.executeQuery();
	            if (resultSet.next()) {
	                Customer customer = new Customer();
	                customer.setCustomerId(resultSet.getString("customer_id"));  // Ensure this line is correct
	                customer.setFullName(resultSet.getString("full_name"));
	                customer.setAddress(resultSet.getString("address"));
	                customer.setMobileNo(resultSet.getString("mobile_no"));
	                customer.setEmailId(resultSet.getString("email_id"));
	                customer.setAccountType(resultSet.getString("account_type"));  // Ensure this line is correct
	                customer.setBalance(resultSet.getDouble("balance"));
	                customer.setDateOfBirth(resultSet.getString("date_of_birth"));
	                customer.setIdProof(resultSet.getString("id_proof"));
	                customer.setAccountNo(resultSet.getString("account_no"));
	                customer.setTempPassword(resultSet.getString("temp_password"));
	                return customer;
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return null;
		}
		//Methods for deletecustomer
//		 public boolean deleteCustomer(String accountNo) {
//		        String query = "DELETE FROM customer WHERE accountNo = ?";
//		        try (Connection connection = DBConnection1.getConnection();
//		             PreparedStatement statement = connection.prepareStatement(query)) {
//		            statement.setString(1, accountNo);
//		            return statement.executeUpdate() > 0;
//		        } catch (SQLException e) {
//		            e.printStackTrace();
//		        }
//		        return false;
//		    }
		 public boolean deleteCustomer(String accountNo) {
		        try (Connection connection = DBConnection1.getConnection()) {
		            String query = "DELETE FROM customer WHERE account_no = ?";
		            PreparedStatement statement = connection.prepareStatement(query);
		            statement.setString(1, accountNo);

		            int rowsDeleted = statement.executeUpdate();
		            return rowsDeleted > 0;
		        } catch (SQLException e) {
		            e.printStackTrace();
		        }
		        return false;
		    }  
		//Methods for addTransaction
		public static boolean addTransaction(Transaction transaction) throws SQLException {
		    String sql = "INSERT INTO transaction (account_no, amount, transaction_type, transaction_date) VALUES (?, ?, ?, ?)";
		    try (Connection connection = DBConnection1.getConnection();
		         PreparedStatement statement = connection.prepareStatement(sql)) {
		        statement.setString(1, transaction.getAccountNo());
		        statement.setDouble(2, transaction.getAmount());
		        statement.setString(3, transaction.getTransactionType());
		        statement.setTimestamp(4, transaction.getTransactionDate());

		        int rowsAffected = statement.executeUpdate();
		        return rowsAffected > 0;
		    } catch (SQLException e) {
		        e.printStackTrace();
		        throw e;
		    }
		}
//Methods for moneytransfer
//		public static void MoneyTransfer(String accountNo, double amount, String transactionType) throws SQLException {
//		    String query = "INSERT INTO transaction (account_no, amount, transaction_type, transaction_date) VALUES (?, ?, ?, ?)";
//		    try (Connection conn = DBConnection1.getConnection();
//		         PreparedStatement stmt = conn.prepareStatement(query)) {
//		        stmt.setString(1, accountNo);
//		        stmt.setDouble(2, amount);
//		        stmt.setString(3, transactionType);
//		        stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
//		        stmt.executeUpdate();
//		    }
//		}
		//Methods for updatepassword		
		public boolean updatePassword(String accountNo, String newPassword) {
		    try (Connection connection = DBConnection1.getConnection()) {
		        String query = "UPDATE Customer SET temp_password = ? WHERE account_no = ?";
		        PreparedStatement statement = connection.prepareStatement(query);
		        statement.setString(1, newPassword);
		        statement.setString(2, accountNo);

		        int rowsUpdated = statement.executeUpdate();
		        return rowsUpdated > 0;
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		    return false;
		}
}