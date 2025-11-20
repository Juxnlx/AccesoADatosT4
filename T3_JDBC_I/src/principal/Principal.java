package principal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;


import conexion.Conexion;
import crud.RestauranteCRUD;

public class Principal {

	private static final Scanner sc = new Scanner(System.in);

	public static void main(String[] args) {

		Connection con = Conexion.conexionBD(); // Conexión abierta

        RestauranteCRUD crud = new RestauranteCRUD(); // Instancia del CRUD
		
		// Creamos la variable opc como int para almacenar la opción seleccionada por el
		// usuario.
		int opc;

		do {
			// Llamamos a la función menú.
			Menu();
			// Le pedimos al usuario que introduzca la opción que quiere seleccionar en el
			// menú y la leemos.
			System.out.println("Introduce una opción --> ");
			opc = sc.nextInt();
			// Limpiamos el buffer
			sc.nextLine();

			switch (opc) {
			// Se encarga de la creación de todas las tablas o de alguna especifica.
			case 1 -> gestionCreacionTablas();
			case 2 -> gestionInsentarMesa();
			case 3 -> gestionInsertarProducto();
			case 4 -> gestionInsertarFactura();
			case 5 -> gestionInsertarPedido();
			case 6 -> menuListar();
			case 7 -> modificar();
			case 8 -> borrarDesdeMain(con, crud);
			case 9 -> eliminarTablasDesdeMain(con, crud);
			case 0 -> System.out.println("Saliendo del programa...");
		    default -> System.out.println("Opción no válida.");
			}

		} while (opc != 0);

		// Cerramos el Scanner
		sc.close();
	}

	public static void Menu() {
		System.out.println("----------MENÚ----------");
		System.out.println("1. Crear Tabla");
		System.out.println("2. Insertar Mesa");
		System.out.println("3. Insertar Producto");
		System.out.println("4. Insertar Factura");
		System.out.println("5. Insertar Pedido");
		System.out.println("6. Listar");
		System.out.println("7. Modificar");
		System.out.println("8. Borrar");
		System.out.println("9. Eliminar Tabla");
		System.out.println("0. Salir");
	}

	public static void gestionCreacionTablas() {
		int opc;
		String nombreTabla = "";

		System.out.println("¿Quieres crear todas las tablas o una tabla concreta?");
		System.out.println("1. Todas las tablas");
		System.out.println("2. Tabla concreta");
		opc = sc.nextInt();
		sc.nextLine();

		if (opc == 1) {
			RestauranteCRUD.crearTodasLasTablas();
		} else if (opc == 2) {
			System.out.println("Introduce el nombre de la tabla (mesa, producto, factura, pedido):");
			nombreTabla = sc.nextLine().toLowerCase();

			try {
				if (RestauranteCRUD.puedeCrearTabla(nombreTabla)) {
					switch (nombreTabla) {
					case "mesa" -> RestauranteCRUD.crearTablaMesa();
					case "producto" -> RestauranteCRUD.crearTablaProducto();
					case "factura" -> RestauranteCRUD.crearTablaFactura();
					case "pedido" -> RestauranteCRUD.crearTablaPedido();
					default -> System.out.println("Tabla no válida.");
					}
					System.out.println("Tabla " + nombreTabla + " creada correctamente.");
				} else {
					System.out.println("No se puede crear la tabla " + nombreTabla
							+ " porque depende de otras tablas que no existen.");
				}

			} catch (SQLException e) {
				System.out.println("Error al crear la tabla: " + e.getMessage());
			}
		} else {
			System.out.println("Opción no válida");
		}
	}

	public static void gestionInsentarMesa() {
		int numComensales;
		
		System.out.println("----- INSERTAR MESA -----");
		System.out.print("Número de comensales: ");
		numComensales = sc.nextInt();
		sc.nextLine();
		
		System.out.print("¿Tiene reserva? (1 = Sí, 0 = No): ");
		int reserva = sc.nextInt();
		sc.nextLine();

		if (RestauranteCRUD.insertarMesa(numComensales, reserva)) {
			System.out.println("Mesa insertada correctamente.");
		} else {
			System.out.println("Error al insertar mesa.");
		}
	}
	
	public static void gestionInsertarProducto() {
		String nombre = "";
		double precio;
		
		System.out.println("---- INSERTAR PRODUCTO -----");
		System.out.println("Nombre del producto: ");
		nombre = sc.nextLine();
		
		System.out.println("Precio del producto: ");
		precio = sc.nextDouble();
		sc.nextLine();
		
		if (RestauranteCRUD.insertarProducto(nombre, precio)) {
			System.out.println("Producto insertado correctamente");
		} else {
			System.out.println("Error al insertar producto.");
		}
	}
	
	public static void gestionInsertarFactura() {
		int idMesa;
		String tipoPago = "";
		double importe;
		
		System.out.println("----- INSERTAR FACTURA -----");
		System.out.println("ID de mesa: ");
		idMesa = sc.nextInt();
		sc.nextLine();
		
		System.out.println("Tipo de pago: ");
		tipoPago = sc.nextLine();
		
		System.out.println("Importe de factura: ");
		importe = sc.nextDouble();
		sc.nextLine();
		
		if (RestauranteCRUD.insertarFactura(idMesa, tipoPago, importe)) {
			System.out.println("Factura insertada correctamente");
		} else {
			System.out.println("Error al insertar factura.");
		}
	}
	
	public static void gestionInsertarPedido() {
		int idFactura;
		int idProducto;
		int cantidad;
		
		System.out.println("----- INSERTAR PEDIDO -----");
		System.out.println("ID de Factura: ");
		idFactura = sc.nextInt();
		sc.nextLine();
		
		System.out.println("ID de Producto: ");
		idProducto = sc.nextInt();
		sc.nextLine();
		
		System.out.println("Cantidad de pedido: ");
		cantidad = sc.nextInt();
		sc.nextLine();
		
		if (RestauranteCRUD.insertarPedido(idFactura, idProducto, cantidad)) {
			System.out.println("Pedido insertado correctamente");
		} else {
			System.out.println("Error al insertar pedido.");
		}
	}

	public static void menuListar() {
	    System.out.println("¿Qué tabla quieres listar?");
	    System.out.println("1. Mesa");
	    System.out.println("2. Producto");
	    System.out.println("3. Factura");
	    System.out.println("4. Pedido");
	    System.out.print("Opción: ");

	    int opcTabla = sc.nextInt();
	    sc.nextLine(); // limpiar buffer

	    String tabla = "";
	    switch (opcTabla) {
	        case 1 -> tabla = "Mesa";
	        case 2 -> tabla = "Producto";
	        case 3 -> tabla = "Factura";
	        case 4 -> tabla = "Pedido";
	        default -> { 
	            System.out.println("Tabla no válida.");
	            return;
	        }
	    }

	    System.out.println("¿Quieres filtrar?");
	    System.out.println("1. Sí");
	    System.out.println("2. No");
	    System.out.print("Opción: ");

	    int opcFiltro = sc.nextInt();
	    sc.nextLine();

	    String campo = "";
	    String operacion = "";
	    String valor = "";

	    if (opcFiltro == 1) {
	        System.out.print("Campo por el que filtrar: ");
	        campo = sc.nextLine();

	        System.out.print("Operación (=, <, >, LIKE): ");
	        operacion = sc.nextLine().toUpperCase();

	        System.out.print("Valor: ");
	        valor = sc.nextLine();
	    }

	    // Llamamos al CRUD
	    String resultado = RestauranteCRUD.listar(tabla, campo, operacion, valor);

	    // Mostrar resultados
	    System.out.println("----- RESULTADO -----");
	    System.out.println(resultado);
	}
	
	public static void modificar() {

	    System.out.println("Tabla:");
	    String tabla = sc.nextLine();

	    System.out.println("Campo filtro:");
	    String campoFiltro = sc.nextLine();

	    System.out.println("Valor del filtro:");
	    String valorFiltro = sc.nextLine();

	    System.out.println("Campo a modificar:");
	    String campoModificar = sc.nextLine();

	    System.out.println("Nuevo valor:");
	    String nuevoValor = sc.nextLine();

	    boolean modificado = RestauranteCRUD.modificar(tabla, campoFiltro, valorFiltro, campoModificar, nuevoValor);

	    if (!modificado) {
	        System.out.println("No se modificó ningún registro.");
	        return;
	    }

	    System.out.println("¿Confirmar cambios? (s/n)");
	    String resp = sc.nextLine();

	    try (Connection con = Conexion.conexionBD()) {
	        if (resp.equalsIgnoreCase("s")) {
	            con.commit();
	            System.out.println("Cambios guardados.");
	        } else {
	            con.rollback();
	            System.out.println("Cambios deshechos.");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	public static void listarTablaCompleta(Connection con, String tabla) {

	    if (con == null) {
	        System.out.println("ERROR: la conexión es null.");
	        return;
	    }

	    try {
	        String sql = "SELECT * FROM " + tabla;
	        Statement st = con.createStatement();
	        ResultSet rs = st.executeQuery(sql);

	        while (rs.next()) {
	            int cols = rs.getMetaData().getColumnCount();

	            for (int i = 1; i <= cols; i++) {
	                System.out.print(rs.getMetaData().getColumnName(i) + ": " + rs.getString(i) + "  ");
	            }
	            System.out.println();
	        }

	    } catch (SQLException e) {
	        System.out.println("Error al listar tabla completa: " + e.getMessage());
	    }
	}


	
	public static void borrarDesdeMain(Connection con, RestauranteCRUD crud) {

	    System.out.println("¿Qué tabla quieres borrar?");
	    String tabla = sc.nextLine();

	    System.out.println("¿Quieres borrar toda la tabla? (s/n)");
	    boolean borrarTodo = sc.nextLine().equalsIgnoreCase("s");

	    String campo = "";
	    String valor = "";

	    if (!borrarTodo) {
	        System.out.println("Introduce el campo por el que borrar:");
	        campo = sc.nextLine();

	        System.out.println("Introduce el valor que debe tener:");
	        valor = sc.nextLine();
	    }

	    boolean exito = crud.borrar(con, tabla, campo, valor, borrarTodo);

	    if (!exito) {
	        System.out.println("No se pudo borrar. Revisa claves foráneas.");
	        try { con.rollback(); } catch (SQLException e) {}
	        return;
	    }

	    System.out.println("Datos tras el borrado:");
	    listarTabla(con, tabla);

	    System.out.println("¿Confirmar cambios? (s/n)");
	    if (sc.nextLine().equalsIgnoreCase("s")) {
	        try { con.commit(); System.out.println("Cambios guardados."); } catch (SQLException e) {}
	    } else {
	        try { con.rollback(); System.out.println("Cambios deshechos."); } catch (SQLException e) {}
	    }
	}
	
	public static void listarTabla(Connection con, String tabla) {
	    try {
	        String sql = "SELECT * FROM " + tabla;
	        PreparedStatement ps = con.prepareStatement(sql);
	        ResultSet rs = ps.executeQuery();

	        ResultSetMetaData meta = rs.getMetaData();
	        int columnas = meta.getColumnCount();

	        while (rs.next()) {
	            for (int i = 1; i <= columnas; i++) {
	                System.out.print(meta.getColumnName(i) + ": " + rs.getString(i) + "  ");
	            }
	            System.out.println();
	        }

	    } catch (SQLException e) {
	        System.out.println("Error al listar.");
	    }
	}

	public static void eliminarTablasDesdeMain(Connection con, RestauranteCRUD crud) {
	    System.out.println("¿Quieres eliminar todas las tablas? (s/n)");
	    boolean eliminarTodo = sc.nextLine().equalsIgnoreCase("s");

	    String tabla = "";
	    if (!eliminarTodo) {
	        System.out.println("Introduce el nombre de la tabla a eliminar:");
	        tabla = sc.nextLine();
	    }

	    boolean exito = crud.eliminarTabla(con, tabla, eliminarTodo);

	    if (!exito) {
	        System.out.println("No se pudo eliminar la tabla. Puede haber restricciones de integridad.");
	        try { con.rollback(); } catch (SQLException e) {}
	        return;
	    }

	    System.out.println("Tabla(s) eliminada(s) correctamente.");

	    // Confirmar cambios
	    System.out.println("¿Confirmar cambios? (s/n)");
	    if (sc.nextLine().equalsIgnoreCase("s")) {
	        try { con.commit(); System.out.println("Cambios guardados."); } catch (SQLException e) {}
	    } else {
	        try { con.rollback(); System.out.println("Cambios deshechos."); } catch (SQLException e) {}
	    }
	}


}