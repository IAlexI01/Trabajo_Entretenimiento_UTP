package dao;

import model.Arte;
import util.DatabaseUtil;
import java.sql.*;
import java.util.*;
import java.util.UUID;

public class ArteDAO {
    
    public List<Arte> obtenerTodos() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Arte> artes = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM artes ORDER BY fecha_creacion DESC";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Arte arte = mapearArte(rs);
                artes.add(arte);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResultSet(rs);
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }
        
        return artes;
    }
    
    public List<Arte> obtenerActivos() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Arte> artes = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM artes WHERE estado IN ('activo', 'proximamente') ORDER BY fecha_inicio ASC";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Arte arte = mapearArte(rs);
                artes.add(arte);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResultSet(rs);
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }
        
        return artes;
    }
    
    public Arte obtenerPorId(String id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Arte arte = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM artes WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                arte = mapearArte(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResultSet(rs);
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }
        
        return arte;
    }
    
    public List<Arte> buscar(String query, String categoria) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Arte> resultados = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            StringBuilder sql = new StringBuilder("SELECT * FROM artes WHERE estado IN ('activo', 'proximamente')");
            
            List<String> parametros = new ArrayList<>();
            
            if (query != null && !query.trim().isEmpty()) {
                sql.append(" AND (titulo LIKE ? OR descripcion LIKE ? OR artista LIKE ?)");
                String likeQuery = "%" + query + "%";
                parametros.add(likeQuery);
                parametros.add(likeQuery);
                parametros.add(likeQuery);
            }
            
            if (categoria != null && !categoria.trim().isEmpty() && !"todos".equals(categoria)) {
                sql.append(" AND categoria = ?");
                parametros.add(categoria);
            }
            
            sql.append(" ORDER BY fecha_inicio ASC");
            
            stmt = conn.prepareStatement(sql.toString());
            
            for (int i = 0; i < parametros.size(); i++) {
                stmt.setString(i + 1, parametros.get(i));
            }
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Arte arte = mapearArte(rs);
                resultados.add(arte);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResultSet(rs);
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }
        
        return resultados;
    }
    
    public boolean insertar(Arte arte) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "INSERT INTO artes (id, tipo, titulo, descripcion, artista, fecha_inicio, fecha_fin, lugar, categoria, precio, capacidad, disponibles, imagen, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            
            arte.setId(UUID.randomUUID().toString());
            
            stmt.setString(1, arte.getId());
            stmt.setString(2, arte.getTipo());
            stmt.setString(3, arte.getTitulo());
            stmt.setString(4, arte.getDescripcion());
            stmt.setString(5, arte.getArtista());
            stmt.setDate(6, arte.getFechaInicio() != null ? new java.sql.Date(arte.getFechaInicio().getTime()) : null);
            stmt.setDate(7, arte.getFechaFin() != null ? new java.sql.Date(arte.getFechaFin().getTime()) : null);
            stmt.setString(8, arte.getLugar());
            stmt.setString(9, arte.getCategoria());
            stmt.setDouble(10, arte.getPrecio());
            stmt.setInt(11, arte.getCapacidad());
            stmt.setInt(12, arte.getCapacidad()); // Inicialmente todos disponibles
            stmt.setString(13, arte.getImagen());
            stmt.setString(14, arte.getEstado());
            
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
    
    public boolean actualizar(Arte arte) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "UPDATE artes SET titulo = ?, descripcion = ?, artista = ?, fecha_inicio = ?, fecha_fin = ?, lugar = ?, categoria = ?, precio = ?, capacidad = ?, imagen = ?, estado = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, arte.getTitulo());
            stmt.setString(2, arte.getDescripcion());
            stmt.setString(3, arte.getArtista());
            stmt.setDate(4, arte.getFechaInicio() != null ? new java.sql.Date(arte.getFechaInicio().getTime()) : null);
            stmt.setDate(5, arte.getFechaFin() != null ? new java.sql.Date(arte.getFechaFin().getTime()) : null);
            stmt.setString(6, arte.getLugar());
            stmt.setString(7, arte.getCategoria());
            stmt.setDouble(8, arte.getPrecio());
            stmt.setInt(9, arte.getCapacidad());
            stmt.setString(10, arte.getImagen());
            stmt.setString(11, arte.getEstado());
            stmt.setString(12, arte.getId());
            
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
    
    public boolean eliminar(String id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "UPDATE artes SET estado = 'finalizado' WHERE id = ?";
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
    
    public boolean reservar(String arteId, int cantidad, String usuarioId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            
            // Verificar disponibilidad
            String sqlCheck = "SELECT disponibles FROM artes WHERE id = ? AND estado = 'activo'";
            stmt = conn.prepareStatement(sqlCheck);
            stmt.setString(1, arteId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int disponibles = rs.getInt("disponibles");
                if (disponibles >= cantidad) {
                    // Actualizar disponibilidad
                    String sqlUpdate = "UPDATE artes SET disponibles = disponibles - ? WHERE id = ?";
                    stmt = conn.prepareStatement(sqlUpdate);
                    stmt.setInt(1, cantidad);
                    stmt.setString(2, arteId);
                    
                    int filasAfectadas = stmt.executeUpdate();
                    
                    if (filasAfectadas > 0) {
                        // Crear entrada/reserva
                        crearEntrada(conn, arteId, usuarioId, cantidad);
                        return true;
                    }
                }
            }
            
            return false;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }
    }
    
    private void crearEntrada(Connection conn, String arteId, String usuarioId, int cantidad) throws SQLException {
        PreparedStatement stmt = null;
        
        try {
            Arte arte = obtenerPorId(arteId);
            double total = arte.getPrecio() * cantidad;
            
            String sql = "INSERT INTO entradas (id, evento_id, evento_tipo, evento_nombre, usuario_id, cantidad, total, estado) VALUES (?, ?, 'arte', ?, ?, ?, ?, 'activa')";
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, UUID.randomUUID().toString());
            stmt.setString(2, arteId);
            stmt.setString(3, arte.getTitulo());
            stmt.setString(4, usuarioId);
            stmt.setInt(5, cantidad);
            stmt.setDouble(6, total);
            
            stmt.executeUpdate();
            
        } finally {
            DatabaseUtil.closeStatement(stmt);
        }
    }
    
    public List<String> obtenerCategorias() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<String> categorias = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT DISTINCT categoria FROM artes WHERE categoria IS NOT NULL ORDER BY categoria";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                categorias.add(rs.getString("categoria"));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResultSet(rs);
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }
        
        return categorias;
    }
    
    public Map<String, Integer> obtenerEstadisticas() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Map<String, Integer> stats = new HashMap<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            
            // Contar por estado
            String sql = "SELECT estado, COUNT(*) as count FROM artes GROUP BY estado";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                stats.put(rs.getString("estado"), rs.getInt("count"));
            }
            
            // Total capacidad y reservas
            sql = "SELECT SUM(capacidad) as total_capacidad, SUM(capacidad - disponibles) as total_reservas FROM artes";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                stats.put("totalCapacidad", rs.getInt("total_capacidad"));
                stats.put("totalReservas", rs.getInt("total_reservas"));
                stats.put("disponibles", rs.getInt("total_capacidad") - rs.getInt("total_reservas"));
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
    
    private Arte mapearArte(ResultSet rs) throws SQLException {
        Arte arte = new Arte();
        arte.setId(rs.getString("id"));
        arte.setTipo(rs.getString("tipo"));
        arte.setTitulo(rs.getString("titulo"));
        arte.setDescripcion(rs.getString("descripcion"));
        arte.setArtista(rs.getString("artista"));
        arte.setFechaInicio(rs.getDate("fecha_inicio"));
        arte.setFechaFin(rs.getDate("fecha_fin"));
        arte.setLugar(rs.getString("lugar"));
        arte.setCategoria(rs.getString("categoria"));
        arte.setPrecio(rs.getDouble("precio"));
        arte.setCapacidad(rs.getInt("capacidad"));
        arte.setDisponibles(rs.getInt("disponibles"));
        arte.setImagen(rs.getString("imagen"));
        arte.setEstado(rs.getString("estado"));
        return arte;
    }
}