package crud;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import conexion.Conexion;

public class RestauranteCRUD {

	/**
	 * Creamos todas las tablas necesarias para el funcionamiento del sistema de
	 * restaurante.
	 */
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

	/**
	 * Comprueba si una tabla existe en la base de datos.
	 *
	 * @param nombreTabla nombre de la tabla que queremos comprobar.
	 * @return true si la tabla existe en la base de datos, false en caso contrario.
	 */
	public static boolean existenciaTabla(String nombreTabla) {
		boolean existe = false;
		try (Connection conexion = Conexion.conexionBD()) {
			DatabaseMetaData metaData = conexion.getMetaData();
			ResultSet tablas = metaData.getTables(null, null, nombreTabla, new String[] { "TABLE" });

			if (tablas.next())
				existe = true;

			tablas.close();
		} catch (SQLException e) {
			System.out.println("Error comprobando la existencia de la tabla " + nombreTabla + ": " + e.getMessage());
		}
		return existe;
	}

	/**
	 * Determina si una tabla puede ser creada en función de sus dependencias.
	 *
	 * @param nombreTabla el nombre de la tabla que se quiere comprobar.
	 * @return true si se pueden crear las tablas necesarias, false si no.
	 */
	public static boolean puedeCrearTabla(String nombreTabla) {
		nombreTabla = nombreTabla.toLowerCase();
		boolean sol;

		switch (nombreTabla) {
		case "mesa":
		case "producto":
			sol = true;
			break;
		case "factura":
			sol = existenciaTabla("Mesa");
			break;
		case "pedido":
			sol = existenciaTabla("Factura") && existenciaTabla("Producto");
			break;
		default:
			sol = false;
		}

		return sol;
	}

	/**
	 * Genera la sentencia SQL para crear la tabla Mesa.
	 *
	 * @return SQL de creación de la tabla Mesa.
	 */
	public static String crearTablaMesa() {
		return """
				CREATE TABLE IF NOT EXISTS Mesa (
				    idMesa INT AUTO_INCREMENT PRIMARY KEY,
				    numComensales INT NOT NULL,
				    reserva INT NOT NULL
				);
				""";
	}

	/**
	 * Genera la sentencia SQL para crear la tabla Producto.
	 *
	 * @return SQL de creación de la tabla Producto.
	 */
	public static String crearTablaProducto() {
		return """
				CREATE TABLE IF NOT EXISTS Producto (
				    idProducto INT AUTO_INCREMENT PRIMARY KEY,
				    nombre VARCHAR(45) NOT NULL,
				    precio DECIMAL(10,2) NOT NULL
				);
				""";
	}

	/**
	 * Genera la sentencia SQL para crear la tabla Factura.
	 *
	 * @return SQL de creación de la tabla Factura.
	 */
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

	/**
	 * Genera la sentencia SQL para crear la tabla Pedido.
	 *
	 * @return SQL de creación de la tabla Pedido.
	 */
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

	/**
	 * Inserta un registro en la tabla Mesa.
	 *
	 * @param numComensales número de comensales de la mesa.
	 * @param reserva       estado de reserva (1 reservada, 0 libre).
	 * @return true si la inserción fue exitosa, false si hubo error.
	 */
	public static boolean insertarMesa(int numComensales, int reserva) {
		boolean sol = false;
		String sql = "INSERT INTO Mesa (numComensales, reserva) VALUES (?, ?)";
		try (Connection conn = Conexion.conexionBD(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, numComensales);
			ps.setInt(2, reserva);
			ps.executeUpdate();
			sol = true;
		} catch (SQLException e) {
			System.out.println("Error al insertar mesa: " + e.getMessage());
		}
		return sol;
	}

	/**
	 * Inserta un registro en la tabla Producto.
	 *
	 * @param nombre nombre del producto.
	 * @param precio precio del producto.
	 * @return true si la inserción fue exitosa, false si hubo error.
	 */
	public static boolean insertarProducto(String nombre, double precio) {
		boolean sol = false;
		String sql = "INSERT INTO Producto (nombre, precio) VALUES (?, ?)";
		try (Connection conn = Conexion.conexionBD(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, nombre);
			ps.setDouble(2, precio);
			ps.executeUpdate();
			sol = true;
		} catch (SQLException e) {
			System.out.println("Error al insertar producto: " + e.getMessage());
		}
		return sol;
	}

	/**
	 * Inserta un registro en la tabla Factura.
	 *
	 * @param idMesa   ID de la mesa asociada.
	 * @param tipoPago tipo de pago ("Efectivo", "Tarjeta").
	 * @param importe  importe total.
	 * @return true si la inserción fue exitosa, false si hubo error.
	 */
	public static boolean insertarFactura(int idMesa, String tipoPago, double importe) {
		boolean sol = false;
		String sql = "INSERT INTO Factura (idMesa, tipoPago, importe) VALUES (?, ?, ?)";
		try (Connection conn = Conexion.conexionBD(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, idMesa);
			ps.setString(2, tipoPago);
			ps.setDouble(3, importe);
			ps.executeUpdate();
			sol = true;
		} catch (SQLException e) {
			System.out.println("Error al insertar factura: " + e.getMessage());
		}
		return sol;
	}

	/**
	 * Inserta un registro en la tabla Pedido.
	 *
	 * @param idFactura  ID de la factura asociada.
	 * @param idProducto ID del producto.
	 * @param cantidad   cantidad pedida.
	 * @return true si la inserción fue exitosa, false si hubo error.
	 */
	public static boolean insertarPedido(int idFactura, int idProducto, int cantidad) {
		boolean sol = false;
		String sql = "INSERT INTO Pedido (idFactura, idProducto, cantidad) VALUES (?, ?, ?)";
		try (Connection conn = Conexion.conexionBD(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, idFactura);
			ps.setInt(2, idProducto);
			ps.setInt(3, cantidad);
			ps.executeUpdate();
			sol = true;
		} catch (SQLException e) {
			System.out.println("Error al insertar pedido: " + e.getMessage());
		}
		return sol;
	}

	/**
	 * Lista registros de cualquier tabla, con filtrado opcional y comparadores
	 * según campo.
	 *
	 * @param tabla       Nombre de la tabla ("Mesa", "Producto", "Factura",
	 *                    "Pedido").
	 * @param campoFiltro Campo por el que filtrar (null para listar todo).
	 * @param valorFiltro Valor a filtrar (solo si campoFiltro != null).
	 * @return String con los registros o mensaje si no hay resultados/error.
	 */
	public static String listar(String tabla, String campoFiltro, String valorFiltro) {
		StringBuilder resultado = new StringBuilder();
		String sql = "SELECT * FROM " + tabla;
		String comparador = "=";

		if (campoFiltro != null && valorFiltro != null) {
			switch (tabla.toLowerCase()) {
			case "mesa":
				if (campoFiltro.equalsIgnoreCase("numComensales"))
					comparador = ">";
				else
					comparador = "=";
				break;
			case "producto":
				if (campoFiltro.equalsIgnoreCase("nombre")) {
					comparador = "LIKE";
					valorFiltro = "%" + valorFiltro + "%";
				} else
					comparador = ">";
				break;
			case "factura":
				if (campoFiltro.equalsIgnoreCase("tipoPago")) {
					comparador = "LIKE";
					valorFiltro = "%" + valorFiltro + "%";
				} else
					comparador = ">";
				break;
			case "pedido":
				if (campoFiltro.equalsIgnoreCase("cantidad"))
					comparador = ">";
				else
					comparador = "=";
				break;
			default:
				resultado.append("Tabla no reconocida.");
			}
			if (resultado.length() == 0)
				sql += " WHERE " + campoFiltro + " " + comparador + " ?";
		}

		if (resultado.length() == 0) {
			try (Connection conn = Conexion.conexionBD(); PreparedStatement ps = conn.prepareStatement(sql)) {
				if (campoFiltro != null && valorFiltro != null)
					ps.setString(1, valorFiltro);

				ResultSet rs = ps.executeQuery();
				java.sql.ResultSetMetaData meta = rs.getMetaData();
				int columnCount = meta.getColumnCount();

				while (rs.next()) {
					for (int i = 1; i <= columnCount; i++) {
						resultado.append(meta.getColumnName(i)).append(": ").append(rs.getString(i));
						if (i < columnCount)
							resultado.append(" | ");
					}
					resultado.append("\n");
				}
				rs.close();

				if (resultado.length() == 0) {
					if (campoFiltro != null && valorFiltro != null) {
						resultado.append("No hay registros en ").append(tabla).append(" con ").append(campoFiltro)
								.append(" ").append(comparador).append(" ").append(valorFiltro).append(".");
					} else {
						resultado.append("No hay registros en ").append(tabla).append(".");
					}
				}

			} catch (SQLException e) {
				resultado = new StringBuilder("Error al listar " + tabla + ": " + e.getMessage());
			}
		}

		return resultado.toString();
	}

	/**
	 * Modifica un valor de un registro en una tabla específica. Actualiza el valor
	 * de un campo según un filtro dado.
	 *
	 * @param con            Conexión a la base de datos.
	 * @param tabla          Nombre de la tabla donde se realizará la modificación.
	 * @param campoFiltro    Nombre del campo que se utilizará para localizar el
	 *                       registro.
	 * @param valorFiltro    Valor que debe coincidir en el campoFiltro para
	 *                       modificar el registro.
	 * @param campoModificar Nombre del campo que se desea actualizar.
	 * @param nuevoValor     Nuevo valor que se asignará al campoModificar.
	 * @return true si al menos un registro fue modificado, false en caso contrario.
	 */
	public static boolean modificar(Connection con, String tabla, String campoFiltro, String valorFiltro,
			String campoModificar, String nuevoValor) {

		boolean exito = false;

		try {
			// Desactivamos autocommit para manejar la transacción manualmente
			con.setAutoCommit(false);

			// Creamos la sentencia SQL para actualizar el registro
			String sql = "UPDATE " + tabla + " SET " + campoModificar + " = ? WHERE " + campoFiltro + " = ?";
			PreparedStatement ps = con.prepareStatement(sql);

			// Asignamos los valores al PreparedStatement
			ps.setString(1, nuevoValor);
			ps.setString(2, valorFiltro);

			// Ejecutamos la actualización
			int filas = ps.executeUpdate();

			// Si se modificó al menos una fila, indicamos éxito
			if (filas > 0)
				exito = true;

		} catch (SQLException e) {
			System.out.println("Error al modificar: " + e.getMessage());
		}

		// Devolvemos true si se modificó algún registro, false si no
		return exito;
	}

	/**
	 * Elimina registros de una tabla de la base de datos. Puede eliminar todos los
	 * registros o solo aquellos que cumplan un filtro específico.
	 *
	 * @param con         Conexión a la base de datos.
	 * @param tabla       Nombre de la tabla de la cual se eliminarán registros.
	 * @param campoFiltro Nombre del campo que se utilizará como filtro (si no se
	 *                    borra todo).
	 * @param valorFiltro Valor que debe coincidir en el campoFiltro para borrar un
	 *                    registro específico.
	 * @param borrarTodo  true para eliminar todos los registros, false para
	 *                    eliminar solo los filtrados.
	 * @return true si la operación fue correcta, false si ocurrió algún error.
	 */
	public static boolean borrar(Connection con, String tabla, String campoFiltro, String valorFiltro,
			boolean borrarTodo) {
		boolean exito = false;

		try {
			// Desactivamos autocommit para controlar la transacción
			con.setAutoCommit(false);

			// Construimos la sentencia SQL según se quiera borrar todo o solo con filtro
			String sql = borrarTodo ? "DELETE FROM " + tabla
					: "DELETE FROM " + tabla + " WHERE " + campoFiltro + " = ?";
			PreparedStatement ps = con.prepareStatement(sql);

			// Si no estamos borrando todo, asignamos el valor del filtro
			if (!borrarTodo)
				ps.setString(1, valorFiltro);

			// Ejecutamos la eliminación
			ps.executeUpdate();
			exito = true;

		} catch (SQLException e) {
			System.out.println("Error al borrar: " + e.getMessage());
		}

		// Retornamos true si la eliminación fue correcta, false si hubo error
		return exito;
	}

	/**
	 * Elimina una o varias tablas de la base de datos.
	 *
	 * @param con          Conexión a la base de datos.
	 * @param tabla        Nombre de la tabla a eliminar si eliminarTodo es false.
	 * @param eliminarTodo true para eliminar todas las tablas (Pedido, Factura,
	 *                     Producto, Mesa), false para solo la indicada.
	 * @return true si la operación se ejecutó correctamente, false si ocurrió algún
	 *         error.
	 */
	public static boolean eliminarTabla(Connection con, String tabla, boolean eliminarTodo) {
		boolean exito = false;

		try {
			// Desactivamos autocommit para controlar la transacción
			con.setAutoCommit(false);

			if (eliminarTodo) {
				// Eliminamos todas las tablas en orden para evitar errores de claves foráneas
				String[] tablas = { "Pedido", "Factura", "Producto", "Mesa" };
				for (String t : tablas) {
					try {
						con.createStatement().executeUpdate("DROP TABLE IF EXISTS " + t);
					} catch (SQLException ignored) {
						// Ignoramos errores individuales y seguimos con las demás tablas
					}
				}
				exito = true;
			} else {
				// Eliminamos solo la tabla indicada
				try {
					con.createStatement().executeUpdate("DROP TABLE " + tabla);
					exito = true;
				} catch (SQLException e) {
					System.out.println(
							"No se puede eliminar la tabla " + tabla + ". Puede tener relaciones con otras tablas.");
				}
			}

		} catch (SQLException e) {
			System.out.println("Error al eliminar tablas: " + e.getMessage());
		}

		// Retornamos true si la eliminación fue correcta, false si hubo algún error
		return exito;
	}
}
