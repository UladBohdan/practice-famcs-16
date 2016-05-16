package by.bsu.famcs.uladbohdan;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

@WebServlet(value = "/login")
public class LoginServlet extends HttpServlet {

    private User[] users;
    private static final String USER_NOT_FOUND = "not found";

    @Override
    public void init() throws ServletException {
        super.init();
        try (Reader reader = new InputStreamReader(new FileInputStream(getUsersPath()))) {
            Gson gson = new GsonBuilder().create();
            users = gson.fromJson(reader, User[].class);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("IO Exception");
        } catch (Exception e) {
            System.out.println("Unknown exception:");
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String userId = isUserFound(username, password);
        if (!userId.equals(USER_NOT_FOUND)) {
            resp.sendRedirect("/homepage.html?uid=" + userId);
        } else {
            req.setAttribute("errorMsg", "Wrong username/password. Try again:");
            getServletContext().getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }

    private String isUserFound(String username, String password) {
        try {
            for (User user : users) {
                if (username.equals(user.getUsername()) &&
                        encryptPassword(password).equals(user.getPassword())) {
                    return user.getUid();
                }
            }
        } catch(NoSuchAlgorithmException e) {
            System.out.println("Algorithm was not found");
        } catch(UnsupportedEncodingException e) {
            System.out.println("Encoding is not supported");
        }
        return USER_NOT_FOUND;
    }

    private String encryptPassword(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String sha1;
        MessageDigest crypt = MessageDigest.getInstance("SHA-1");
        crypt.reset();
        crypt.update(password.getBytes("UTF-8"));
        Formatter formatter = new Formatter();
        for (byte b : crypt.digest()) {
            formatter.format("%02x", b);
        }
        sha1 = formatter.toString();
        formatter.close();
        return sha1;
    }

    private String getProjectPath() {
        String path = getServletContext().getRealPath("/");
        return path.substring(0, path.length() - 37);
    }

    private String getUsersPath() {
        return getProjectPath() + "data/users.json";
    }
}
