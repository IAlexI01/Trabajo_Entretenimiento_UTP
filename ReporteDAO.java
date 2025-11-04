package dao;

import model.Reporte;
import util.DatabaseUtil;
import java.sql.*;
import java.util.*;
import java.util.UUID;
import java.util.Date;

public class ReporteDAO {
    
    public List<Reporte> obtenerReportes() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Reporte> reportes = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM reportes ORDER BY fecha_generacion DESC";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Reporte reporte = mapearReporte(rs);
                reportes.add(reporte);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResultSet(rs);
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }
        
        return reportes;
    }
    
    public Reporte generarReporte(String tipo, Date fechaInicio, Date fechaFin, String usuario) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Reporte reporte = new Reporte(tipo, fechaInicio, fechaFin);
        
        try {
            conn = DatabaseUtil.getConnection();
            reporte.setId(UUID.randomUUID().toString());
            reporte.setGeneradoPor(usuario);
            
            String datos = "";
            switch (tipo) {
                case "ventas":
                    datos = generarReporteVentas(conn, fechaInicio, fechaFin);
                    break;
                case "asistencia":
                    datos = generarReporteAsistencia(conn, fechaInicio, fechaFin);
                    break;
                case "satisfaccion":
                    datos = generarReporteSatisfaccion(conn, fechaInicio, fechaFin);
                    break;
                case "popularidad":
                    datos = generarReportePopularidad(conn, fechaInicio, fechaFin);
                    break;
                default:
                    datos = "{\"error\": \"Tipo de reporte no válido: " + tipo + "\"}";
                    break;
            }
            
            reporte.setDatos(datos);
            guardarReporte(reporte);
        } catch (SQLException e) {
            e.printStackTrace();
            reporte.setDatos("{\"error\": \"Error al generar reporte: " + e.getMessage() + "\"}");
        } finally {
            DatabaseUtil.closeResultSet(rs);
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }
        
        return reporte;
    }
    
    public boolean guardarReporte(Reporte reporte) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "INSERT INTO reportes (id, tipo, fecha_inicio, fecha_fin, datos, fecha_generacion, generado_por) VALUES (?, ?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, reporte.getId());
            stmt.setString(2, reporte.getTipo());
            stmt.setDate(3, new java.sql.Date(reporte.getFechaInicio().getTime()));
            stmt.setDate(4, new java.sql.Date(reporte.getFechaFin().getTime()));
            stmt.setString(5, reporte.getDatos());
            stmt.setTimestamp(6, new java.sql.Timestamp(reporte.getFechaGeneracion().getTime()));
            stmt.setString(7, reporte.getGeneradoPor());
            
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }
    }
    
    public Reporte obtenerReportePorId(String id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Reporte reporte = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM reportes WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                reporte = mapearReporte(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResultSet(rs);
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }
        
        return reporte;
    }
    
    public boolean eliminarReporte(String id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "DELETE FROM reportes WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }
    }
    
    public Map<String, Object> obtenerEstadisticasGenerales() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Map<String, Object> stats = new HashMap<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT COUNT(*) as total_usuarios FROM usuarios";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            if (rs.next()) stats.put("usuariosRegistrados", rs.getInt("total_usuarios"));
            
            sql = "SELECT COUNT(*) as total_entradas, COALESCE(SUM(total), 0) as ingresos_totales FROM entradas WHERE estado = 'activa'";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            if (rs.next()) {
                stats.put("entradasVendidas", rs.getInt("total_entradas"));
                stats.put("ingresosTotales", rs.getDouble("ingresos_totales"));
            }
            
            sql = "SELECT COUNT(*) as eventos_activos FROM artes WHERE estado = 'activo'";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            if (rs.next()) stats.put("eventosActivos", rs.getInt("eventos_activos"));
            
            sql = "SELECT COUNT(*) as peliculas_activas FROM peliculas WHERE estado = 'activa'";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            if (rs.next()) stats.put("peliculasActivas", rs.getInt("peliculas_activas"));
            
            sql = "SELECT COUNT(*) as total_podcasts FROM podcasts";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            if (rs.next()) stats.put("totalPodcasts", rs.getInt("total_podcasts"));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResultSet(rs);
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }
        
        return stats;
    }
    
    public Map<String, Object> obtenerEstadisticasUsuario(String usuarioId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Map<String, Object> stats = new HashMap<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT COUNT(*) as entradas_compradas, COALESCE(SUM(total), 0) as total_gastado FROM entradas WHERE usuario_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, usuarioId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                stats.put("entradasCompradas", rs.getInt("entradas_compradas"));
                stats.put("totalGastado", rs.getDouble("total_gastado"));
            }
            
            sql = "SELECT fecha_registro FROM usuarios WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, usuarioId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                stats.put("miembroDesde", rs.getDate("fecha_registro"));
            }
            
            sql = "SELECT COUNT(DISTINCT evento_id) as eventos_asistidos FROM entradas WHERE usuario_id = ? AND estado = 'usada'";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, usuarioId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                stats.put("eventosAsistidos", rs.getInt("eventos_asistidos"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResultSet(rs);
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }
        
        return stats;
    }
    
    public Map<String, Double> obtenerVentasPorMes(int año) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Map<String, Double> ventasPorMes = new LinkedHashMap<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT MONTH(fecha_compra) as mes, SUM(total) as total FROM entradas WHERE YEAR(fecha_compra) = ? AND estado = 'activa' GROUP BY MONTH(fecha_compra) ORDER BY mes";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, año);
            rs = stmt.executeQuery();
            
            for (int i = 1; i <= 12; i++) {
                ventasPorMes.put(String.valueOf(i), 0.0);
            }
            
            while (rs.next()) {
                ventasPorMes.put(String.valueOf(rs.getInt("mes")), rs.getDouble("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResultSet(rs);
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }
        
        return ventasPorMes;
    }
    
    public List<Map<String, Object>> obtenerTopEventos(int limite) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Map<String, Object>> topEventos = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT evento_nombre, evento_tipo, COUNT(*) as total_entradas, SUM(total) as ingresos FROM entradas WHERE estado = 'activa' GROUP BY evento_nombre, evento_tipo ORDER BY ingresos DESC LIMIT ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, limite);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> evento = new HashMap<>();
                evento.put("nombre", rs.getString("evento_nombre"));
                evento.put("tipo", rs.getString("evento_tipo"));
                evento.put("totalEntradas", rs.getInt("total_entradas"));
                evento.put("ingresos", rs.getDouble("ingresos"));
                topEventos.add(evento);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResultSet(rs);
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }
        
        return topEventos;
    }
    
    private String generarReporteVentas(Connection conn, Date fechaInicio, Date fechaFin) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            String sql = "SELECT evento_tipo, SUM(total) as total_ventas, COUNT(*) as total_entradas FROM entradas WHERE fecha_compra BETWEEN ? AND ? AND estado = 'activa' GROUP BY evento_tipo";
            stmt = conn.prepareStatement(sql);
            stmt.setDate(1, new java.sql.Date(fechaInicio.getTime()));
            stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
            rs = stmt.executeQuery();
            
            StringBuilder datos = new StringBuilder();
            datos.append("{\"ventas_por_tipo\": [");
            boolean primero = true;
            while (rs.next()) {
                if (!primero) datos.append(",");
                datos.append("{\"tipo\": \"").append(rs.getString("evento_tipo"))
                     .append("\", \"total_ventas\": ").append(rs.getDouble("total_ventas"))
                     .append(", \"total_entradas\": ").append(rs.getInt("total_entradas"))
                     .append("}");
                primero = false;
            }
            datos.append("], \"fecha_inicio\": \"").append(fechaInicio)
                 .append("\", \"fecha_fin\": \"").append(fechaFin).append("\"}");
            return datos.toString();
        } finally {
            DatabaseUtil.closeResultSet(rs);
            DatabaseUtil.closeStatement(stmt);
        }
    }
    
    private String generarReporteAsistencia(Connection conn, Date fechaInicio, Date fechaFin) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            String sql = "SELECT COUNT(*) as total_entradas, SUM(CASE WHEN estado = 'usada' THEN 1 ELSE 0 END) as entradas_usadas, ROUND((SUM(CASE WHEN estado = 'usada' THEN 1 ELSE 0 END) * 100.0 / COUNT(*)), 2) as tasa_asistencia FROM entradas WHERE fecha_compra BETWEEN ? AND ?";
            stmt = conn.prepareStatement(sql);
            stmt.setDate(1, new java.sql.Date(fechaInicio.getTime()));
            stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return String.format("{\"asistencia_promedio\": %.2f, \"entradas_totales\": %d, \"entradas_usadas\": %d, \"fecha_inicio\": \"%s\", \"fecha_fin\": \"%s\"}", rs.getDouble("tasa_asistencia"), rs.getInt("total_entradas"), rs.getInt("entradas_usadas"), fechaInicio, fechaFin);
            }
            return "{\"error\": \"No se pudieron calcular las estadísticas de asistencia\"}";
        } finally {
            DatabaseUtil.closeResultSet(rs);
            DatabaseUtil.closeStatement(stmt);
        }
    }
    
    private String generarReporteSatisfaccion(Connection conn, Date fechaInicio, Date fechaFin) throws SQLException {
        Random random = new Random();
        double satisfaccionPromedio = 4.2 + (random.nextDouble() * 0.8);
        int comentariosPositivos = 120 + random.nextInt(50);
        int comentariosNegativos = 10 + random.nextInt(20);
        
        return String.format("{\"satisfaccion_promedio\": %.1f, \"comentarios_positivos\": %d, \"comentarios_negativos\": %d, \"fecha_inicio\": \"%s\", \"fecha_fin\": \"%s\"}", satisfaccionPromedio, comentariosPositivos, comentariosNegativos, fechaInicio, fechaFin);
    }
    
    private String generarReportePopularidad(Connection conn, Date fechaInicio, Date fechaFin) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            String sql = "SELECT evento_nombre, evento_tipo, COUNT(*) as total_reservas FROM entradas WHERE fecha_compra BETWEEN ? AND ? AND estado = 'activa' GROUP BY evento_nombre, evento_tipo ORDER BY total_reservas DESC LIMIT 10";
            stmt = conn.prepareStatement(sql);
            stmt.setDate(1, new java.sql.Date(fechaInicio.getTime()));
            stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
            rs = stmt.executeQuery();
            
            StringBuilder datos = new StringBuilder();
            datos.append("{\"eventos_populares\": [");
            boolean primero = true;
            while (rs.next()) {
                if (!primero) datos.append(",");
                datos.append("{\"nombre\": \"").append(rs.getString("evento_nombre"))
                     .append("\", \"tipo\": \"").append(rs.getString("evento_tipo"))
                     .append("\", \"reservas\": ").append(rs.getInt("total_reservas"))
                     .append("}");
                primero = false;
            }
            datos.append("], \"fecha_inicio\": \"").append(fechaInicio)
                 .append("\", \"fecha_fin\": \"").append(fechaFin).append("\"}");
            return datos.toString();
        } finally {
            DatabaseUtil.closeResultSet(rs);
            DatabaseUtil.closeStatement(stmt);
        }
    }
    
    private Reporte mapearReporte(ResultSet rs) throws SQLException {
        Reporte reporte = new Reporte();
        reporte.setId(rs.getString("id"));
        reporte.setTipo(rs.getString("tipo"));
        reporte.setFechaInicio(rs.getDate("fecha_inicio"));
        reporte.setFechaFin(rs.getDate("fecha_fin"));
        reporte.setDatos(rs.getString("datos"));
        reporte.setFechaGeneracion(rs.getTimestamp("fecha_generacion"));
        reporte.setGeneradoPor(rs.getString("generado_por"));
        return reporte;
    }
}
