<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Usuario" %>
<%@ page import="java.util.List" %>
<%@ page import="dto.PodcastDTO" %>
<%
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null || !"admin".equals(usuario.getRol())) {
        response.sendRedirect("../index.html");
        return;
    }
    
    List<PodcastDTO> podcasts = (List<PodcastDTO>) request.getAttribute("podcasts");
    if (podcasts == null) {
        response.sendRedirect("../podcast/admin");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Conexión Cultural - Gestión de Podcasts</title>
    <link rel="stylesheet" href="../Css/Estilos_Podcast.css">
    <script src="../Js/JS_Podcast_Admin.js" defer></script>
</head>
<body>
    <header class="dashboard-header">
        <div class="header-content">
            <h1>🎙️ Conexión Cultural</h1>
            <p>Panel de Administración - Podcasts</p>
        </div>
        <nav class="main-nav">
            <ul>
                <li><a href="dashboard.jsp">Inicio</a></li>
                <li><a href="cine.jsp">Cine</a></li>
                <li><a href="podcast.jsp" class="active">Podcast</a></li>
                <li><a href="artes.jsp">Artes</a></li>
                <li><a href="entradas.jsp">Entradas</a></li>
                <li><a href="reportes.jsp">Reportes</a></li>
                <li><a href="perfil.jsp">Mi Perfil</a></li>
                <li><a href="../logout" id="logoutLink">Cerrar Sesión</a></li>
            </ul>
        </nav>
    </header>

    <main class="podcast-main">
        <h2>Gestión de Podcasts Culturales</h2>

        <section class="form-section">
            <form class="podcast-form" action="../podcast/agregar" method="POST" enctype="multipart/form-data">
                <div class="form-row">
                    <div class="form-group">
                        <label for="titulo">Título del Podcast</label>
                        <input type="text" id="titulo" name="titulo" required>
                    </div>
                    <div class="form-group">
                        <label for="anfitrion">Anfitrión</label>
                        <input type="text" id="anfitrion" name="anfitrion" required>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="fecha_publicacion">Fecha de Publicación</label>
                        <input type="date" id="fecha_publicacion" name="fecha_publicacion" required>
                    </div>
                    <div class="form-group">
                        <label for="duracion">Duración (minutos)</label>
                        <input type="number" id="duracion" name="duracion" min="5" max="180" required>
                    </div>
                </div>

                <div class="form-group">
                    <label for="categoria">Categoría</label>
                    <select id="categoria" name="categoria" required>
                        <option value="">Seleccione una categoría</option>
                        <option value="arte">Arte</option>
                        <option value="cultura">Cultura</option>
                        <option value="historia">Historia</option>
                        <option value="musica">Música</option>
                        <option value="literatura">Literatura</option>
                        <option value="entrevistas">Entrevistas</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="descripcion">Descripción</label>
                    <textarea id="descripcion" name="descripcion" rows="4" required></textarea>
                </div>

                <div class="form-group">
                    <label for="archivo_audio">Archivo de Audio (MP3)</label>
                    <input type="file" id="archivo_audio" name="archivo_audio" accept="audio/mp3" required>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn-primary">Subir Podcast</button>
                    <button type="reset" class="btn-secondary">Limpiar</button>
                </div>
            </form>
        </section>

        <section class="podcast-list">
            <h3>Podcasts Disponibles</h3>
            <div class="podcast-items">
                <% for (PodcastDTO podcast : podcasts) { %>
                <article class="podcast-item">
                    <h4><%= podcast.getTitulo() %></h4>
                    <p><strong>Anfitrión:</strong> <%= podcast.getAnfitrion() %> | <strong>Duración:</strong> <%= podcast.getDuracion() %> min</p>
                    <p><strong>Categoría:</strong> <%= podcast.getCategoria() %> | <strong>Publicado:</strong> <%= podcast.getFechaPublicacion() != null ? podcast.getFechaPublicacion().toString() : "N/A" %></p>
                    <audio controls>
                        <source src="<%= podcast.getArchivoAudio() != null ? podcast.getArchivoAudio() : "#" %>" type="audio/mp3">
                        Tu navegador no soporta el elemento de audio.
                    </audio>
                    <div class="podcast-actions">
                        <button class="btn-small" onclick="editarPodcast('<%= podcast.getId() %>')">Editar</button>
                        <button class="btn-small btn-danger" onclick="eliminarPodcast('<%= podcast.getId() %>')">Eliminar</button>
                    </div>
                </article>
                <% } %>
            </div>
        </section>
    </main>

    <footer class="dashboard-footer">
        <p>&copy; 2025 Conexión Cultural - Universidad de Arequipa</p>
    </footer>

    <script>
    function editarPodcast(id) {
        window.location.href = '../podcast/editar?id=' + id;
    }
    
    function eliminarPodcast(id) {
        if (confirm('¿Está seguro de eliminar este podcast?')) {
            window.location.href = '../podcast/eliminar?id=' + id;
        }
    }
    </script>
</body>
</html>
