<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Usuario" %>
<%@ page import="java.util.Map" %>
<%
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null || !"admin".equals(usuario.getRol())) {
        response.sendRedirect("../index.html");
        return;
    }
    
    Map<String, Object> estadisticas = (Map<String, Object>) request.getAttribute("estadisticas");
    if (estadisticas == null) {
        response.sendRedirect("../reportes/admin");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Conexión Cultural - Reportes</title>
    <link rel="stylesheet" href="../Css/Estilos.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script src="../Js/JS_Reportes_Admin.js" defer></script>
</head>
<body>
    <header class="dashboard-header">
        <div class="header-content">
            <h1>Conexión Cultural</h1>
            <p>Reportes y Estadísticas del Sistema</p>
        </div>
        <nav class="main-nav">
            <ul>
                <li><a href="dashboard.jsp">Inicio</a></li>
                <li><a href="cine.jsp">Cine</a></li>
                <li><a href="podcast.jsp">Podcast</a></li>
                <li><a href="artes.jsp">Artes</a></li>
                <li><a href="entradas.jsp">Entradas</a></li>
                <li><a href="reportes.jsp" class="active">Reportes</a></li>
                <li><a href="perfil.jsp">Mi Perfil</a></li>
                <li><a href="../logout" id="logoutLink">Cerrar Sesión</a></li>
            </ul>
        </nav>
    </header>

    <main class="reportes-main">
        <h2>Panel de Reportes</h2>

        <section class="filtros-reportes">
            <h3>Filtros de Búsqueda</h3>
            <form id="filtro-form" action="../reportes/generar" method="POST">
                <div class="form-row">
                    <div class="form-group">
                        <label for="fecha_inicio">Fecha Inicio</label>
                        <input type="date" id="fecha_inicio" name="fecha_inicio">
                    </div>
                    <div class="form-group">
                        <label for="fecha_fin">Fecha Fin</label>
                        <input type="date" id="fecha_fin" name="fecha_fin">
                    </div>
                    <div class="form-group">
                        <label for="tipo_reporte">Tipo de Reporte</label>
                        <select id="tipo_reporte" name="tipo_reporte">
                            <option value="ventas">Ventas</option>
                            <option value="asistencia">Asistencia</option>
                            <option value="satisfaccion">Satisfacción</option>
                            <option value="popularidad">Popularidad</option>
                        </select>
                    </div>
                </div>
                <div class="form-actions">
                    <button type="submit" id="btnGenerar" class="btn-primary">Generar</button>
                    <button type="button" id="btnPDF" class="btn-secondary" onclick="exportarPDF()">Exportar PDF</button>
                    <button type="reset" class="btn-secondary">Limpiar</button>
                </div>
            </form>
        </section>

        <section class="kpi-cards">
            <div class="kpi-card">
                <h4>Total de Ventas</h4>
                <p class="kpi-value">S/ <%= estadisticas.get("totalVentas") != null ? String.format("%.2f", estadisticas.get("totalVentas")) : "0.00" %></p>
                <p class="kpi-tendencia tendencia-positiva">+15%</p>
            </div>
            <div class="kpi-card">
                <h4>Eventos Realizados</h4>
                <p class="kpi-value"><%= estadisticas.get("totalEventos") != null ? estadisticas.get("totalEventos") : "0" %></p>
                <p class="kpi-tendencia tendencia-positiva">+8%</p>
            </div>
            <div class="kpi-card">
                <h4>Satisfacción Promedio</h4>
                <p class="kpi-value"><%= estadisticas.get("satisfaccion") != null ? estadisticas.get("satisfaccion") : "0" %>/5</p>
                <p class="kpi-tendencia tendencia-neutral">0%</p>
            </div>
        </section>

        <section class="reportes-content">
            <div class="graficos-container">
                <div class="reporte-chart">
                    <h3>Ventas por Categoría</h3>
                    <canvas id="ventasChart"></canvas>
                </div>
                <div class="reporte-chart">
                    <h3>Asistencia Mensual</h3>
                    <canvas id="asistenciaChart"></canvas>
                </div>
            </div>

            <div class="reporte-chart full-width">
                <h3>Satisfacción General</h3>
                <canvas id="satisfaccionChart"></canvas>
            </div>

            <div class="reporte-tabla">
                <h3>Detalle de Actividades</h3>
                <table>
                    <thead>
                        <tr>
                            <th>Categoría</th>
                            <th>Ventas</th>
                            <th>Asistencia</th>
                            <th>Ingresos</th>
                            <th>Satisfacción</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr><td>Cine</td><td>120</td><td>85%</td><td>S/1800</td><td>4.7</td></tr>
                        <tr><td>Arte</td><td>95</td><td>78%</td><td>S/1425</td><td>4.5</td></tr>
                        <tr><td>Podcast</td><td>65</td><td>92%</td><td>S/1300</td><td>4.8</td></tr>
                    </tbody>
                </table>
            </div>
        </section>
    </main>

    <footer class="dashboard-footer">
        <p>&copy; 2025 Conexión Cultural - Universidad de Arequipa</p>
    </footer>

    <script>
    function exportarPDF() {
        window.location.href = '../reportes/exportar?formato=pdf';
    }
    </script>
</body>
</html>