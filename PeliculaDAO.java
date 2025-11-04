package dao;

import model.Pelicula;
import util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PeliculaDAO {
    
    public List<Pelicula> obtenerTodas() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Pelicula> peliculas = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM peliculas WHERE estado = 'activa' ORDER BY fecha_estreno DESC";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Pelicula pelicula = mapearPelicula(rs);
                peliculas.add(pelicula);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResultSet(rs);
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }
        
        return peliculas;
    }
    
    public Pelicula obtenerPorId(String id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Pelicula pelicula = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM peliculas WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                pelicula = mapearPelicula(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResultSet(rs);
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }
        
        return pelicula;
    }
    
    public boolean insertar(Pelicula pelicula) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "INSERT INTO peliculas (id, titulo, director, fecha_estreno, hora_funcion, duracion, genero, clasificacion, sinopsis) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            
            pelicula.setId(UUID.randomUUID().toString());
            
            stmt.setString(1, pelicula.getId());
            stmt.setString(2, pelicula.getTitulo());
            stmt.setString(3, pelicula.getDirector());
            stmt.setDate(4, new java.sql.Date(pelicula.getFecha().getTime()));
            stmt.setString(5, pelicula.getHoraFuncion());
            stmt.setInt(6, pelicula.getDuracion());
            stmt.setString(7, pelicula.getGenero());
            stmt.setString(8, pelicula.getClasificacion());
            stmt.setString(9, pelicula.getSinopsis());
            
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
    
    public boolean actualizar(Pelicula pelicula) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "UPDATE peliculas SET titulo = ?, director = ?, fecha_estreno = ?, hora_funcion = ?, duracion = ?, genero = ?, clasificacion = ?, sinopsis = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, pelicula.getTitulo());
            stmt.setString(2, pelicula.getDirector());
            stmt.setDate(3, new java.sql.Date(pelicula.getFecha().getTime()));
            stmt.setString(4, pelicula.getHoraFuncion());
            stmt.setInt(5, pelicula.getDuracion());
            stmt.setString(6, pelicula.getGenero());
            stmt.setString(7, pelicula.getClasificacion());
            stmt.setString(8, pelicula.getSinopsis());
            stmt.setString(9, pelicula.getId());
            
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
            String sql = "UPDATE peliculas SET estado = 'inactiva' WHERE id = ?";
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
    
    public int contarPeliculas() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int count = 0;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT COUNT(*) as total FROM peliculas WHERE estado = 'activa'";
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
    
    private Pelicula mapearPelicula(ResultSet rs) throws SQLException {
        Pelicula pelicula = new Pelicula();
        pelicula.setId(rs.getString("id"));
        pelicula.setTitulo(rs.getString("titulo"));
        pelicula.setDirector(rs.getString("director"));
        pelicula.setFecha(rs.getDate("fecha_estreno"));
        pelicula.setHoraFuncion(rs.getString("hora_funcion"));
        pelicula.setDuracion(rs.getInt("duracion"));
        pelicula.setGenero(rs.getString("genero"));
        pelicula.setClasificacion(rs.getString("clasificacion"));
        pelicula.setSinopsis(rs.getString("sinopsis"));
        return pelicula;
    }
}