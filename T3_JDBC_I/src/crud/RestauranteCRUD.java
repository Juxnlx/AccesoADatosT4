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
				    numero INT NOT NULL,
				    capacidad INT NOT NULL
				);
				""";
	}

	public static String crearTablaProducto() {
		return """
				CREATE TABLE IF NOT EXISTS Producto (
				    idProducto INT AUTO_INCREMENT PRIMARY KEY,
				    nombre VARCHAR(50) NOT NULL,
				    precio DECIMAL(6,2) NOT NULL
				);
				""";
	}

	public static String crearTablaFactura() {
		return """
				CREATE TABLE IF NOT EXISTS Factura (
				    idFactura INT AUTO_INCREMENT PRIMARY KEY,
				    fecha DATE NOT NULL,
				    idMesa INT,
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

	public static boolean insertarMesa(int numero, int capacidad) throws SQLException {
		String sql = "INSERT INTO Mesa (numero, capacidad) VALUES (?, ?)";
		boolean sol = false;

		try (Connection conn = Conexion.conexionBD(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, numero);
			ps.setInt(2, capacidad);

			ps.executeUpdate();

			sol = true;

		} catch (SQLException e) {
			System.out.println("Error al insertar mesa: " + e.getMessage());
		}

		return sol;
	}
	
	public static boolean insertarFactura(String fecha, int numeroMesa) throws SQLException {
		int idMesa = obtenerIdPorCampo("Mesa", "numero", numeroMesa);

        if (idMesa == -1) {
            System.out.println("No existe ninguna mesa con ese número.");
            return false;
        } 
		
		String sql = "INSERT INTO Factura (fecha, idMesa) VALUES (?, ?)";
		boolean sol = false;

		try (Connection conn = Conexion.conexionBD(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setDate(1, Date.valueOf(fecha));
			ps.setInt(2, idMesa);

			ps.executeUpdate();

			sol = true;

		} catch (SQLException e) {
			System.out.println("Error al insertar factura: " + e.getMessage());
		}

		return sol;
	}

}
