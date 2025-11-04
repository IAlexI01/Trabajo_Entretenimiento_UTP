package dao;

import model.Entrada;
import util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.UUID;

public class EntradaDAO {
    
    public List<Entrada> obtenerTodas() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Entrada> entradas = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT e.*, u.nombre as usuario_nombre FROM entradas e " +
                        "LEFT JOIN usuarios u ON e.usuario_id = u.id " +
                        "ORDER BY e.fecha_compra DESC";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Entrada entrada = mapearEntrada(rs);
                entradas.add(entrada);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResultSet(rs);
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }
        
        return entradas;
    }
    
    public List<Entrada> obtenerPorUsuario(String usuarioId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Entrada> entradas = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM entradas WHERE usuario_id = ? ORDER BY fecha_compra DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, usuarioId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Entrada entrada = mapearEntrada(rs);
                entradas.add(entrada);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResultSet(rs);
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }
        
        return entradas;
    }
    
    public Entrada obtenerPorId(String id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Entrada entrada = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT e.*, u.nombre as usuario_nombre FROM entradas e " +
                        "LEFT JOIN usuarios u ON e.usuario_id = u.id " +
                        "WHERE e.id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                entrada = mapearEntrada(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResultSet(rs);
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }
        
        return entrada;
    }
    
    public boolean insertar(Entrada entrada) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "INSERT INTO entradas (id, evento_id, evento_tipo, evento_nombre, usuario_id, cantidad, total, estado, codigo_qr) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            
            entrada.setId(UUID.randomUUID().toString());
            
            stmt.setString(1, entrada.getId());
            stmt.setString(2, entrada.getEventoId());
            stmt.setString(3, entrada.getEventoTipo());
            stmt.setString(4, entrada.getEventoNombre());
            stmt.setString(5, entrada.getUsuarioId());
            stmt.setInt(6, entrada.getCantidad());
            stmt.setDouble(7, entrada.getTotal());
            stmt.setString(8, entrada.getEstado());
            stmt.setString(9, generarCodigoQR(entrada.getId()));
            
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
    
    public boolean actualizarEstado(String entradaId, String nuevoEstado) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "UPDATE entradas SET estado = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, nuevoEstado);
            stmt.setString(2, entradaId);
            
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
    
    public double obtenerTotalVentas() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        double total = 0;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT SUM(total) as total_ventas FROM entradas WHERE estado = 'activa'";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                total = rs.getDouble("total_ventas");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResultSet(rs);
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }
        
        return total;
    }
    
    public int contarEntradasVendidas() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int count = 0;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT COUNT(*) as total FROM entradas WHERE estado = 'activa'";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                count = rs.getInt("total");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResultSet(rs);
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }
        
        return count;
    }
    
    public Map<String, Double> obtenerVentasPorTipo() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Map<String, Double> ventas = new HashMap<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT evento_tipo, SUM(total) as total FROM entradas WHERE estado = 'activa' GROUP BY evento_tipo";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                ventas.put(rs.getString("evento_tipo"), rs.getDouble("total"));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResultSet(rs);
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }
        
        return ventas;
    }
    
    private Entrada mapearEntrada(ResultSet rs) throws SQLException {
        Entrada entrada = new Entrada();
        entrada.setId(rs.getString("id"));
        entrada.setEventoId(rs.getString("evento_id"));
        entrada.setEventoNombre(rs.getString("evento_nombre"));
        entrada.setUsuarioId(rs.getString("usuario_id"));
        entrada.setCantidad(rs.getInt("cantidad"));
        entrada.setTotal(rs.getDouble("total"));
        entrada.setFechaCompra(rs.getTimestamp("fecha_compra"));
        entrada.setEstado(rs.getString("estado"));
        return entrada;
    }
    
    private String generarCodigoQR(String entradaId) {
        return "QR-" + entradaId + "-" + System.currentTimeMillis();
    }
}