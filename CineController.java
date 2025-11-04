package controller;

import dao.PeliculaDAO;
import dto.PeliculaDTO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/cine/*")
public class CineController extends HttpServlet {
    private PeliculaDAO peliculaDAO;
    private service.CineService cineService;
    
    public void init() {
        peliculaDAO = new PeliculaDAO();
        cineService = new service.CineService();
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getPathInfo();
        
        try {
            switch (action == null ? "list" : action) {
                case "/admin":
                    listarPeliculasAdmin(request, response);
                    break;
                case "/usuario":
                    listarPeliculasUsuario(request, response);
                    break;
                default:
                    listarPeliculasUsuario(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getPathInfo();
        
        try {
            switch (action) {
                case "/agregar":
                    agregarPelicula(request, response);
                    break;
                case "/actualizar":
                    actualizarPelicula(request, response);
                    break;
                case "/eliminar":
                    eliminarPelicula(request, response);
                    break;
                default:
                    response.sendRedirect("../Admin/CineAdmin.html");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void listarPeliculasAdmin(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        List<PeliculaDTO> peliculas = cineService.obtenerTodasPeliculas();
        request.setAttribute("peliculas", peliculas);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/Admin/CineAdmin.html");
        dispatcher.forward(request, response);
    }
    
    private void listarPeliculasUsuario(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        List<PeliculaDTO> peliculas = cineService.obtenerPeliculasActivas();
        request.setAttribute("peliculas", peliculas);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/Usuario/CineUsuario.html");
        dispatcher.forward(request, response);
    }
    
    private void agregarPelicula(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        PeliculaDTO peliculaDTO = new PeliculaDTO();
        peliculaDTO.setTitulo(request.getParameter("pelicula"));
        peliculaDTO.setDirector(request.getParameter("director"));
        
        cineService.crearPelicula(peliculaDTO);
        response.sendRedirect("../Admin/CineAdmin.html?success=Película agregada correctamente");
    }
    
    private void actualizarPelicula(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        response.sendRedirect("../Admin/CineAdmin.html?success=Película actualizada");
    }
    
    private void eliminarPelicula(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String id = request.getParameter("id");
        cineService.eliminarPelicula(id);
        response.sendRedirect("../Admin/CineAdmin.html?success=Película eliminada");
    }
}