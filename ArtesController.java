package controller;

import dao.ArteDAO;
import dto.ArteDTO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

@WebServlet("/artes/*")
public class ArtesController extends HttpServlet {
    private ArteDAO arteDAO;
    private service.ArteService arteService;
    
    public void init() {
        arteDAO = new ArteDAO();
        arteService = new service.ArteService();
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getPathInfo();
        HttpSession session = request.getSession();
        String userRole = (String) session.getAttribute("userRole");
        
        try {
            if (action == null) action = "/usuario";
            
            switch (action) {
                case "/admin":
                    if ("admin".equals(userRole)) {
                        listarArtesAdmin(request, response);
                    } else {
                        response.sendRedirect("../index.html");
                    }
                    break;
                case "/usuario":
                    listarArtesUsuario(request, response);
                    break;
                case "/detalle":
                    mostrarDetalle(request, response);
                    break;
                case "/buscar":
                    buscarArtes(request, response);
                    break;
                case "/categorias":
                    listarCategorias(request, response);
                    break;
                default:
                    listarArtesUsuario(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("../error.html");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getPathInfo();
        HttpSession session = request.getSession();
        String userRole = (String) session.getAttribute("userRole");
        
        try {
            if (!"admin".equals(userRole)) {
                response.sendRedirect("../index.html");
                return;
            }
            
            switch (action) {
                case "/agregar":
                    agregarArte(request, response);
                    break;
                case "/actualizar":
                    actualizarArte(request, response);
                    break;
                case "/eliminar":
                    eliminarArte(request, response);
                    break;
                case "/reservar":
                    reservarArte(request, response);
                    break;
                default:
                    response.sendRedirect("../Admin/Artes_Admin.html");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("../error.html");
        }
    }
    
    private void listarArtesAdmin(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        List<ArteDTO> artes = arteService.obtenerTodasArtes();
        request.setAttribute("artes", artes);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/Admin/Artes_Admin.html");
        dispatcher.forward(request, response);
    }
    
    private void listarArtesUsuario(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        List<ArteDTO> artes = arteService.obtenerArtesActivos();
        request.setAttribute("artes", artes);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/Usuario/Artes_Usuario.html");
        dispatcher.forward(request, response);
    }
    
    private void mostrarDetalle(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String id = request.getParameter("id");
        ArteDTO arteDTO = arteService.obtenerArtePorId(id);
        
        if (arteDTO != null) {
            request.setAttribute("arte", arteDTO);
            
            HttpSession session = request.getSession();
            String userRole = (String) session.getAttribute("userRole");
            
            if ("admin".equals(userRole)) {
                RequestDispatcher dispatcher = request.getRequestDispatcher("/Admin/DetalleArte.html");
                dispatcher.forward(request, response);
            } else {
                RequestDispatcher dispatcher = request.getRequestDispatcher("/Usuario/DetalleArte.html");
                dispatcher.forward(request, response);
            }
        } else {
            response.sendRedirect("../Usuario/Artes_Usuario.html?error=Arte no encontrado");
        }
    }
    
    private void buscarArtes(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String query = request.getParameter("q");
        String categoria = request.getParameter("categoria");
        
        List<ArteDTO> resultados = arteService.obtenerArtesActivos();
        request.setAttribute("artes", resultados);
        request.setAttribute("query", query);
        request.setAttribute("categoriaSeleccionada", categoria);
        
        HttpSession session = request.getSession();
        String userRole = (String) session.getAttribute("userRole");
        
        if ("admin".equals(userRole)) {
            RequestDispatcher dispatcher = request.getRequestDispatcher("/Admin/Artes_Admin.html");
            dispatcher.forward(request, response);
        } else {
            RequestDispatcher dispatcher = request.getRequestDispatcher("/Usuario/Artes_Usuario.html");
            dispatcher.forward(request, response);
        }
    }
    
    private void listarCategorias(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/componentes/categorias.jsp");
        dispatcher.forward(request, response);
    }
    
    private void agregarArte(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        try {
            ArteDTO arteDTO = new ArteDTO();
            arteDTO.setTipo(request.getParameter("tipo"));
            arteDTO.setTitulo(request.getParameter("titulo"));
            arteDTO.setDescripcion(request.getParameter("descripcion"));
            arteDTO.setArtista(request.getParameter("artista"));
            arteDTO.setCategoria(request.getParameter("categoria"));
            arteDTO.setLugar(request.getParameter("lugar"));
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if (request.getParameter("fechaInicio") != null && !request.getParameter("fechaInicio").isEmpty()) {
                arteDTO.setFechaInicio(sdf.parse(request.getParameter("fechaInicio")));
            }
            if (request.getParameter("fechaFin") != null && !request.getParameter("fechaFin").isEmpty()) {
                arteDTO.setFechaFin(sdf.parse(request.getParameter("fechaFin")));
            }
            
            if (request.getParameter("precio") != null && !request.getParameter("precio").isEmpty()) {
                arteDTO.setPrecio(Double.parseDouble(request.getParameter("precio")));
            }
            
            if (request.getParameter("capacidad") != null && !request.getParameter("capacidad").isEmpty()) {
                arteDTO.setCapacidad(Integer.parseInt(request.getParameter("capacidad")));
                arteDTO.setDisponibles(arteDTO.getCapacidad());
            }
            
            boolean exito = arteService.crearArte(arteDTO);
            
            if (exito) {
                response.sendRedirect("../Admin/Artes_Admin.html?success=Arte agregado exitosamente");
            } else {
                response.sendRedirect("../Admin/Artes_Admin.html?error=Error al agregar el arte");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("../Admin/Artes_Admin.html?error=Error en los datos del formulario");
        }
    }
    
    private void actualizarArte(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        try {
            String id = request.getParameter("id");
            ArteDTO arteDTO = arteService.obtenerArtePorId(id);
            
            if (arteDTO != null) {
                arteDTO.setTitulo(request.getParameter("titulo"));
                arteDTO.setDescripcion(request.getParameter("descripcion"));
                arteDTO.setArtista(request.getParameter("artista"));
                arteDTO.setCategoria(request.getParameter("categoria"));
                arteDTO.setLugar(request.getParameter("lugar"));
                
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                if (request.getParameter("fechaInicio") != null && !request.getParameter("fechaInicio").isEmpty()) {
                    arteDTO.setFechaInicio(sdf.parse(request.getParameter("fechaInicio")));
                }
                if (request.getParameter("fechaFin") != null && !request.getParameter("fechaFin").isEmpty()) {
                    arteDTO.setFechaFin(sdf.parse(request.getParameter("fechaFin")));
                }
                
                boolean exito = arteService.actualizarArte(arteDTO);
                
                if (exito) {
                    response.sendRedirect("../Admin/Artes_Admin.html?success=Arte actualizado exitosamente");
                } else {
                    response.sendRedirect("../Admin/Artes_Admin.html?error=Error al actualizar el arte");
                }
            } else {
                response.sendRedirect("../Admin/Artes_Admin.html?error=Arte no encontrado");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("../Admin/Artes_Admin.html?error=Error en los datos del formulario");
        }
    }
    
    private void eliminarArte(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String id = request.getParameter("id");
        boolean exito = arteDAO.eliminar(id);
        
        if (exito) {
            response.sendRedirect("../Admin/Artes_Admin.html?success=Arte eliminado exitosamente");
        } else {
            response.sendRedirect("../Admin/Artes_Admin.html?error=Error al eliminar el arte");
        }
    }
    
    private void reservarArte(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        try {
            String id = request.getParameter("id");
            int cantidad = Integer.parseInt(request.getParameter("cantidad"));
            
            HttpSession session = request.getSession();
            String usuarioId = (String) session.getAttribute("usuarioId");
            
            boolean exito = arteDAO.reservar(id, cantidad, usuarioId);
            
            if (exito) {
                response.sendRedirect("../Usuario/MisEntradas.html?success=Reserva realizada exitosamente");
            } else {
                response.sendRedirect("../Usuario/Artes_Usuario.html?error=No hay disponibilidad suficiente");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("../Usuario/Artes_Usuario.html?error=Error en la reserva");
        }
    }
}