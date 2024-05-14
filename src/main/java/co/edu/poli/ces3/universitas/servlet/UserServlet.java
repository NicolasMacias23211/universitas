package co.edu.poli.ces3.universitas.servlet;

import co.edu.poli.ces3.universitas.dao.User;
import co.edu.poli.ces3.universitas.database.ConexionMySql;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "userServlet", value = "/user")
public class UserServlet extends MyServlet {
    private ConexionMySql cnn;
    private GsonBuilder gsonBuilder;
    private Gson gson;
    public void init() {
        cnn = new ConexionMySql();
        gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        // Hello
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>PUT</h1>");
        out.println("</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        if(req.getParameter("mail") != null) {
            String email = req.getParameter("mail");
            User listUsers = null;
            try {
                listUsers = cnn.getUserByEmail(email);
            } catch (SQLException ex) {
                Logger.getLogger(UserServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (listUsers == null || listUsers.getName() == null) {
                String name = req.getParameter("name");
                if (name != null) {
                    listUsers.setName(name);
                } else {
                    try {
                       throw new Exception("El parámetro 'name' es nulo.");
                    } catch (Exception ex) {
                       Logger.getLogger(UserServlet.class.getName()).log(Level.SEVERE, null, ex);
                    }
                   return ;
                }
                String lastname = req.getParameter("lastName");
                if (lastname != null) {
                    listUsers.setLastName(lastname);
                } else {
                    try {
                       throw new Exception("El parámetro 'lastName' es nulo.");
                    } catch (Exception ex) {
                       Logger.getLogger(UserServlet.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return ;
                }
                String mail = req.getParameter("mail");
                if (mail != null) {
                    listUsers.setEmail(mail);
                } else {
                    try {
                       throw new Exception("El parámetro 'mail' es nulo.");
                    } catch (Exception ex) {
                       Logger.getLogger(UserServlet.class.getName()).log(Level.SEVERE, null, ex);
                    }
                   return ;
                }
                String password = req.getParameter("password");
                if (password != null) {
                    listUsers.setPassword(password);
                } else {
                    try {
                       throw new Exception("El parámetro 'password' es nulo.");
                    } catch (Exception ex) {
                       Logger.getLogger(UserServlet.class.getName()).log(Level.SEVERE, null, ex);
                    }
                   return ;
                }
                try {
                    User NewUser = cnn.insertUser(listUsers);
                    out.print(gson.toJson(NewUser));
                } catch (SQLException ex) {
                    Logger.getLogger(UserServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
                out.print(gson.toJson(listUsers));
            }
        }
         out.flush();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        try {
            PrintWriter out = response.getWriter();
            if(request.getParameter("id") == null) {
                ArrayList<User> listUsers = (ArrayList<User>) cnn.getUsers();
                out.print(gson.toJson(listUsers));
            }else{
                User user = cnn.getUser(request.getParameter("id"));
                out.print(gson.toJson(user));
            }
            out.flush();
        } catch (SQLException e) {
            System.out.println("error");
            throw new RuntimeException(e);
        }


    }

    public void destroy() {
    }

    @Override
    void saludar() {

    }
}