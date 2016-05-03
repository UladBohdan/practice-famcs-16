package by.bsu.famcs.uladbohdan;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MessengerFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain)
            throws IOException, ServletException {
        /*String uidParam = servletRequest.getParameter(MessengerServlet.PARAM_UID);
        if (uidParam == null && servletRequest instanceof HttpServletRequest ) {
            Cookie[] cookies = ((HttpServletRequest) servletRequest).getCookies();
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(MessengerServlet.COOKIE_USER_ID)) {
                    uidParam = cookie.getValue();
                }
            }
        }
        boolean authenticated = checkAuthenticated(uidParam);
        if (authenticated) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else if (servletResponse instanceof HttpServletResponse) {
            ((HttpServletResponse) servletResponse).sendRedirect("/unauthorized.html");
        } else {
            servletResponse.getOutputStream().println("403, Forbidden");
        }*/
    }

    /*private boolean checkAuthenticated(String uid) {
        return StaticKeyStorage.getUserByUid(uid) != null;
    }*/

    @Override
    public void destroy() {

    }
}
