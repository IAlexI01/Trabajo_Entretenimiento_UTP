<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Usuario" %>
<%@ page import="java.util.List" %>
<%@ page import="dto.EntradaDTO" %>
<%
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null || !"admin".equals(usuario.getRol())) {
        response.sendRedirect("../index.html");
        return;
    }
    
    List<EntradaDTO> ventas = (List<EntradaDTO>) request.getAttribute("ventas");
    if (ventas == null) {
        response.sendRedirect("../entradas/admin");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Conexión Cultural - Administración de Entradas</title>
    <link rel="stylesheet" href="../Css/Estilos.css">
    <script src="../Js/JS_EntradasAdmin.js" defer></script>
</head>
<body>
    <header class="dashboard-header">
        <div class="header-content">
            <h1>Conexión Cultural</h1>
            <p>Gestión de Entradas - Administrador</p>
        </div>
        <nav class="main-nav">
            <ul>
                <li><a href="dashboard.jsp">Inicio</a></li>
                <li><a href="cine.jsp">Cine</a></li>
                <li><a href="podcast.jsp">Podcast</a></li>
                <li><a href="artes.jsp">Artes</a></li>
                <li><a href="entradas.jsp" class="active">Entradas</a></li>
                <li><a href="reportes.jsp">Reportes</a></li>
                <li><a href="perfil.jsp">Mi Perfil</a></li>
                <li><a href="../logout" id="logoutLink">Cerrar Sesión</a></li>
            </ul>
        </nav>
    </header>

    <main class="entradas-main">
        <h2>Gestión de Entradas 🎟️</h2>

        <div class="entradas-options">
            <button class="btn-primary" onclick="showSection('venta')">Venta de Entradas</button>
            <button class="btn-secondary" onclick="showSection('consulta')">Consulta de Ventas</button>
        </div>

        <!-- Sección de Venta -->
        <section id="venta-section" class="entradas-section">
            <h3>Registrar Nueva Venta</h3>

            <form class="entradas-form" id="formVenta" action="../entradas/procesar-venta" method="post">
                <div class="form-row">
                    <div class="form-group">
                        <label for="evento">Seleccionar Evento</label>
                        <select id="evento" name="evento" required>
                            <option value="">Seleccione un evento</option>
                            <option value="Cine: Los Andes en Cine - 18/12/2025 19:30">Cine: Los Andes en Cine - 18/12/2025 19:30</option>
                            <option value="Exposición: Arte Andino Contemporáneo">Exposición: Arte Andino Contemporáneo</option>
                            <option value="Concierto: Música Tradicional Arequipeña">Concierto: Música Tradicional Arequipeña</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="cantidad">Cantidad</label>
                        <input type="number" id="cantidad" name="cantidad" min="1" max="10" required>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="nombre">Nombre del Cliente</label>
                        <input type="text" id="nombre" name="nombre" required>
                    </div>
                    <div class="form-group">
                        <label for="email">Correo Electrónico</label>
                        <input type="email" id="email" name="email" required>
                    </div>
                </div>

                <div class="form-group">
                    <label for="telefono">Teléfono</label>
                    <input type="tel" id="telefono" name="telefono" required>
                </div>

                <div class="form-group">
                    <label for="metodo_pago">Método de Pago</label>
                    <select id="metodo_pago" name="metodo_pago" required>
                        <option value="">Seleccione un método</option>
                        <option value="Tarjeta de Crédito/Débito">Tarjeta de Crédito/Débito</option>
                        <option value="Transferencia Bancaria">Transferencia Bancaria</option>
                        <option value="Efectivo">Efectivo</option>
                    </select>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn-primary">Procesar Venta</button>
                    <button type="reset" class="btn-secondary">Limpiar</button>
                </div>
            </form>
        </section>

        <!-- Sección de Consulta -->
        <section id="consulta-section" class="entradas-section" style="display: none;">
            <h3>Consulta de Ventas</h3>

            <div class="filtros-ventas">
                <div class="form-row">
                    <div class="form-group">
                        <label for="fecha_desde">Desde</label>
                        <input type="date" id="fecha_desde">
                    </div>
                    <div class="form-group">
                        <label for="fecha_hasta">Hasta</label>
                        <input type="date" id="fecha_hasta">
                    </div>
                    <div class="form-group">
                        <label for="evento_filtro">Evento</label>
                        <select id="evento_filtro">
                            <option value="">Todos</option>
                            <option value="cine">Cine</option>
                            <option value="exposicion">Exposición</option>
                            <option value="concierto">Concierto</option>
                        </select>
                    </div>
                </div>
                <button class="btn-primary" onclick="filtrarVentas()">Buscar</button>
            </div>

            <div class="ventas-list">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Evento</th>
                            <th>Cliente</th>
                            <th>Cantidad</th>
                            <th>Total</th>
                            <th>Fecha</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody id="tablaVentas">
                        <% for (EntradaDTO venta : ventas) { %>
                        <tr>
                            <td><%= venta.getId() %></td>
                            <td><%= venta.getEventoNombre() %></td>
                            <td><%= venta.getUsuarioId() %></td>
                            <td><%= venta.getCantidad() %></td>
                            <td>S/ <%= String.format("%.2f", venta.getTotal()) %></td>
                            <td><%= venta.getFechaCompra() != null ? venta.getFechaCompra().toString() : "N/A" %></td>
                            <td>
                                <button class="btn-small" onclick="verDetalles('<%= venta.getId() %>')">Detalles</button>
                                <button class="btn-small" onclick="reimprimir('<%= venta.getId() %>')">Reimprimir</button>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </section>
    </main>

    <footer class="dashboard-footer">
        <p>&copy; 2025 Conexión Cultural - Universidad de Arequipa</p>
    </footer>

    <script>
    function verDetalles(id) {
        window.location.href = '../entradas/detalle?id=' + id;
    }
    
    function reimprimir(id) {
        if (confirm('¿Reimprimir entrada?')) {
            window.location.href = '../entradas/reimprimir?id=' + id;
        }
    }
    
    function filtrarVentas() {
        // Tu lógica de filtrado existente funciona igual
        alert('Filtrando ventas...');
    }
    </script>
</body>
</html>