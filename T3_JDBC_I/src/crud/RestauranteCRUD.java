package crud;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import conexion.Conexion;

public class RestauranteCRUD {

	public static void crearTodasLasTablas() {
		try (Connection conn = Conexion.conexionBD(); Statement st = conn.createStatement()) {

			if (puedeCrearTabla("mesa")) {
				st.executeUpdate(crearTablaMesa());
				System.out.println("Tabla Mesa creada correctamente.");
			}

			if (puedeCrearTabla("producto")) {
				st.executeUpdate(crearTablaProducto());
				System.out.println("Tabla Producto creada correctamente.");
			}

			if (puedeCrearTabla("factura")) {
				st.executeUpdate(crearTablaFactura());
				System.out.println("Tabla Factura creada correctamente.");
			}

			if (puedeCrearTabla("pedido")) {
				st.executeUpdate(crearTablaPedido());
				System.out.println("Tabla Pedido creada correctamente.");
			}

		} catch (SQLException e) {
			System.out.println("Error al crear las tablas: " + e.getMessage());
		}
	}

	public static boolean existenciaTabla(String nombreTabla) {
		boolean existe = false;

		try (Connection conexion = Conexion.conexionBD()) {
			DatabaseMetaData metaData = conexion.getMetaData();
			ResultSet tablas = metaData.getTables(null, null, nombreTabla, new String[] { "TABLE" });

			if (tablas.next()) {
				existe = true;
			}

			tablas.close();

		} catch (SQLException e) {
			System.out.println("Error comprobando la existencia de la tabla " + nombreTabla + ": " + e.getMessage());
		}

		return existe;
	}

	public static boolean puedeCrearTabla(String nombreTabla) throws SQLException {
		nombreTabla = nombreTabla.toLowerCase();
		boolean sol;

		switch (nombreTabla) {
		case "mesa", "producto" -> sol = true;
		case "factura" -> sol = existenciaTabla("mesa");
		case "pedido" -> sol = existenciaTabla("factura") && existenciaTabla("producto");
		default -> sol = false;
		}
		return sol;
	}

	public static String crearTablaMesa() {
		return """
				CREATE TABLE IF NOT EXISTS Mesa (
				    idMesa INT AUTO_INCREMENT PRIMARY KEY,
				    numComensales INT NOT NULL,
				    reserva INT NOT NULL
				);
				""";
	}

	public static String crearTablaProducto() {
		return """
				CREATE TABLE IF NOT EXISTS Producto (
				    idProducto INT AUTO_INCREMENT PRIMARY KEY,
				    nombre VARCHAR(45) NOT NULL,
				    precio DECIMAL(10,2) NOT NULL
				);
				""";
	}

	public static String crearTablaFactura() {
		return """
				CREATE TABLE IF NOT EXISTS Factura (
				    idFactura INT AUTO_INCREMENT PRIMARY KEY,
				    idMesa INT,
				    tipoPago VARCHAR(45) NOT NULL,
				    importe DECIMAL(10,2) NOT NULL,
				    FOREIGN KEY (idMesa) REFERENCES Mesa(idMesa)
				);
				""";
	}

	public static String crearTablaPedido() {
		return """
				CREATE TABLE IF NOT EXISTS Pedido (
				    idPedido INT AUTO_INCREMENT PRIMARY KEY,
				    idFactura INT,
				    idProducto INT,
				    cantidad INT NOT NULL,
				    FOREIGN KEY (idFactura) REFERENCES Factura(idFactura),
				    FOREIGN KEY (idProducto) REFERENCES Producto(idProducto)
				);
				""";
	}

	public static int obtenerIdPorCampo(String tabla, String campo, Object valor) {
		int id = -1;
		String sql = "SELECT * FROM " + tabla + " WHERE " + campo + " = ?";

		try (Connection conn = Conexion.conexionBD(); PreparedStatement ps = conn.prepareStatement(sql)) {
			if (valor instanceof String)
				ps.setString(1, (String) valor);
			else if (valor instanceof Integer)
				ps.setInt(1, (Integer) valor);
			else if (valor instanceof Double)
				ps.setDouble(1, (Double) valor);
			else if (valor instanceof Date)
				ps.setDate(1, (Date) valor);

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String idCol = "id" + tabla;
				id = rs.getInt(idCol);
			}
		} catch (SQLException e) {
			System.out.println("Error al obtener ID de " + tabla + ": " + e.getMessage());
		}

		return id;
	}

	public static boolean insertarMesa(int numComensales, int reserva) {
		String sql = "INSERT INTO Mesa (numComensales, reserva) VALUES (?, ?)";
		boolean sol = false;

		try (Connection conn = Conexion.conexionBD(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, numComensales);
			ps.setInt(2, reserva);
			ps.executeUpdate();
			sol = true;
			System.out.println("Mesa insertada correctamente");
		} catch (SQLException e) {
			System.out.println("Error al insertar mesa: " + e.getMessage());
		}

		return sol;
	}

	public static boolean insertarProducto(String nombre, double precio) {
		String sql = "INSERT INTO Producto (nombre, precio) VALUES (?, ?)";
		boolean sol = false;

		try (Connection conn = Conexion.conexionBD(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, nombre);
			ps.setDouble(2, precio);
			ps.executeUpdate();
			sol = true;
			System.out.println("Producto insertado");
		} catch (SQLException e) {
			System.out.println("Error al insertar producto: " + e.getMessage());
		}

		return sol;
	}

	public static boolean insertarFactura(int idMesa, String tipoPago, double importe) {
		String sql = "INSERT INTO Factura (idMesa, tipoPago, importe) VALUES (?, ?, ?)";
		boolean sol = false;

		try (Connection conn = Conexion.conexionBD(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, idMesa);
			ps.setString(2, tipoPago);
			ps.setDouble(3, importe);

			ps.executeUpdate();
			sol = true;
			System.out.println("Factura insertada correctamente");
		} catch (SQLException e) {
			System.out.println("Error al insertar factura: " + e.getMessage());
		}

		return sol;
	}

	public static boolean insertarPedido(int idFactura, int idProducto, int cantidad) {
		String sql = "INSERT INTO Pedido (idFactura, idProducto, cantidad) VALUES (?, ?, ?)";
		boolean sol = false;

		try (Connection conn = Conexion.conexionBD(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, idFactura);
			ps.setInt(2, idProducto);
			ps.setInt(3, cantidad);
			ps.executeUpdate();
			sol = true;
			System.out.println("Pedido insertado");
		} catch (SQLException e) {
			System.out.println("Error al insertar pedido: " + e.getMessage());
		}

		return sol;
	}

	public static String listar(String tabla, String campo, String operacion, String valor) {
		StringBuilder resultado = new StringBuilder();
		String sql;

		// Si no hay filtro → listar todo
		if (campo.equals("") || operacion.equals("") || valor.equals("")) {
			sql = "SELECT * FROM " + tabla;
		} else {
			if (operacion.equalsIgnoreCase("LIKE")) {
				sql = "SELECT * FROM " + tabla + " WHERE " + campo + " LIKE ?";
				valor = "%" + valor + "%";
			} else {
				sql = "SELECT * FROM " + tabla + " WHERE " + campo + " " + operacion + " ?";
			}
		}

		try (Connection conn = Conexion.conexionBD(); PreparedStatement ps = conn.prepareStatement(sql)) {

			// Si hay filtro → asignar valor
			if (!campo.equals("") && !operacion.equals("") && !valor.equals("")) {
				ps.setString(1, valor);
			}

			ResultSet rs = ps.executeQuery();

			// Obtener nombres de columnas automáticamente
			int columnas = rs.getMetaData().getColumnCount();

			while (rs.next()) {
				for (int i = 1; i <= columnas; i++) {
					resultado.append(rs.getMetaData().getColumnName(i)).append(": ").append(rs.getString(i))
							.append("   ");
				}
				resultado.append("\n");
			}

		} catch (SQLException e) {
			return "Error al listar: " + e.getMessage();
		}

		if (resultado.length() == 0) {
			return "No hay resultados.";
		}

		return resultado.toString();
	}

	public static boolean modificar(String tabla, String campoFiltro, String valorFiltro, String campoModificar,
			String nuevoValor) {

		boolean exito = false;
		Connection con = null;

		try {
			con = Conexion.conexionBD();

			con.setAutoCommit(false); // Iniciar transacción

			String sql = "UPDATE " + tabla + " SET " + campoModificar + " = ? WHERE " + campoFiltro + " = ?";
			PreparedStatement ps = con.prepareStatement(sql);

			ps.setString(1, nuevoValor);
			ps.setString(2, valorFiltro);

			int filas = ps.executeUpdate();

			if (filas > 0) {
				exito = true; // ha modificado algo
			}

			// No hacemos commit ni rollback aquí.
			// Se hará en el MAIN según confirme el usuario.

		} catch (SQLException e) {
			try {
				if (con != null) {
					con.rollback(); // Deshacer cambios si hay error
				}
			} catch (SQLException e2) {
			}
		}

		try {
			if (con != null) {
				con.setAutoCommit(true);
				con.close();
			}
		} catch (SQLException e3) {
		}

		return exito; // ← único return
	}

	public boolean borrar(Connection con, String tabla, String campoFiltro, String valorFiltro, boolean borrarTodo) {
		boolean exito = false;

		try {
			con.setAutoCommit(false); // inicia transacción

			String sql;

			if (borrarTodo) {
				sql = "DELETE FROM " + tabla;
			} else {
				sql = "DELETE FROM " + tabla + " WHERE " + campoFiltro + " = ?";
			}

			PreparedStatement ps = con.prepareStatement(sql);

			if (!borrarTodo) {
				ps.setString(1, valorFiltro);
			}

			ps.executeUpdate();

			exito = true;

		} catch (SQLException e) {
			exito = false;
		}

		return exito;
	}

	public boolean eliminarTabla(Connection con, String tabla, boolean eliminarTodo) {
		boolean exito = false;

		try {
			con.setAutoCommit(false);

			if (eliminarTodo) {
				// Eliminamos todas las tablas (orden recomendado para evitar FK)
				String[] tablas = { "Pedido", "Factura", "Producto", "Mesa" };
				for (String t : tablas) {
					try {
						con.createStatement().executeUpdate("DROP TABLE IF EXISTS " + t);
					} catch (SQLException e) {
						// ignoramos si falla una tabla por FK
					}
				}
				exito = true;
			} else {
				// Eliminamos solo la tabla indicada
				String sql = "DROP TABLE " + tabla;
				try {
					con.createStatement().executeUpdate(sql);
					exito = true;
				} catch (SQLException e) {
					exito = false; // no se puede eliminar por FK u otro motivo
				}
			}

		} catch (SQLException e) {
			exito = false;
		}

		return exito;
	}

}
