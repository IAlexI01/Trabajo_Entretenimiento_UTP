package controller;

import dao.EntradaDAO;
import dto.EntradaDTO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/entradas/*")
public class EntradasController extends HttpServlet {
    private EntradaDAO entradaDAO;
    private service.EntradaService entradaService;
    
    public void init() {
        entradaDAO = new EntradaDAO();
        entradaService = new service.EntradaService();
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getPathInfo();
        HttpSession session = request.getSession();
        String userRole = (String) session.getAttribute("userRole");
        
        try {
            if ("/admin".equals(action)) {
                listarVentasAdmin(request, response);
            } else if ("/usuario".equals(action)) {
                listarEntradasUsuario(request, response);
            } else if ("/mis-entradas".equals(action)) {
                listarMisEntradas(request, response);
            } else {
                response.sendRedirect("../index.html");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getPathInfo();
        
        try {
            if ("/comprar".equals(action)) {
                comprarEntradas(request, response);
            } else if ("/procesar-venta".equals(action)) {
                procesarVenta(request, response);
            } else {
                response.sendRedirect("../index.html");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void listarVentasAdmin(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        List<EntradaDTO> ventas = entradaService.obtenerTodasEntradas();
        request.setAttribute("ventas", ventas);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/Admin/EntradasAdmin.html");
        dispatcher.forward(request, response);
    }
    
    private void listarEntradasUsuario(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        List<EntradaDTO> entradas = entradaService.obtenerTodasEntradas();
        request.setAttribute("entradas", entradas);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/Usuario/EntradasUsuario.html");
        dispatcher.forward(request, response);
    }
    
    private void listarMisEntradas(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String usuarioId = (String) session.getAttribute("usuarioId");
        
        List<EntradaDTO> misEntradas = entradaService.obtenerEntradasPorUsuario(usuarioId);
        request.setAttribute("misEntradas", misEntradas);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/Usuario/MisEntradas.html");
        dispatcher.forward(request, response);
    }
    
    private void comprarEntradas(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        response.sendRedirect("../Usuario/MisEntradas.html?success=Compra exitosa");
    }
    
    private void procesarVenta(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        response.sendRedirect("../Admin/EntradasAdmin.html?success=Venta procesada");
    }
}