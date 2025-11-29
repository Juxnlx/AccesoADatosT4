package principal;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

import conexion.Conexion;
import crud.RestauranteCRUD;

public class Principal {

    /** Scanner para leer datos desde consola */
    private static final Scanner sc = new Scanner(System.in);

 
    public static void main(String[] args) {
    	// Opción seleccionada por el usuario
    	int opc;
        

        do {
            mostrarMenu(); // Mostramos el menú principal
            System.out.print("Introduce una opción --> ");
            opc = sc.nextInt();
            sc.nextLine(); // Limpiamos buffer

            switch (opc) {
                case 1 -> gestionCreacionTablas();
                case 2 -> gestionInsertarMesa();
                case 3 -> gestionInsertarProducto();
                case 4 -> gestionInsertarFactura();
                case 5 -> gestionInsertarPedido();
                case 6 -> gestionListar();
                case 7 -> modificar();
                case 8 -> borrarDesdeMain();
                case 9 -> eliminarTablasDesdeMain();
                case 0 -> System.out.println("Saliendo del programa...");
                default -> System.out.println("Opción no válida.");
            }

        } while (opc != 0);

        sc.close(); // Cerramos Scanner
    }

    /**
     * Muestra el menú principal con todas las opciones disponibles
     */
    public static void mostrarMenu() {
        System.out.println("\n----------MENÚ----------");
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

    /**
     * Gestiona la creación de tablas en la base de datos según la opción del usuario.
     * Permite crear todas las tablas o solo una específica.
     */
    public static void gestionCreacionTablas() {
        System.out.println("\n¿Quieres crear todas las tablas o una tabla concreta?");
        System.out.println("1. Todas las tablas");
        System.out.println("2. Tabla concreta");
        System.out.print("Opción: ");
        int opc = sc.nextInt();
        sc.nextLine();

        if (opc == 1) {
            RestauranteCRUD.crearTodasLasTablas();
        } else if (opc == 2) {
            System.out.print("Introduce el nombre de la tabla (mesa, producto, factura, pedido): ");
            String nombreTabla = sc.nextLine().toLowerCase();

            if (RestauranteCRUD.puedeCrearTabla(nombreTabla)) {
                try (Connection con = Conexion.conexionBD()) {
                    switch (nombreTabla) {
                        case "mesa" -> con.createStatement().executeUpdate(RestauranteCRUD.crearTablaMesa());
                        case "producto" -> con.createStatement().executeUpdate(RestauranteCRUD.crearTablaProducto());
                        case "factura" -> con.createStatement().executeUpdate(RestauranteCRUD.crearTablaFactura());
                        case "pedido" -> con.createStatement().executeUpdate(RestauranteCRUD.crearTablaPedido());
                        default -> System.out.println("Tabla no válida.");
                    }
                    System.out.println("Tabla " + nombreTabla + " creada correctamente.");
                } catch (SQLException e) {
                    System.out.println("Error al crear la tabla: " + e.getMessage());
                }
            } else {
                System.out.println("No se puede crear la tabla " + nombreTabla +
                        " porque depende de otras tablas que no existen.");
            }
        } else {
            System.out.println("Opción no válida");
        }
    }

    /**
     * Gestiona la inserción de una nueva mesa.
     * Solicita número de comensales y si tiene reserva.
     */
    public static void gestionInsertarMesa() {
        System.out.println("\n----- INSERTAR MESA -----");
        System.out.print("Número de comensales: ");
        int numComensales = sc.nextInt();
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

    /**
     * Gestiona la inserción de un nuevo producto.
     * Solicita nombre y precio del producto.
     */
    public static void gestionInsertarProducto() {
        System.out.println("\n----- INSERTAR PRODUCTO -----");
        System.out.print("Nombre del producto: ");
        String nombre = sc.nextLine();
        System.out.print("Precio del producto: ");
        double precio = sc.nextDouble();
        sc.nextLine();

        if (RestauranteCRUD.insertarProducto(nombre, precio)) {
            System.out.println("Producto insertado correctamente.");
        } else {
            System.out.println("Error al insertar producto.");
        }
    }

    /**
     * Gestiona la inserción de una nueva factura.
     * Solicita mesa, tipo de pago e importe.
     */
    public static void gestionInsertarFactura() {
        System.out.println("\n----- INSERTAR FACTURA -----");
        System.out.println("Mesas disponibles:");
        System.out.println(RestauranteCRUD.listar("Mesa", null, null));

        System.out.print("ID de mesa: ");
        int idMesa = sc.nextInt();
        sc.nextLine();
        System.out.print("Tipo de pago (Efectivo/Tarjeta): ");
        String tipoPago = sc.nextLine();
        System.out.print("Importe de factura: ");
        double importe = sc.nextDouble();
        sc.nextLine();

        if (RestauranteCRUD.insertarFactura(idMesa, tipoPago, importe)) {
            System.out.println("Factura insertada correctamente.");
        } else {
            System.out.println("Error al insertar factura. Verifica que la mesa existe.");
        }
    }

    /**
     * Gestiona la inserción de un nuevo pedido.
     * Solicita ID de factura, ID de producto y cantidad.
     */
    public static void gestionInsertarPedido() {
        System.out.println("\n----- INSERTAR PEDIDO -----");
        System.out.println("Facturas disponibles:");
        System.out.println(RestauranteCRUD.listar("Factura", null, null));
        System.out.print("ID de Factura: ");
        int idFactura = sc.nextInt();
        sc.nextLine();
        System.out.println("Productos disponibles:");
        System.out.println(RestauranteCRUD.listar("Producto", null, null));
        System.out.print("ID de Producto: ");
        int idProducto = sc.nextInt();
        sc.nextLine();
        System.out.print("Cantidad: ");
        int cantidad = sc.nextInt();
        sc.nextLine();

        if (RestauranteCRUD.insertarPedido(idFactura, idProducto, cantidad)) {
            System.out.println("Pedido insertado correctamente.");
        } else {
            System.out.println("Error al insertar pedido. Verifica que la factura y producto existen.");
        }
    }

    /**
     * Gestiona la visualización de los registros de la base de datos.
     * Permite filtrar por campo si se desea.
     */
    public static void gestionListar() {
        System.out.println("\n¿Qué tabla quieres listar?");
        System.out.println("1. Mesa");
        System.out.println("2. Producto");
        System.out.println("3. Factura");
        System.out.println("4. Pedido");
        System.out.print("Opción: ");
        int opc = sc.nextInt();
        sc.nextLine();

        String tabla = switch (opc) {
            case 1 -> "Mesa";
            case 2 -> "Producto";
            case 3 -> "Factura";
            case 4 -> "Pedido";
            default -> null;
        };

        if (tabla == null) {
            System.out.println("Opción no válida.");
            return;
        }

        System.out.print("¿Quieres filtrar algún campo? (s/n): ");
        boolean filtrar = sc.nextLine().equalsIgnoreCase("s");
        String campo = null, valor = null;

        if (filtrar) {
            System.out.print("Campo: ");
            campo = sc.nextLine();
            System.out.print("Valor: ");
            valor = sc.nextLine();
        }

        System.out.println("\n----- " + tabla.toUpperCase() + " -----");
        System.out.println(RestauranteCRUD.listar(tabla, campo, valor));
    }

    /**
     * Gestiona la modificación de un registro.
     * Solicita tabla, campo filtro, valor filtro, campo a modificar y nuevo valor.
     */
    public static void modificar() {
        System.out.println("\n----- MODIFICAR -----");
        System.out.print("Tabla: ");
        String tabla = sc.nextLine();
        System.out.print("Campo filtro: ");
        String campoFiltro = sc.nextLine();
        System.out.print("Valor del filtro: ");
        String valorFiltro = sc.nextLine();
        System.out.print("Campo a modificar: ");
        String campoModificar = sc.nextLine();
        System.out.print("Nuevo valor: ");
        String nuevoValor = sc.nextLine();

        try (Connection con = Conexion.conexionBD()) {
            boolean modificado = RestauranteCRUD.modificar(con, tabla, campoFiltro, valorFiltro, campoModificar, nuevoValor);
            if (!modificado) {
                System.out.println("No se modificó ningún registro.");
                con.rollback();
                return;
            }
            System.out.println("\nDatos después de la modificación:");
            System.out.println(RestauranteCRUD.listar(tabla, null, null));

            System.out.print("\n¿Confirmar cambios? (s/n): ");
            if (sc.nextLine().equalsIgnoreCase("s")) {
                con.commit();
                System.out.println("Cambios guardados.");
            } else {
                con.rollback();
                System.out.println("Cambios deshechos.");
            }
        } catch (SQLException e) {
            System.out.println("Error al modificar: " + e.getMessage());
        }
    }

    /**
     * Gestiona el borrado de registros.
     * Puede borrar toda la tabla o registros filtrados.
     */
    public static void borrarDesdeMain() {
        System.out.println("\n----- BORRAR -----");
        System.out.print("Tabla: ");
        String tabla = sc.nextLine();
        System.out.print("¿Borrar toda la tabla? (s/n): ");
        boolean borrarTodo = sc.nextLine().equalsIgnoreCase("s");

        String campo = null, valor = null;
        if (!borrarTodo) {
            System.out.print("Campo filtro: ");
            campo = sc.nextLine();
            System.out.print("Valor filtro: ");
            valor = sc.nextLine();
        }

        try (Connection con = Conexion.conexionBD()) {
            boolean exito = RestauranteCRUD.borrar(con, tabla, campo, valor, borrarTodo);
            if (!exito) {
                System.out.println("No se pudo borrar. Revisa las claves foráneas.");
                con.rollback();
                return;
            }
            System.out.println("\nDatos tras el borrado:");
            System.out.println(RestauranteCRUD.listar(tabla, null, null));

            System.out.print("¿Confirmar cambios? (s/n): ");
            if (sc.nextLine().equalsIgnoreCase("s")) {
                con.commit();
                System.out.println("Cambios guardados.");
            } else {
                con.rollback();
                System.out.println("Cambios deshechos.");
            }
        } catch (SQLException e) {
            System.out.println("Error al borrar: " + e.getMessage());
        }
    }

    /**
     * Gestiona la eliminación de tablas.
     * Puede eliminar todas las tablas o una específica.
     */
    public static void eliminarTablasDesdeMain() {
        System.out.println("\n----- ELIMINAR TABLA -----");
        System.out.print("¿Eliminar todas las tablas? (s/n): ");
        boolean eliminarTodo = sc.nextLine().equalsIgnoreCase("s");

        String tabla = null;
        if (!eliminarTodo) {
            System.out.print("Nombre de la tabla: ");
            tabla = sc.nextLine();
        }

        try (Connection con = Conexion.conexionBD()) {
            boolean exito = RestauranteCRUD.eliminarTabla(con, tabla, eliminarTodo);
            if (!exito) {
                System.out.println("No se pudo eliminar la tabla. Puede haber restricciones de integridad.");
                con.rollback();
                return;
            }
            System.out.println("Tabla(s) eliminada(s) correctamente.");

            System.out.print("¿Confirmar cambios? (s/n): ");
            if (sc.nextLine().equalsIgnoreCase("s")) {
                con.commit();
                System.out.println("Cambios guardados.");
            } else {
                con.rollback();
                System.out.println("Cambios deshechos.");
            }
        } catch (SQLException e) {
            System.out.println("Error al eliminar tablas: " + e.getMessage());
        }
    }
}
