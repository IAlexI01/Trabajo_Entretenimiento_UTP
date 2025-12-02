<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Usuario" %>
<%@ page import="java.util.List" %>
<%@ page import="dto.ArteDTO" %>
<%
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null || !"admin".equals(usuario.getRol())) {
        response.sendRedirect("../index.html");
        return;
    }
    
    List<ArteDTO> artes = (List<ArteDTO>) request.getAttribute("artes");
    if (artes == null) {
        response.sendRedirect("../artes/admin");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Conexión Cultural - Gestión de Artes</title>
    <link rel="stylesheet" href="../Css/Estilos.css">
    <link rel="stylesheet" href="../Css/Estilos_Artes.css">
    <script src="../Js/JS_Artes_Admin.js" defer></script>
</head>
<body>
    <header class="dashboard-header">
        <div class="header-content">
            <h1>Conexión Cultural</h1>
            <p>Panel de Administración - <span id="userName"><%= usuario.getNombre() != null ? usuario.getNombre() : usuario.getEmail() %></span></p>
        </div>
        <nav class="main-nav">
            <ul>
                <li><a href="dashboard.jsp">Inicio</a></li>
                <li><a href="cine.jsp">Cine</a></li>
                <li><a href="podcast.jsp">Podcast</a></li>
                <li><a href="artes.jsp" class="active">Artes</a></li>
                <li><a href="entradas.jsp">Entradas</a></li>
                <li><a href="reportes.jsp">Reportes</a></li>
                <li><a href="perfil.jsp">Mi Perfil</a></li>
                <li><a href="../logout" id="logoutLink">Cerrar Sesión</a></li>
            </ul>
        </nav>
    </header>

    <main class="artes-main">
        <h2>Registrar Nueva Exposición de Arte</h2>

        <form id="formExposicion" 
              class="artes-form" 
              method="POST" 
              action="../artes/agregar"
              enctype="application/x-www-form-urlencoded">

            <div class="form-row">
                <div class="form-group">
                    <label for="titulo">Nombre de la Exposición</label>
                    <input type="text" id="titulo" name="titulo" required>
                </div>
                <div class="form-group">
                    <label for="artista">Artista o Curador</label>
                    <input type="text" id="artista" name="artista" required>
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="fechaInicio">Fecha de Inicio</label>
                    <input type="date" id="fechaInicio" name="fechaInicio" required>
                </div>
                <div class="form-group">
                    <label for="fechaFin">Fecha de Fin</label>
                    <input type="date" id="fechaFin" name="fechaFin" required>
                </div>
            </div>

            <div class="form-group">
                <label for="lugar">Lugar</label>
                <input type="text" id="lugar" name="lugar" required>
            </div>

            <div class="form-group">
                <label for="categoria">Tipo de Arte</label>
                <select id="categoria" name="categoria" required>
                    <option value="">Seleccione una opción</option>
                    <option value="pintura">Pintura</option>
                    <option value="escultura">Escultura</option>
                    <option value="fotografia">Fotografía</option>
                    <option value="arte_digital">Arte Digital</option>
                    <option value="instalacion">Instalación</option>
                </select>
            </div>

            <div class="form-group">
                <label for="descripcion">Descripción</label>
                <textarea id="descripcion" name="descripcion" rows="4" required></textarea>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="precio">Precio (Opcional)</label>
                    <input type="number" id="precio" name="precio" min="0">
                </div>
                <div class="form-group">
                    <label for="capacidad">Capacidad (Opcional)</label>
                    <input type="number" id="capacidad" name="capacidad" min="1">
                </div>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn-primary">Registrar</button>
                <button type="reset" class="btn-secondary">Limpiar</button>
            </div>
        </form>

        <section class="artes-list">
            <h3>Exposiciones Registradas</h3>

            <table id="tabla-exposiciones">
                <thead>
                    <tr>
                        <th>Exposición</th>
                        <th>Artista</th>
                        <th>Fechas</th>
                        <th>Tipo</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (ArteDTO arte : artes) { %>
                    <tr>
                        <td><%= arte.getTitulo() %></td>
                        <td><%= arte.getArtista() %></td>
                        <td>
                            <%= arte.getFechaInicio() != null ? arte.getFechaInicio().toString() : "N/A" %> - 
                            <%= arte.getFechaFin() != null ? arte.getFechaFin().toString() : "N/A" %>
                        </td>
                        <td><%= arte.getCategoria() %></td>
                        <td>
                            <button class="btn-small btn-editar" onclick="editarArte('<%= arte.getId() %>')">✏️</button>
                            <button class="btn-small btn-danger" onclick="eliminarArte('<%= arte.getId() %>')">🗑️</button>
                        </td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </section>
    </main>

    <footer class="dashboard-footer">
        <p>&copy; 2025 Conexión Cultural - Universidad de Arequipa</p>
    </footer>

    <script>
    function editarArte(id) {
        window.location.href = '../artes/editar?id=' + id;
    }
    
    function eliminarArte(id) {
        if (confirm('¿Está seguro de eliminar esta exposición?')) {
            window.location.href = '../artes/eliminar?id=' + id;
        }
    }
    </script>
</body>
</html>