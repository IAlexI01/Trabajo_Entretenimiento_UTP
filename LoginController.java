package controller;

import model.Usuario;
import dao.UsuarioDAO;
import dto.UsuarioDTO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginController extends HttpServlet {
    private UsuarioDAO usuarioDAO;
    private service.UsuarioService usuarioService;
    
    public void init() {
        usuarioDAO = new UsuarioDAO();
        usuarioService = new service.UsuarioService();
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        try {
            UsuarioDTO usuarioDTO = usuarioService.validarUsuario(email, password);
            
            if (usuarioDTO != null) {
                Usuario usuario = usuarioDTO.toEntity();
                HttpSession session = request.getSession();
                session.setAttribute("usuario", usuario);
                session.setAttribute("userName", usuario.getNombre() != null ? usuario.getNombre() : usuario.getEmail());
                
                if ("admin".equals(usuario.getRol())) {
                    response.sendRedirect("../Admin/DashboardAdmin.html");
                } else {
                    response.sendRedirect("../Usuario/DashboardUsuario.html");
                }
            } else {
                request.setAttribute("errorMessage", "❌ Credenciales incorrectas. Intenta de nuevo.");
                RequestDispatcher dispatcher = request.getRequestDispatcher("/index.html");
                dispatcher.forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error en el sistema. Intenta más tarde.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/index.html");
            dispatcher.forward(request, response);
        }
    }
}