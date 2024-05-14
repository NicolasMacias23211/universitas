package co.edu.poli.ces3.universitas.database;

import co.edu.poli.ces3.universitas.dao.User;

import java.sql.*;
import java.util.*;

public class ConexionMySql {

    private String user;
    private String password;
    private int port;
    private String host;
    private String nameDatabase;
    private Connection cnn;

    public ConexionMySql(){
        this.user = "root";
        password = "admin";
        port = 3306;
        host = "127.0.0.1";
        nameDatabase = "universitas";
    }

    private void createConexion(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.cnn = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.nameDatabase + "?useSSL=false", this.user, this.password);
            System.out.println("Successful connection");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("An error occurred during the connection");
            throw new RuntimeException(e);
        }
    }

    public List<User> getUsers() throws SQLException {
        String sql = "SELECT * FROM users";
        List<User> list = new ArrayList<>();
        try {
            createConexion();
            Statement stmt = cnn.createStatement();
            ResultSet result = stmt.executeQuery(sql);
            while(result.next()){
                list.add(new User(result.getInt("id"),
                        result.getString("name"),
                        result.getString("lastName"),
                        result.getString("mail"),
                        result.getString("password"),
                        result.getDate("createdAt"),
                        result.getDate("updatedAt"),
                        result.getDate("deletedAt")
                ));
            }
            stmt.close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            if(cnn != null)
                cnn.close();
        }
    }

    public static void main(String[] args) {
        ConexionMySql conection = new ConexionMySql();
        try {
            conection.getUsers();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User getUser(String id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            createConexion();
            PreparedStatement stm = cnn.prepareStatement(sql);
            stm.setInt(1, Integer.parseInt(id));
            ResultSet result = stm.executeQuery();
            if(result.next())
            return new User(result.getString("name"), result.getString("lastName"));
        } catch (SQLException error) {
            error.printStackTrace();
        } finally {
            if (cnn != null)
                cnn.close();
        }
        return null;
    }

    public User getUserByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE mail = ?";
        try {
            createConexion();
            PreparedStatement stm = cnn.prepareStatement(sql);
            stm.setString(1, email);
            ResultSet result = stm.executeQuery();
            if(result.next())
                return new User(result.getString("name"), result.getString("lastName"));
        } catch (SQLException error) {
            error.printStackTrace();
        } finally {
            if (cnn != null)
                cnn.close();
        }
        return null;
    }
    
        public User insertUser(User user) throws SQLException {
        String sql = "INSERT INTO users (name, lastName, mail, password) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement stm = cnn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stm.setString(1, user.getName());
            stm.setString(2, user.getLastName());
            stm.setString(3, user.getEmail());
            stm.setString(4, user.getPassword());
            int affectedRows = stm.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            try (ResultSet generatedKeys = stm.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    user.setId(id);
                    return user;
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
