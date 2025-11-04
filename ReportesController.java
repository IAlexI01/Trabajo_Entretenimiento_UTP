package controller;

import model.Reporte;
import dao.ReporteDAO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@WebServlet("/reportes/*")
public class ReportesController extends HttpServlet {
    private ReporteDAO reporteDAO;
    
    public void init() {
        reporteDAO = new ReporteDAO();
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getPathInfo();
        HttpSession session = request.getSession();
        String userRole = (String) session.getAttribute("userRole");
        
        try {
            if ("/admin".equals(action)) {
                mostrarReportesAdmin(request, response);
            } else if ("/usuario".equals(action)) {
                mostrarReportesUsuario(request, response);
            } else if ("/estadisticas".equals(action)) {
                mostrarEstadisticas(request, response, userRole);
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
            if ("/generar".equals(action)) {
                generarReporte(request, response);
            } else if ("/exportar".equals(action)) {
                exportarReporte(request, response);
            } else {
                response.sendRedirect("../Admin/Reportes_Admin.html");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void mostrarReportesAdmin(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        List<Reporte> reportes = reporteDAO.obtenerReportes();
        Map<String, Object> estadisticas = reporteDAO.obtenerEstadisticasGenerales();
        
        request.setAttribute("reportes", reportes);
        request.setAttribute("estadisticas", estadisticas);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/Admin/Reportes_Admin.html");
        dispatcher.forward(request, response);
    }
    
    private void mostrarReportesUsuario(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String usuarioId = (String) session.getAttribute("usuarioId");
        Map<String, Object> estadisticas = reporteDAO.obtenerEstadisticasUsuario(usuarioId);
        
        request.setAttribute("estadisticas", estadisticas);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/Usuario/Reportes_Usuario.html");
        dispatcher.forward(request, response);
    }
    
    private void mostrarEstadisticas(HttpServletRequest request, HttpServletResponse response, String userRole) 
            throws ServletException, IOException {
        
        Map<String, Object> estadisticas;
        
        if ("admin".equals(userRole)) {
            estadisticas = reporteDAO.obtenerEstadisticasGenerales();
            request.setAttribute("estadisticas", estadisticas);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/Admin/Reportes_Admin.html");
            dispatcher.forward(request, response);
        } else {
            String usuarioId = (String) request.getSession().getAttribute("usuarioId");
            estadisticas = reporteDAO.obtenerEstadisticasUsuario(usuarioId);
            request.setAttribute("estadisticas", estadisticas);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/Usuario/Reportes_Usuario.html");
            dispatcher.forward(request, response);
        }
    }
    
    private void generarReporte(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        try {
            String tipo = request.getParameter("tipo_reporte");
            String fechaInicioStr = request.getParameter("fecha_inicio");
            String fechaFinStr = request.getParameter("fecha_fin");
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaInicio = sdf.parse(fechaInicioStr);
            Date fechaFin = sdf.parse(fechaFinStr);
            
            HttpSession session = request.getSession();
            String usuario = (String) session.getAttribute("userName");
            
            Reporte reporte = reporteDAO.generarReporte(tipo, fechaInicio, fechaFin, usuario);
            boolean guardado = reporteDAO.guardarReporte(reporte);
            
            if (guardado) {
                response.sendRedirect("../Admin/Reportes_Admin.html?success=Reporte generado exitosamente");
            } else {
                response.sendRedirect("../Admin/Reportes_Admin.html?error=Error al generar el reporte");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("../Admin/Reportes_Admin.html?error=Error en el formato de fechas");
        }
    }
    
    private void exportarReporte(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String reporteId = request.getParameter("reporteId");
        String formato = request.getParameter("formato"); // "pdf", "excel", "csv"
        
        try {
            Reporte reporte = reporteDAO.obtenerReportePorId(reporteId);
            
            if (reporte != null) {
                // Configurar headers para descarga
                String nombreArchivo = "reporte_" + reporte.getTipo() + "_" + 
                                     new SimpleDateFormat("yyyyMMdd").format(new Date());
                
                switch (formato) {
                    case "pdf":
                        response.setContentType("application/pdf");
                        response.setHeader("Content-Disposition", 
                                         "attachment; filename=\"" + nombreArchivo + ".pdf\"");
                        break;
                    case "excel":
                        response.setContentType("application/vnd.ms-excel");
                        response.setHeader("Content-Disposition", 
                                         "attachment; filename=\"" + nombreArchivo + ".xlsx\"");
                        break;
                    case "csv":
                        response.setContentType("text/csv");
                        response.setHeader("Content-Disposition", 
                                         "attachment; filename=\"" + nombreArchivo + ".csv\"");
                        break;
                    default:
                        response.setContentType("text/plain");
                        response.setHeader("Content-Disposition", 
                                         "attachment; filename=\"" + nombreArchivo + ".txt\"");
                        break;
                }
                
                // En una implementación real, aquí generarías el archivo
                // Por ahora solo simulamos
                String contenido = "Reporte: " + reporte.getTipo() + "\n" +
                                 "Fecha: " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()) + "\n" +
                                 "Datos: " + reporte.getDatos();
                
                response.getWriter().write(contenido);
                
            } else {
                response.sendRedirect("../Admin/Reportes_Admin.html?error=Reporte no encontrado");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("../Admin/Reportes_Admin.html?error=Error al exportar el reporte");
        }
    }
    
    // Método auxiliar para obtener estadísticas en tiempo real
    private void obtenerEstadisticasTiempoReal(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // Simular datos en tiempo real
            stats.put("ventasHoy", 1250.00);
            stats.put("usuariosConectados", 45);
            stats.put("entradasVendidasHoy", 68);
            stats.put("eventosActivos", 12);
            
            // Convertir a JSON (en una implementación real usarías una librería JSON)
            String jsonResponse = "{" +
                "\"ventasHoy\": " + stats.get("ventasHoy") + "," +
                "\"usuariosConectados\": " + stats.get("usuariosConectados") + "," +
                "\"entradasVendidasHoy\": " + stats.get("entradasVendidasHoy") + "," +
                "\"eventosActivos\": " + stats.get("eventosActivos") +
                "}";
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(jsonResponse);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Error al obtener estadísticas\"}");
        }
    }
}