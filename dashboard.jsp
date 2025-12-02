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
    <title>Conexión Cultural - Administrador</title>
    <link rel="stylesheet" href="../Css/Estilos.css">
    <script src="../Js/JS_DashboardAdmin.js" defer></script>
</head>
<body>
    <header class="dashboard-header">
        <div class="header-content">
            <h1>Conexión Cultural</h1>
            <p>Bienvenido, <span id="adminName"><%= usuario.getNombre() != null ? usuario.getNombre() : usuario.getEmail() %></span></p>
        </div>

        <nav class="main-nav">
            <ul>
                <li><a href="dashboard.jsp">Inicio</a></li>
                <li><a href="cine.jsp">Cine</a></li>
                <li><a href="podcast.jsp">Podcast</a></li>
                <li><a href="artes.jsp">Artes</a></li>
                <li><a href="entradas.jsp">Entradas</a></li>
                <li><a href="reportes.jsp">Reportes</a></li>
                <li><a href="perfil.jsp">Mi Perfil</a></li>
                <li><a href="../logout" id="logoutLink">Cerrar Sesión</a></li>
            </ul>
        </nav>
    </header>

    <main class="dashboard-main">
        <section class="welcome-section">
            <h2>Panel de Administración</h2>
            <p class="intro-text">Administra el contenido cultural, gestiona usuarios y supervisa las estadísticas del sistema.</p>

            <div class="mission-vision">
                <div class="card">
                    <h3>MISIÓN</h3>
                    <p>Centralizar la gestión cultural de Arequipa mediante una plataforma moderna que facilite la administración de cine, arte y podcasts.</p>
                </div>
                <div class="card">
                    <h3>VISIÓN</h3>
                    <p>Ser la herramienta tecnológica principal para la gestión cultural en la región, ofreciendo control y análisis de la oferta cultural local.</p>
                </div>
            </div>
        </section>

        <section class="quick-access">
            <h3>Acceso Rápido</h3>
            <div class="access-cards">
                <a href="cine.jsp" class="access-card">
                    <h4>Cine</h4>
                    <p>Gestiona películas, funciones y precios</p>
                </a>
                <a href="podcast.jsp" class="access-card">
                    <h4>Podcast</h4>
                    <p>Sube nuevos episodios y administra contenido</p>
                </a>
                <a href="artes.jsp" class="access-card">
                    <h4>Artes</h4>
                    <p>Organiza exposiciones y artistas invitados</p>
                </a>
                <a href="reportes.jsp" class="access-card">
                    <h4>Reportes</h4>
                    <p>Consulta estadísticas y métricas del sistema</p>
                </a>
            </div>
        </section>
    </main>

    <footer class="dashboard-footer">
        <p>&copy; 2025 Conexión Cultural - Universidad de Arequipa</p>
    </footer>
</body>
</html>