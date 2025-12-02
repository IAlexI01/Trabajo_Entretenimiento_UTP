<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Usuario" %>
<%
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null || !"admin".equals(usuario.getRol())) {
        response.sendRedirect("../index.html");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Perfil del Administrador - Conexión Cultural</title>
    <link rel="stylesheet" href="../Css/Estilos.css">
    <link rel="stylesheet" href="../Css/Estilos_Admin.css">
    <script src="../Js/JS_PerfilAdmin.js" defer></script>
</head>
<body>
    <header class="dashboard-header">
        <div class="header-content">
            <h1>Conexión Cultural</h1>
            <p>Mi Perfil - Administrador: <strong><%= usuario.getNombre() != null ? usuario.getNombre() : usuario.getEmail() %></strong></p>
        </div>
        <nav class="main-nav">
            <ul>
                <li><a href="dashboard.jsp">Inicio</a></li>
                <li><a href="cine.jsp">Cine</a></li>
                <li><a href="podcast.jsp">Podcast</a></li>
                <li><a href="artes.jsp">Artes</a></li>
                <li><a href="entradas.jsp">Entradas</a></li>
                <li><a href="reportes.jsp">Reportes</a></li>
                <li><a href="perfil.jsp" class="active">Mi Perfil</a></li>
                <li><a href="../logout" id="logoutLink">Cerrar Sesión</a></li>
            </ul>
        </nav>
    </header>

    <main class="perfil-admin-main">
        <section class="perfil-container">
            <h2>Información del Administrador</h2>
            <form id="adminForm" action="../usuario/actualizar" method="POST">
                <label>Nombre</label>
                <input type="text" id="nombre" name="nombre" value="<%= usuario.getNombre() != null ? usuario.getNombre() : "" %>" required>

                <label>Email</label>
                <input type="email" id="email" name="email" value="<%= usuario.getEmail() %>" readonly>

                <label>Rol</label>
                <input type="text" id="rol" value="Administrador" readonly>

                <label>Teléfono</label>
                <input type="tel" id="telefono" name="telefono" value="<%= usuario.getTelefono() != null ? usuario.getTelefono() : "" %>" placeholder="+51 900 123 456">

                <div class="form-actions">
                    <button type="submit" class="btn-primary">Guardar Cambios</button>
                    <button type="reset" class="btn-secondary">Cancelar</button>
                </div>
            </form>
        </section>

        <section class="admin-stats">
            <h3>Estadísticas del Sistema</h3>
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-icon">👥</div>
                    <div class="stat-info">
                        <h4>Usuarios Registrados</h4>
                        <p class="stat-number">1,245</p>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon">🎫</div>
                    <div class="stat-info">
                        <h4>Entradas Vendidas</h4>
                        <p class="stat-number">4,380</p>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon">🎨</div>
                    <div class="stat-info">
                        <h4>Eventos Activos</h4>
                        <p class="stat-number">32</p>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon">💰</div>
                    <div class="stat-info">
                        <h4>Ingresos Totales</h4>
                        <p class="stat-number">S/ 52,300.00</p>
                    </div>
                </div>
            </div>
        </section>

        <section class="admin-logs">
            <h3>Últimas Acciones del Sistema</h3>
            <ul class="logs-list">
                <li>🕒 12/12/2025 — Usuario "JuanP" eliminado por inactividad</li>
                <li>🕒 11/12/2025 — Evento "Arte Colonial" aprobado</li>
                <li>🕒 10/12/2025 — Nuevo podcast "Música del Sur" subido</li>
                <li>🕒 09/12/2025 — Se actualizó la cartelera de cine</li>
            </ul>
        </section>
    </main>

    <footer class="dashboard-footer">
        <p>&copy; 2025 Conexión Cultural - Universidad de Arequipa</p>
    </footer>
</body>
</html>