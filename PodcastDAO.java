package dao;

import model.Podcast;
import util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PodcastDAO {
    
    public List<Podcast> obtenerTodos() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Podcast> podcasts = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM podcasts ORDER BY fecha_publicacion DESC";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Podcast podcast = mapearPodcast(rs);
                podcasts.add(podcast);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResultSet(rs);
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }
        
        return podcasts;
    }
    
    public Podcast obtenerPorId(String id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Podcast podcast = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM podcasts WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                podcast = mapearPodcast(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResultSet(rs);
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }
        
        return podcast;
    }
    
    public List<Podcast> obtenerPorCategoria(String categoria) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Podcast> podcasts = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM podcasts WHERE categoria = ? ORDER BY fecha_publicacion DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, categoria);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Podcast podcast = mapearPodcast(rs);
                podcasts.add(podcast);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResultSet(rs);
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }
        
        return podcasts;
    }
    
    public boolean insertar(Podcast podcast) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "INSERT INTO podcasts (id, titulo, anfitrion, fecha_publicacion, duracion, categoria, descripcion, archivo_audio) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            
            podcast.setId(UUID.randomUUID().toString());
            
            stmt.setString(1, podcast.getId());
            stmt.setString(2, podcast.getTitulo());
            stmt.setString(3, podcast.getAnfitrion());
            stmt.setDate(4, new java.sql.Date(podcast.getFechaPublicacion().getTime()));
            stmt.setInt(5, podcast.getDuracion());
            stmt.setString(6, podcast.getCategoria());
            stmt.setString(7, podcast.getDescripcion());
            stmt.setString(8, podcast.getArchivoAudio());
            
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
    
    public boolean actualizar(Podcast podcast) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "UPDATE podcasts SET titulo = ?, anfitrion = ?, fecha_publicacion = ?, duracion = ?, categoria = ?, descripcion = ?, archivo_audio = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, podcast.getTitulo());
            stmt.setString(2, podcast.getAnfitrion());
            stmt.setDate(3, new java.sql.Date(podcast.getFechaPublicacion().getTime()));
            stmt.setInt(4, podcast.getDuracion());
            stmt.setString(5, podcast.getCategoria());
            stmt.setString(6, podcast.getDescripcion());
            stmt.setString(7, podcast.getArchivoAudio());
            stmt.setString(8, podcast.getId());
            
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
            String sql = "DELETE FROM podcasts WHERE id = ?";
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
    
    public List<String> obtenerCategorias() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<String> categorias = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT DISTINCT categoria FROM podcasts WHERE categoria IS NOT NULL ORDER BY categoria";
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
    
    public int contarPodcasts() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int count = 0;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT COUNT(*) as total FROM podcasts";
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
    
    private Podcast mapearPodcast(ResultSet rs) throws SQLException {
        Podcast podcast = new Podcast();
        podcast.setId(rs.getString("id"));
        podcast.setTitulo(rs.getString("titulo"));
        podcast.setAnfitrion(rs.getString("anfitrion"));
        podcast.setFechaPublicacion(rs.getDate("fecha_publicacion"));
        podcast.setDuracion(rs.getInt("duracion"));
        podcast.setCategoria(rs.getString("categoria"));
        podcast.setDescripcion(rs.getString("descripcion"));
        podcast.setArchivoAudio(rs.getString("archivo_audio"));
        return podcast;
    }
}