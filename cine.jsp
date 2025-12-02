<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Usuario" %>
<%@ page import="java.util.List" %>
<%@ page import="dto.PeliculaDTO" %>
<%
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null || !"admin".equals(usuario.getRol())) {
        response.sendRedirect("../index.html");
        return;
    }
    
    List<PeliculaDTO> peliculas = (List<PeliculaDTO>) request.getAttribute("peliculas");
    if (peliculas == null) {
        response.sendRedirect("../cine/admin");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Conexión Cultural - Administración de Cine</title>
    <link rel="stylesheet" href="../Css/Estilos_Cine.css">
    <script src="../Js/JS_CineAdmin.js" defer></script>
</head>
<body>
    <header class="dashboard-header">
        <div class="header-content">
            <h1>Conexión Cultural</h1>
            <p>Panel de Administración - Cine 🎬</p>
        </div>
        <nav class="main-nav">
            <ul>
                <li><a href="dashboard.jsp">Inicio</a></li>
                <li><a href="cine.jsp" class="active">Cine</a></li>
                <li><a href="podcast.jsp">Podcast</a></li>
                <li><a href="artes.jsp">Artes</a></li>
                <li><a href="entradas.jsp">Entradas</a></li>
                <li><a href="reportes.jsp">Reportes</a></li>
                <li><a href="perfil.jsp">Mi Perfil</a></li>
                <li><a href="../logout" id="logoutLink">Cerrar Sesión</a></li>
            </ul>
        </nav>
    </header>

    <main class="cine-main">
        <h2>Gestión de Cartelera</h2>

        <form class="cine-form" id="formCine" action="../cine/agregar" method="POST">
            <div class="form-row">
                <div class="form-group">
                    <label for="pelicula">Película</label>
                    <input type="text" id="pelicula" name="pelicula" required>
                </div>
                <div class="form-group">
                    <label for="director">Director</label>
                    <input type="text" id="director" name="director" required>
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="fecha">Fecha</label>
                    <input type="date" id="fecha" name="fecha" required>
                </div>
                <div class="form-group">
                    <label for="hora">Hora</label>
                    <input type="time" id="hora" name="hora" required>
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="genero">Género</label>
                    <select id="genero" name="genero" required>
                        <option value="">Seleccione género</option>
                        <option value="documental">Documental</option>
                        <option value="comedia">Comedia</option>
                        <option value="drama">Drama</option>
                        <option value="accion">Acción</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="duracion">Duración (min)</label>
                    <input type="number" id="duracion" name="duracion" min="1" required>
                </div>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn-primary">Agregar Película</button>
                <button type="reset" class="btn-secondary">Limpiar</button>
            </div>
        </form>

        <div class="cine-list">
            <h3>Películas en Cartelera</h3>
            <table>
                <thead>
                    <tr>
                        <th>Película</th>
                        <th>Director</th>
                        <th>Fecha</th>
                        <th>Hora</th>
                        <th>Género</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody id="tablaCine">
                    <% for (PeliculaDTO pelicula : peliculas) { %>
                    <tr>
                        <td><%= pelicula.getTitulo() %></td>
                        <td><%= pelicula.getDirector() %></td>
                        <td><%= pelicula.getFecha() != null ? pelicula.getFecha().toString() : "N/A" %></td>
                        <td><%= pelicula.getHoraFuncion() != null ? pelicula.getHoraFuncion() : "N/A" %></td>
                        <td><%= pelicula.getGenero() != null ? pelicula.getGenero() : "N/A" %></td>
                        <td>
                            <button class="btn-small" onclick="editarPelicula('<%= pelicula.getId() %>')">Editar</button>
                            <button class="btn-small btn-danger" onclick="eliminarPelicula('<%= pelicula.getId() %>')">Eliminar</button>
                        </td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </main>

    <footer class="dashboard-footer">
        <p>&copy; 2025 Conexión Cultural - Universidad de Arequipa</p>
    </footer>

    <script>
    function editarPelicula(id) {
        window.location.href = '../cine/editar?id=' + id;
    }
    
    function eliminarPelicula(id) {
        if (confirm('¿Está seguro de eliminar esta película?')) {
            window.location.href = '../cine/eliminar?id=' + id;
        }
    }
    </script>
</body>
</html>