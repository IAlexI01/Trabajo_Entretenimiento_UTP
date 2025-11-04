package dao;

import model.Exposicion;
import util.DatabaseUtil;
import java.sql.*;
import java.util.*;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
public class ExposicionDAO {
    
    public List<Exposicion> obtenerTodas() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Exposicion> exposiciones = new ArrayList<>();
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM exposiciones ORDER BY fecha_inicio DESC";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Exposicion exposicion = mapearExposicion(rs);
                exposiciones.add(exposicion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResultSet(rs);
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }
        return exposiciones;
    }
    
    public List<Exposicion> obtenerActivas() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Exposicion> exposiciones = new ArrayList<>();
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM exposiciones WHERE fecha_fin >= CURDATE() ORDER BY fecha_inicio ASC";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Exposicion exposicion = mapearExposicion(rs);
                exposiciones.add(exposicion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResultSet(rs);
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }
        return exposiciones;
    }
    
    public List<Exposicion> obtenerProximas() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Exposicion> exposiciones = new ArrayList<>();
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM exposiciones WHERE fecha_inicio > CURDATE() ORDER BY fecha_inicio ASC";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Exposicion exposicion = mapearExposicion(rs);
                exposiciones.add(exposicion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResultSet(rs);
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }
        return exposiciones;
    }
    
    public Exposicion obtenerPorId(String id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Exposicion exposicion = null;
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM exposiciones WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                exposicion = mapearExposicion(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResultSet(rs);
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }
        return exposicion;
    }
    
    public List<Exposicion> buscarPorTipo(String tipo) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Exposicion> exposiciones = new ArrayList<>();
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM exposiciones WHERE tipo_arte = ? ORDER BY fecha_inicio DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, tipo);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Exposicion exposicion = mapearExposicion(rs);
                exposiciones.add(exposicion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResultSet(rs);
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }
        return exposiciones;
    }
    
    public List<Exposicion> buscarPorArtista(String artista) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Exposicion> exposiciones = new ArrayList<>();
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM exposiciones WHERE artista LIKE ? ORDER BY fecha_inicio DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + artista + "%");
            rs = stmt.executeQuery();
            while (rs.next()) {
                Exposicion exposicion = mapearExposicion(rs);
                exposiciones.add(exposicion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResultSet(rs);
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }
        return exposiciones;
    }
    
    public boolean insertar(Exposicion exposicion) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "INSERT INTO exposiciones (id, nombre, artista, fecha_inicio, fecha_fin, lugar, tipo_arte, descripcion, imagen_url, precio_entrada, capacidad_maxima) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            exposicion.setId(UUID.randomUUID().toString());
            stmt.setString(1, exposicion.getId());
            stmt.setString(2, exposicion.getNombre());
            stmt.setString(3, exposicion.getArtista());
            stmt.setDate(4, new java.sql.Date(exposicion.getFechaInicio().getTime()));
            stmt.setDate(5, new java.sql.Date(exposicion.getFechaFin().getTime()));
            stmt.setString(6, exposicion.getLugar());
            stmt.setString(7, exposicion.getTipoArte());
            stmt.setString(8, exposicion.getDescripcion());
            stmt.setString(9, exposicion.getImagenUrl());
            stmt.setDouble(10, exposicion.getPrecioEntrada());
            stmt.setInt(11, exposicion.getCapacidadMaxima());
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
    
    public boolean actualizar(Exposicion exposicion) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "UPDATE exposiciones SET nombre = ?, artista = ?, fecha_inicio = ?, fecha_fin = ?, lugar = ?, tipo_arte = ?, descripcion = ?, imagen_url = ?, precio_entrada = ?, capacidad_maxima = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, exposicion.getNombre());
            stmt.setString(2, exposicion.getArtista());
            stmt.setDate(3, new java.sql.Date(exposicion.getFechaInicio().getTime()));
            stmt.setDate(4, new java.sql.Date(exposicion.getFechaFin().getTime()));
            stmt.setString(5, exposicion.getLugar());
            stmt.setString(6, exposicion.getTipoArte());
            stmt.setString(7, exposicion.getDescripcion());
            stmt.setString(8, exposicion.getImagenUrl());
            stmt.setDouble(9, exposicion.getPrecioEntrada());
            stmt.setInt(10, exposicion.getCapacidadMaxima());
            stmt.setString(11, exposicion.getId());
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
            String sql = "DELETE FROM exposiciones WHERE id = ?";
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
    
    public List<String> obtenerTiposArte() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<String> tipos = new ArrayList<>();
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT DISTINCT tipo_arte FROM exposiciones WHERE tipo_arte IS NOT NULL ORDER BY tipo_arte";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                tipos.add(rs.getString("tipo_arte"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResultSet(rs);
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }
        return tipos;
    }
    
    public List<String> obtenerArtistas() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<String> artistas = new ArrayList<>();
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT DISTINCT artista FROM exposiciones WHERE artista IS NOT NULL ORDER BY artista";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                artistas.add(rs.getString("artista"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResultSet(rs);
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }
        return artistas;
    }
    
    public int contarExposicionesActivas() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int count = 0;
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT COUNT(*) as total FROM exposiciones WHERE fecha_fin >= CURDATE()";
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
    
    public Map<String, Object> obtenerEstadisticas() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Map<String, Object> stats = new HashMap<>();
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT COUNT(*) as total FROM exposiciones";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            if (rs.next()) stats.put("totalExposiciones", rs.getInt("total"));
            sql = "SELECT COUNT(*) as activas FROM exposiciones WHERE fecha_fin >= CURDATE()";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            if (rs.next()) stats.put("exposicionesActivas", rs.getInt("activas"));
            sql = "SELECT COUNT(*) as proximas FROM exposiciones WHERE fecha_inicio > CURDATE()";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            if (rs.next()) stats.put("exposicionesProximas", rs.getInt("proximas"));
            sql = "SELECT tipo_arte, COUNT(*) as cantidad FROM exposiciones GROUP BY tipo_arte ORDER BY cantidad DESC LIMIT 5";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            List<Map<String, Object>> tiposPopulares = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> tipo = new HashMap<>();
                tipo.put("tipo", rs.getString("tipo_arte"));
                tipo.put("cantidad", rs.getInt("cantidad"));
                tiposPopulares.add(tipo);
            }
            stats.put("tiposPopulares", tiposPopulares);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResultSet(rs);
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }
        return stats;
    }
    
    public List<Exposicion> obtenerExposicionesPorRangoFechas(Date fechaInicio, Date fechaFin) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Exposicion> exposiciones = new ArrayList<>();
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM exposiciones WHERE fecha_inicio >= ? AND fecha_fin <= ? ORDER BY fecha_inicio ASC";
            stmt = conn.prepareStatement(sql);
            stmt.setDate(1, new java.sql.Date(fechaInicio.getTime()));
            stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
            rs = stmt.executeQuery();
            while (rs.next()) {
                Exposicion exposicion = mapearExposicion(rs);
                exposiciones.add(exposicion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResultSet(rs);
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }
        return exposiciones;
    }
    
    private Exposicion mapearExposicion(ResultSet rs) throws SQLException {
        Exposicion exposicion = new Exposicion();
        exposicion.setId(rs.getString("id"));
        exposicion.setNombre(rs.getString("nombre"));
        exposicion.setArtista(rs.getString("artista"));
        exposicion.setFechaInicio(rs.getDate("fecha_inicio"));
        exposicion.setFechaFin(rs.getDate("fecha_fin"));
        exposicion.setLugar(rs.getString("lugar"));
        exposicion.setTipoArte(rs.getString("tipo_arte"));
        exposicion.setDescripcion(rs.getString("descripcion"));
        exposicion.setImagenUrl(rs.getString("imagen_url"));
        exposicion.setPrecioEntrada(rs.getDouble("precio_entrada"));
        exposicion.setCapacidadMaxima(rs.getInt("capacidad_maxima"));
        return exposicion;
    }
}
