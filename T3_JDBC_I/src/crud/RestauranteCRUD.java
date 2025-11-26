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
		// Utilizamos try-catch para abrir la conexión y el Statement.
		// De esta forma, ambos se cierran automáticamente al finalizar.
		try (Connection conn = Conexion.conexionBD(); Statement st = conn.createStatement()) {

			// Intentamos crear la tabla Mesa si es posible.
			if (puedeCrearTabla("mesa")) {
				st.executeUpdate(crearTablaMesa());
				System.out.println("Tabla Mesa creada correctamente.");
			}

			// Intentamos crear la tabla Producto.
			if (puedeCrearTabla("producto")) {
				st.executeUpdate(crearTablaProducto());
				System.out.println("Tabla Producto creada correctamente.");
			}

			// Intentamos crear la tabla Factura (depende de Mesa).
			if (puedeCrearTabla("factura")) {
				st.executeUpdate(crearTablaFactura());
				System.out.println("Tabla Factura creada correctamente.");
			}

			// Intentamos crear la tabla Pedido (depende de Factura y Producto).
			if (puedeCrearTabla("pedido")) {
				st.executeUpdate(crearTablaPedido());
				System.out.println("Tabla Pedido creada correctamente.");
			}

		} catch (SQLException e) {
			// Si ocurre cualquier problema con la BD, mostramos el error.
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
		// Variable para almacenar si la tabla existe o no.
		boolean existe = false;

		try (Connection conexion = Conexion.conexionBD()) {

			// Obtenemos datos de la base de datos para consultar las tablas existentes.
			DatabaseMetaData metaData = conexion.getMetaData();

			// Buscamos la tabla especificada.
			ResultSet tablas = metaData.getTables(null, null, nombreTabla, new String[] { "TABLE" });

			// Si el ResultSet tiene al menos un resultado, la tabla existe.
			if (tablas.next()) {
				existe = true;
			}

			// Cerramos el ResultSet manualmente.
			tablas.close();

		} catch (SQLException e) {
			// Si ocurre algún error, informamos por consola.
			System.out.println("Error comprobando la existencia de la tabla " + nombreTabla + ": " + e.getMessage());
		}
		// Devolvemos true si la tabla existe, false si no.
		return existe;
	}

	/**
	 * Determina si una tabla puede ser creada en función de las dependencias que
	 * requiere.
	 * 
	 * @param nombreTabla el nombre de la tabla que se quiere comprobar.
	 * @return true si se cumplen las condiciones para crear la tabla, false en caso
	 *         contrario.
	 */
	public static boolean puedeCrearTabla(String nombreTabla) {

		// Convertimos el nombre a minúsculas.
		nombreTabla = nombreTabla.toLowerCase();

		// Variable que indica si se puede crear la tabla.
		boolean sol;

		// Comprobamos qué tabla es...
		switch (nombreTabla) {

		// Mesa y Producto no dependen de ninguna otra tabla.
		case "mesa":
		case "producto":
			sol = true;
			break;

		// Factura solo se puede crear si la tabla Mesa existe.
		case "factura":
			sol = existenciaTabla("Mesa");
			break;

		// Pedido depende de Factura y Producto.
		case "pedido":
			sol = existenciaTabla("Factura") && existenciaTabla("Producto");
			break;

		// Cualquier otro nombre de tabla no se reconoce: no se crea.
		default:
			sol = false;
		}

		// Devolvemos el resultado final.
		return sol;
	}

	/**
	 * Genera la sentencia SQL para crear la tabla Mesa si no existe.
	 *
	 * @return cadena con la sentencia SQL de creación.
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
	 * Genera la sentencia SQL para crear la tabla Producto si no existe.
	 *
	 * @return cadena con la sentencia SQL de creación.
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
	 * Genera la sentencia SQL para crear la tabla Factura si no existe.
	 *
	 * @return cadena con la sentencia SQL de creación.
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
	 * Genera la sentencia SQL para crear la tabla Pedido si no existe.
	 *
	 * @return cadena con la sentencia SQL de creación.
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
	 * @param reserva       estado de reserva (1 si está reservada, 0 si no).
	 * @return true si la inserción fue exitosa, false si ocurrió algún error.
	 */
	public static boolean insertarMesa(int numComensales, int reserva) {

		String sql = "INSERT INTO Mesa (numComensales, reserva) VALUES (?, ?)";
		boolean sol = false;

		try (Connection conn = Conexion.conexionBD(); PreparedStatement ps = conn.prepareStatement(sql)) {

			// Asignamos los parámetros del PreparedStatement.
			ps.setInt(1, numComensales);
			ps.setInt(2, reserva);

			// Ejecutamos la inserción.
			ps.executeUpdate();
			sol = true;

		} catch (SQLException e) {
			// Mostramos mensaje si ocurre un error en la inserción.
			System.out.println("Error al insertar mesa: " + e.getMessage());
		}

		return sol;
	}

	/**
	 * Inserta un registro en la tabla Producto.
	 *
	 * @param nombre nombre del producto.
	 * @param precio precio del producto.
	 * @return true si la inserción fue exitosa, false si ocurrió algún error.
	 */
	public static boolean insertarProducto(String nombre, double precio) {
		String sql = "INSERT INTO Producto (nombre, precio) VALUES (?, ?)";
		boolean sol = false;

		try (Connection conn = Conexion.conexionBD(); PreparedStatement ps = conn.prepareStatement(sql)) {

			// Asignamos los parámetros del PreparedStatement.
			ps.setString(1, nombre);
			ps.setDouble(2, precio);

			// Ejecutamos la inserción.
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
	 * @param idMesa   ID de la mesa asociada a la factura.
	 * @param tipoPago tipo de pago de la factura "Efectivo", "Tarjeta".
	 * @param importe  importe total de la factura.
	 * @return true si la inserción fue exitosa, false si ocurrió algún error.
	 */
	public static boolean insertarFactura(int idMesa, String tipoPago, double importe) {
		String sql = "INSERT INTO Factura (idMesa, tipoPago, importe) VALUES (?, ?, ?)";
		boolean sol = false;

		try (Connection conn = Conexion.conexionBD(); PreparedStatement ps = conn.prepareStatement(sql)) {

			// Asignamos los parámetros.
			ps.setInt(1, idMesa);
			ps.setString(2, tipoPago);
			ps.setDouble(3, importe);

			// Ejecutamos la inserción.
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
	 * @param idFactura  ID de la factura asociada al pedido.
	 * @param idProducto ID del producto que se pide.
	 * @param cantidad   cantidad de unidades del producto.
	 * @return true si la inserción fue exitosa, false si ocurrió algún error.
	 */
	public static boolean insertarPedido(int idFactura, int idProducto, int cantidad) {
		String sql = "INSERT INTO Pedido (idFactura, idProducto, cantidad) VALUES (?, ?, ?)";
		boolean sol = false;

		try (Connection conn = Conexion.conexionBD(); PreparedStatement ps = conn.prepareStatement(sql)) {

			// Asignamos los parámetros del PreparedStatement.
			ps.setInt(1, idFactura);
			ps.setInt(2, idProducto);
			ps.setInt(3, cantidad);

			// Ejecutamos la inserción.
			ps.executeUpdate();
			sol = true;

		} catch (SQLException e) {
			System.out.println("Error al insertar pedido: " + e.getMessage());
		}

		return sol;
	}

	/**
	 * Lista todos los registros de la tabla Mesa.
	 *
	 * @return cadena con todos los registros de Mesa o mensaje de error.
	 */
	public static String listarMesa() {
		StringBuilder resultado = new StringBuilder();
		String sql = "SELECT * FROM Mesa";

		try (Connection conn = Conexion.conexionBD();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				resultado.append("ID: ").append(rs.getInt("idMesa"));
				resultado.append(" | Comensales: ").append(rs.getInt("numComensales"));
				resultado.append(" | Reserva: ").append(rs.getInt("reserva"));
				resultado.append("\n");
			}

		} catch (SQLException e) {
			return "Error al listar mesas: " + e.getMessage();
		}

		if (resultado.length() == 0) {
			return "No hay mesas registradas.";
		}

		return resultado.toString();
	}

	/**
	 * Lista registros de la tabla Mesa filtrando por número de comensales.
	 *
	 * @param numComensales número de comensales a buscar.
	 * @return cadena con los registros encontrados o mensaje si no hay resultados.
	 */
	public static String listarMesaPorComensales(int numComensales) {
		StringBuilder resultado = new StringBuilder();
		String sql = "SELECT * FROM Mesa WHERE numComensales = ?";

		try (Connection conn = Conexion.conexionBD(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, numComensales);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				resultado.append("ID: ").append(rs.getInt("idMesa"));
				resultado.append(" | Comensales: ").append(rs.getInt("numComensales"));
				resultado.append(" | Reserva: ").append(rs.getInt("reserva"));
				resultado.append("\n");
			}

		} catch (SQLException e) {
			return "Error al listar mesas: " + e.getMessage();
		}

		if (resultado.length() == 0) {
			return "No hay mesas con " + numComensales + " comensales.";
		}

		return resultado.toString();
	}

	/**
	 * Lista registros de la tabla Mesa filtrando por estado de reserva.
	 *
	 * @param reserva estado de reserva (1 reservada, 0 libre).
	 * @return cadena con los registros encontrados o mensaje si no hay resultados.
	 */
	public static String listarMesaPorReserva(int reserva) {
		StringBuilder resultado = new StringBuilder();
		String sql = "SELECT * FROM Mesa WHERE reserva = ?";

		try (Connection conn = Conexion.conexionBD(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, reserva);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				resultado.append("ID: ").append(rs.getInt("idMesa"));
				resultado.append(" | Comensales: ").append(rs.getInt("numComensales"));
				resultado.append(" | Reserva: ").append(rs.getInt("reserva"));
				resultado.append("\n");
			}

		} catch (SQLException e) {
			return "Error al listar mesas: " + e.getMessage();
		}

		if (resultado.length() == 0) {
			return "No hay mesas con ese estado de reserva.";
		}

		return resultado.toString();
	}

	/**
	 * Lista todos los registros de la tabla Producto.
	 *
	 * @return cadena con todos los productos o mensaje de error.
	 */
	public static String listarProducto() {
		StringBuilder resultado = new StringBuilder();
		String sql = "SELECT * FROM Producto";

		try (Connection conn = Conexion.conexionBD();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				resultado.append("ID: ").append(rs.getInt("idProducto"));
				resultado.append(" | Nombre: ").append(rs.getString("nombre"));
				resultado.append(" | Precio: ").append(rs.getDouble("precio"));
				resultado.append("\n");
			}

		} catch (SQLException e) {
			return "Error al listar productos: " + e.getMessage();
		}

		if (resultado.length() == 0) {
			return "No hay productos registrados.";
		}

		return resultado.toString();
	}

	/**
	 * Lista registros de la tabla Producto filtrando por nombre (búsqueda parcial).
	 *
	 * @param nombre nombre o parte del nombre del producto a buscar.
	 * @return cadena con los registros encontrados o mensaje si no hay resultados.
	 */
	public static String listarProductoPorNombre(String nombre) {
		StringBuilder resultado = new StringBuilder();
		String sql = "SELECT * FROM Producto WHERE nombre LIKE ?";

		try (Connection conn = Conexion.conexionBD(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, "%" + nombre + "%");
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				resultado.append("ID: ").append(rs.getInt("idProducto"));
				resultado.append(" | Nombre: ").append(rs.getString("nombre"));
				resultado.append(" | Precio: ").append(rs.getDouble("precio"));
				resultado.append("\n");
			}

		} catch (SQLException e) {
			return "Error al listar productos: " + e.getMessage();
		}

		if (resultado.length() == 0) {
			return "No hay productos con ese nombre.";
		}

		return resultado.toString();
	}

	/**
	 * Lista registros de la tabla Producto filtrando por precio menor que el
	 * indicado.
	 *
	 * @param precio precio máximo.
	 * @return cadena con los registros encontrados o mensaje si no hay resultados.
	 */
	public static String listarProductoPorPrecioMenor(double precio) {
		StringBuilder resultado = new StringBuilder();
		String sql = "SELECT * FROM Producto WHERE precio < ?";

		try (Connection conn = Conexion.conexionBD(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setDouble(1, precio);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				resultado.append("ID: ").append(rs.getInt("idProducto"));
				resultado.append(" | Nombre: ").append(rs.getString("nombre"));
				resultado.append(" | Precio: ").append(rs.getDouble("precio"));
				resultado.append("\n");
			}

		} catch (SQLException e) {
			return "Error al listar productos: " + e.getMessage();
		}

		if (resultado.length() == 0) {
			return "No hay productos con precio menor a " + precio;
		}

		return resultado.toString();
	}

	/**
	 * Lista registros de la tabla Producto filtrando por precio mayor que el
	 * indicado.
	 *
	 * @param precio precio mínimo.
	 * @return cadena con los registros encontrados o mensaje si no hay resultados.
	 */
	public static String listarProductoPorPrecioMayor(double precio) {
		StringBuilder resultado = new StringBuilder();
		String sql = "SELECT * FROM Producto WHERE precio > ?";

		try (Connection conn = Conexion.conexionBD(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setDouble(1, precio);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				resultado.append("ID: ").append(rs.getInt("idProducto"));
				resultado.append(" | Nombre: ").append(rs.getString("nombre"));
				resultado.append(" | Precio: ").append(rs.getDouble("precio"));
				resultado.append("\n");
			}

		} catch (SQLException e) {
			return "Error al listar productos: " + e.getMessage();
		}

		if (resultado.length() == 0) {
			return "No hay productos con precio mayor a " + precio;
		}

		return resultado.toString();
	}

	/**
	 * Lista todos los registros de la tabla Factura.
	 *
	 * @return cadena con todas las facturas o mensaje de error.
	 */
	public static String listarFactura() {
		StringBuilder resultado = new StringBuilder();
		String sql = "SELECT * FROM Factura";

		try (Connection conn = Conexion.conexionBD();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				resultado.append("ID: ").append(rs.getInt("idFactura"));
				resultado.append(" | Mesa: ").append(rs.getInt("idMesa"));
				resultado.append(" | Tipo Pago: ").append(rs.getString("tipoPago"));
				resultado.append(" | Importe: ").append(rs.getDouble("importe"));
				resultado.append("\n");
			}

		} catch (SQLException e) {
			return "Error al listar facturas: " + e.getMessage();
		}

		if (resultado.length() == 0) {
			return "No hay facturas registradas.";
		}

		return resultado.toString();
	}

	/**
	 * Lista registros de la tabla Factura filtrando por tipo de pago.
	 *
	 * @param tipoPago tipo de pago a buscar (Efectivo, Tarjeta, etc.).
	 * @return cadena con los registros encontrados o mensaje si no hay resultados.
	 */
	public static String listarFacturaPorTipoPago(String tipoPago) {
		StringBuilder resultado = new StringBuilder();
		String sql = "SELECT * FROM Factura WHERE tipoPago = ?";

		try (Connection conn = Conexion.conexionBD(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, tipoPago);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				resultado.append("ID: ").append(rs.getInt("idFactura"));
				resultado.append(" | Mesa: ").append(rs.getInt("idMesa"));
				resultado.append(" | Tipo Pago: ").append(rs.getString("tipoPago"));
				resultado.append(" | Importe: ").append(rs.getDouble("importe"));
				resultado.append("\n");
			}

		} catch (SQLException e) {
			return "Error al listar facturas: " + e.getMessage();
		}

		if (resultado.length() == 0) {
			return "No hay facturas con ese tipo de pago.";
		}

		return resultado.toString();
	}

	/**
	 * Lista registros de la tabla Factura filtrando por importe menor que el
	 * indicado.
	 *
	 * @param importe importe máximo.
	 * @return cadena con los registros encontrados o mensaje si no hay resultados.
	 */
	public static String listarFacturaPorImporteMenor(double importe) {
		StringBuilder resultado = new StringBuilder();
		String sql = "SELECT * FROM Factura WHERE importe < ?";

		try (Connection conn = Conexion.conexionBD(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setDouble(1, importe);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				resultado.append("ID: ").append(rs.getInt("idFactura"));
				resultado.append(" | Mesa: ").append(rs.getInt("idMesa"));
				resultado.append(" | Tipo Pago: ").append(rs.getString("tipoPago"));
				resultado.append(" | Importe: ").append(rs.getDouble("importe"));
				resultado.append("\n");
			}

		} catch (SQLException e) {
			return "Error al listar facturas: " + e.getMessage();
		}

		if (resultado.length() == 0) {
			return "No hay facturas con importe menor a " + importe;
		}

		return resultado.toString();
	}

	/**
	 * Lista registros de la tabla Factura filtrando por importe mayor que el
	 * indicado.
	 *
	 * @param importe importe mínimo.
	 * @return cadena con los registros encontrados o mensaje si no hay resultados.
	 */
	public static String listarFacturaPorImporteMayor(double importe) {
		StringBuilder resultado = new StringBuilder();
		String sql = "SELECT * FROM Factura WHERE importe > ?";

		try (Connection conn = Conexion.conexionBD(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setDouble(1, importe);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				resultado.append("ID: ").append(rs.getInt("idFactura"));
				resultado.append(" | Mesa: ").append(rs.getInt("idMesa"));
				resultado.append(" | Tipo Pago: ").append(rs.getString("tipoPago"));
				resultado.append(" | Importe: ").append(rs.getDouble("importe"));
				resultado.append("\n");
			}

		} catch (SQLException e) {
			return "Error al listar facturas: " + e.getMessage();
		}

		if (resultado.length() == 0) {
			return "No hay facturas con importe mayor a " + importe;
		}

		return resultado.toString();
	}

	/**
	 * Lista todos los registros de la tabla Pedido.
	 *
	 * @return cadena con todos los pedidos o mensaje de error.
	 */
	public static String listarPedido() {
		StringBuilder resultado = new StringBuilder();
		String sql = "SELECT * FROM Pedido";

		try (Connection conn = Conexion.conexionBD();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				resultado.append("ID: ").append(rs.getInt("idPedido"));
				resultado.append(" | Factura: ").append(rs.getInt("idFactura"));
				resultado.append(" | Producto: ").append(rs.getInt("idProducto"));
				resultado.append(" | Cantidad: ").append(rs.getInt("cantidad"));
				resultado.append("\n");
			}

		} catch (SQLException e) {
			return "Error al listar pedidos: " + e.getMessage();
		}

		if (resultado.length() == 0) {
			return "No hay pedidos registrados.";
		}

		return resultado.toString();
	}

	/**
	 * Lista registros de la tabla Pedido filtrando por cantidad igual a la
	 * indicada.
	 *
	 * @param cantidad cantidad exacta a buscar.
	 * @return cadena con los registros encontrados o mensaje si no hay resultados.
	 */
	public static String listarPedidoPorCantidad(int cantidad) {
		StringBuilder resultado = new StringBuilder();
		String sql = "SELECT * FROM Pedido WHERE cantidad = ?";

		try (Connection conn = Conexion.conexionBD(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, cantidad);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				resultado.append("ID: ").append(rs.getInt("idPedido"));
				resultado.append(" | Factura: ").append(rs.getInt("idFactura"));
				resultado.append(" | Producto: ").append(rs.getInt("idProducto"));
				resultado.append(" | Cantidad: ").append(rs.getInt("cantidad"));
				resultado.append("\n");
			}

		} catch (SQLException e) {
			return "Error al listar pedidos: " + e.getMessage();
		}

		if (resultado.length() == 0) {
			return "No hay pedidos con esa cantidad.";
		}

		return resultado.toString();
	}

	/**
	 * Modifica un valor de un registro en una tabla específica. Este método
	 * actualiza el valor de un campo según un filtro dado, usando una transacción
	 * para que los cambios puedan confirmarse o deshacerse posteriormente.
	 *
	 * @param con            conexión a la base de datos.
	 * @param tabla          nombre de la tabla donde se realizará la modificación.
	 * @param campoFiltro    nombre del campo que se utilizará para localizar el
	 *                       registro.
	 * @param valorFiltro    valor que debe coincidir en el campoFiltro para
	 *                       modificar el registro.
	 * @param campoModificar nombre del campo que se desea actualizar.
	 * @param nuevoValor     nuevo valor que se asignará al campoModificar.
	 * @return true si al menos un registro fue modificado, false en caso contrario.
	 */
	public static boolean modificar(Connection con, String tabla, String campoFiltro, String valorFiltro,
			String campoModificar, String nuevoValor) {

		// Variable que indica si la modificación tuvo éxito.
		boolean exito = false;

		try {
			// Desactivamos el autocommit para manejar la transacción manualmente.
			con.setAutoCommit(false);

			// Construimos la sentencia SQL para modificar el registro.
			String sql = "UPDATE " + tabla + " SET " + campoModificar + " = ? WHERE " + campoFiltro + " = ?";
			PreparedStatement ps = con.prepareStatement(sql);

			// Asignamos los parámetros al PreparedStatement.
			ps.setString(1, nuevoValor);
			ps.setString(2, valorFiltro);

			// Ejecutamos la actualización.
			int filas = ps.executeUpdate();

			// Si se modificó al menos una fila, indicamos éxito.
			if (filas > 0) {
				exito = true;
			}

		} catch (SQLException e) {
			System.out.println("Error al modificar: " + e.getMessage());
		}

		// Devolvemos true si se modificó algún registro, false en caso contrario.
		return exito;
	}

	/**
	 * Elimina registros de una tabla de la base de datos. Puede eliminar todos los
	 * registros de la tabla o solo aquellos que cumplan un filtro específico.
	 * 
	 * @param con         conexión a la base de datos, gestionada desde el método
	 *                    que llama.
	 * @param tabla       nombre de la tabla de la cual se eliminarán registros.
	 * @param campoFiltro nombre del campo que se utilizará como filtro (si no se
	 *                    borra todo).
	 * @param valorFiltro valor que debe coincidir en el campoFiltro para borrar un
	 *                    registro específico.
	 * @param borrarTodo  true para eliminar todos los registros de la tabla, false
	 *                    para eliminar solo los que cumplan el filtro.
	 * @return true si la operación se ejecutó correctamente, false en caso de
	 *         error.
	 */
	public static boolean borrar(Connection con, String tabla, String campoFiltro, String valorFiltro,
			boolean borrarTodo) {

		// Variable que indica si la operación fue exitosa.
		boolean exito = false;

		try {
			// Iniciamos la transacción desactivando autocommit.
			con.setAutoCommit(false);

			String sql;

			// Si borrarTodo es true, se eliminarán todos los registros.
			if (borrarTodo) {
				sql = "DELETE FROM " + tabla;
			} else {
				// Si no, solo eliminamos los registros que coincidan con el filtro.
				sql = "DELETE FROM " + tabla + " WHERE " + campoFiltro + " = ?";
			}

			// Preparamos la sentencia SQL.
			PreparedStatement ps = con.prepareStatement(sql);

			// Si no estamos borrando todo, asignamos el valor del filtro.
			if (!borrarTodo) {
				ps.setString(1, valorFiltro);
			}

			// Ejecutamos la eliminación.
			ps.executeUpdate();

			// Si no hay excepción, la operación se considera exitosa.
			exito = true;

		} catch (SQLException e) {
			System.out.println("Error al borrar: " + e.getMessage());
		}

		// Devolvemos true si la eliminación fue correcta, false si hubo error.
		return exito;
	}

	/**
	 * Elimina una o varias tablas de la base de datos.
	 *
	 * @param con          conexión a la base de datos, gestionada desde el método
	 *                     que llama.
	 * @param tabla        nombre de la tabla a eliminar si eliminarTodo es false.
	 * @param eliminarTodo true para eliminar todas las tablas (Pedido, Factura,
	 *                     Producto, Mesa), false para eliminar solo la tabla
	 *                     indicada.
	 * @return true si la operación se ejecutó correctamente, false si ocurrió algún
	 *         error.
	 */
	public static boolean eliminarTabla(Connection con, String tabla, boolean eliminarTodo) {

		// Variable que indica si la operación fue exitosa.
		boolean exito = false;

		try {
			// Desactivamos autocommit para gestionar la transacción manualmente.
			con.setAutoCommit(false);

			if (eliminarTodo) {
				// Eliminamos todas las tablas en orden para evitar errores de FK.
				String[] tablas = { "Pedido", "Factura", "Producto", "Mesa" };
				for (String t : tablas) {
					try {
						con.createStatement().executeUpdate("DROP TABLE IF EXISTS " + t);
					} catch (SQLException e) {
						// Ignoramos errores individuales, continuamos con las demás.
					}
				}
				exito = true;

			} else {
				// Eliminamos solo la tabla indicada.
				String sql = "DROP TABLE " + tabla;
				try {
					con.createStatement().executeUpdate(sql);
					exito = true;
				} catch (SQLException e) {
					System.out.println(
							"No se puede eliminar la tabla " + tabla + ". Puede tener relaciones con otras tablas.");
				}
			}

		} catch (SQLException e) {
			System.out.println("Error al eliminar tablas: " + e.getMessage());
		}

		// Devolvemos true si se eliminó correctamente, false si hubo algún error.
		return exito;
	}
}
