package by.bsu.famcs.uladbohdan;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@WebFilter(value = "/chat")
public class MessengerFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain)
            throws IOException, ServletException {
        String uidParam = servletRequest.getParameter("uid");
        if (uidParam != null) {
            boolean authenticated = checkAuthenticated(uidParam);
            if (authenticated) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else if (servletResponse instanceof HttpServletResponse) {
                ((HttpServletResponse) servletResponse).sendRedirect("/login.jsp");
            } else {
                servletResponse.getOutputStream().println("403, Forbidden");
            }
        }
    }

    private boolean checkAuthenticated(String uid) {
        return (Integer.parseInt(uid) >= 1 && Integer.parseInt(uid) <= 2);
    }

    @Override
    public void destroy() {

    }
}
