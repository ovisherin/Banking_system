package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Transaction;

public class TransactionDao {
    public boolean addTransaction(Transaction transaction) {
        try (Connection connection = DBConnection1.getConnection()) {
            String query = "INSERT INTO transaction (account_no, amount, transaction_type, transaction_date) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, transaction.getAccountNo());
            statement.setDouble(2, transaction.getAmount());
            statement.setString(3, transaction.getTransactionType());
            statement.setTimestamp(4, transaction.getTransactionDate());

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Transaction> getLast10Transactions(String accountNo) {
        List<Transaction> transactions = new ArrayList<>();
        try (Connection connection = DBConnection1.getConnection()) {
            String query = "SELECT * FROM transaction WHERE account_no = ? ORDER BY transaction_date DESC LIMIT 10";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, accountNo);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Transaction transaction = new Transaction();
                transaction.setTransactionId(resultSet.getInt("transaction_id"));
                transaction.setAccountNo(resultSet.getString("account_no"));
                transaction.setAmount(resultSet.getDouble("amount"));
                transaction.setTransactionType(resultSet.getString("transaction_type"));
                transaction.setTransactionDate(resultSet.getTimestamp("transaction_date"));
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    // Additional methods for handling transactions
}
