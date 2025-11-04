package controller;

import model.Usuario;
import dao.UsuarioDAO;
import dto.UsuarioDTO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;

@WebServlet("/usuario/*")
public class UsuarioController extends HttpServlet {
    private UsuarioDAO usuarioDAO;
    private service.UsuarioService usuarioService;
    
    public void init() {
        usuarioDAO = new UsuarioDAO();
        usuarioService = new service.UsuarioService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getPathInfo();
        HttpSession session = request.getSession();
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        
        try {
            if (usuario == null) {
                response.sendRedirect("../index.html");
                return;
            }
            
            switch (action == null ? "/perfil" : action) {
                case "/perfil":
                    mostrarPerfil(request, response, usuario);
                    break;
                case "/editar":
                    mostrarFormularioEdicion(request, response, usuario);
                    break;
                default:
                    response.sendRedirect("../index.html");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("../error.html");
        }
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getPathInfo();
        HttpSession session = request.getSession();
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        
        try {
            if (usuario == null) {
                response.sendRedirect("../index.html");
                return;
            }
            
            switch (action) {
                case "/actualizar":
                    actualizarPerfil(request, response, usuario);
                    break;
                case "/cambiar-password":
                    cambiarPassword(request, response, usuario);
                    break;
                default:
                    response.sendRedirect("../index.html");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("../error.html");
        }
    }
    
    private void mostrarPerfil(HttpServletRequest request, HttpServletResponse response, Usuario usuario) 
            throws ServletException, IOException {
        
        UsuarioDTO usuarioDTO = usuarioService.obtenerUsuarioPorId(usuario.getId());
        request.setAttribute("totalEntradas", 5);
        request.setAttribute("totalFavoritos", 8);
        request.setAttribute("eventosAsistidos", 3);
        
        if ("admin".equals(usuario.getRol())) {
            RequestDispatcher dispatcher = request.getRequestDispatcher("/Admin/PerfilAdmin.html");
            dispatcher.forward(request, response);
        } else {
            RequestDispatcher dispatcher = request.getRequestDispatcher("/Usuario/PerfilUsuario.html");
            dispatcher.forward(request, response);
        }
    }
    
    private void mostrarFormularioEdicion(HttpServletRequest request, HttpServletResponse response, Usuario usuario) 
            throws ServletException, IOException {
        
        UsuarioDTO usuarioDTO = usuarioService.obtenerUsuarioPorId(usuario.getId());
        request.setAttribute("usuario", usuarioDTO);
        
        if ("admin".equals(usuario.getRol())) {
            RequestDispatcher dispatcher = request.getRequestDispatcher("/Admin/EditarPerfilAdmin.html");
            dispatcher.forward(request, response);
        } else {
            RequestDispatcher dispatcher = request.getRequestDispatcher("/Usuario/EditarPerfilUsuario.html");
            dispatcher.forward(request, response);
        }
    }
    
    private void actualizarPerfil(HttpServletRequest request, HttpServletResponse response, Usuario usuario) 
            throws IOException {
        
        String nombre = request.getParameter("nombre");
        String apellido = request.getParameter("apellido");
        String telefono = request.getParameter("telefono");
        
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId(usuario.getId());
        usuarioDTO.setNombre(nombre);
        usuarioDTO.setApellido(apellido);
        usuarioDTO.setTelefono(telefono);
        usuarioDTO.setEmail(usuario.getEmail());
        usuarioDTO.setRol(usuario.getRol());
        
        usuarioService.actualizarUsuario(usuarioDTO);
        
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setTelefono(telefono);
        
        HttpSession session = request.getSession();
        session.setAttribute("usuario", usuario);
        session.setAttribute("userName", usuario.getNombre());
        
        if ("admin".equals(usuario.getRol())) {
            response.sendRedirect("../Admin/PerfilAdmin.html?success=Perfil actualizado correctamente");
        } else {
            response.sendRedirect("../Usuario/PerfilUsuario.html?success=Perfil actualizado correctamente");
        }
    }
    
    private void cambiarPassword(HttpServletRequest request, HttpServletResponse response, Usuario usuario) 
            throws IOException {
        
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        
        if (!newPassword.equals(confirmPassword)) {
            if ("admin".equals(usuario.getRol())) {
                response.sendRedirect("../Admin/PerfilAdmin.html?error=Las contraseñas no coinciden");
            } else {
                response.sendRedirect("../Usuario/PerfilUsuario.html?error=Las contraseñas no coinciden");
            }
            return;
        }
        
        usuarioService.cambiarPassword(usuario.getId(), newPassword);
        
        if ("admin".equals(usuario.getRol())) {
            response.sendRedirect("../Admin/PerfilAdmin.html?success=Contraseña cambiada correctamente");
        } else {
            response.sendRedirect("../Usuario/PerfilUsuario.html?success=Contraseña cambiada correctamente");
        }
    }
}