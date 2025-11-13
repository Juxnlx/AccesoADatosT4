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
	
	public static ResultSet listar(String tabla, String campo, String operador, String valor) {
	    ResultSet rs = null;
	    Connection conn = null;

	    try {
	        conn = Conexion.conexionBD();
	        String sql;

	        // Si no hay filtro → mostrar toda la tabla
	        if (campo == null || campo.isEmpty()) {
	            sql = "SELECT * FROM " + tabla;
	            PreparedStatement ps = conn.prepareStatement(sql);
	            rs = ps.executeQuery();
	        } else {
	            sql = "SELECT * FROM " + tabla + " WHERE " + campo + " " + operador + " ?";
	            PreparedStatement ps = conn.prepareStatement(sql);

	            if (operador.equalsIgnoreCase("LIKE")) {
	                ps.setString(1, "%" + valor + "%");
	            } else {
	                ps.setString(1, valor);
	            }

	            rs = ps.executeQuery();
	        }

	    } catch (SQLException e) {
	        System.out.println("Error al listar datos de " + tabla + ": " + e.getMessage());
	    }

	    return rs;
	}


}
