package controller;

import dao.PodcastDAO;
import dto.PodcastDTO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/podcast/*")
public class PodcastController extends HttpServlet {
    private PodcastDAO podcastDAO;
    private service.PodcastService podcastService;
    
    public void init() {
        podcastDAO = new PodcastDAO();
        podcastService = new service.PodcastService();
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getPathInfo();
        
        try {
            if ("/admin".equals(action)) {
                listarPodcastsAdmin(request, response);
            } else if ("/usuario".equals(action)) {
                listarPodcastsUsuario(request, response);
            } else {
                listarPodcastsUsuario(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getPathInfo();
        
        try {
            if ("/agregar".equals(action)) {
                agregarPodcast(request, response);
            } else {
                response.sendRedirect("../Admin/Podcast_Admin.html");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void listarPodcastsAdmin(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        List<PodcastDTO> podcasts = podcastService.obtenerTodosPodcasts();
        request.setAttribute("podcasts", podcasts);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/Admin/Podcast_Admin.html");
        dispatcher.forward(request, response);
    }
    
    private void listarPodcastsUsuario(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        List<PodcastDTO> podcasts = podcastService.obtenerTodosPodcasts();
        request.setAttribute("podcasts", podcasts);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/Usuario/Podcast_Usuario.html");
        dispatcher.forward(request, response);
    }
    
    private void agregarPodcast(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        PodcastDTO podcastDTO = new PodcastDTO();
        podcastDTO.setTitulo(request.getParameter("titulo"));
        podcastDTO.setAnfitrion(request.getParameter("anfitrion"));
        
        podcastService.crearPodcast(podcastDTO);
        response.sendRedirect("../Admin/Podcast_Admin.html?success=Podcast agregado");
    }
}