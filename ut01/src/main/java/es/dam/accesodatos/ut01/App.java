
package es.dam.accesodatos.ut01;

import es.dam.accesodatos.ut01.dao.AbstractFactoryDAO;
import es.dam.accesodatos.ut01.dao.ConexionDB;
import es.dam.accesodatos.ut01.dao.PedidoDAO;
import es.dam.accesodatos.ut01.dao.ProductoDAO;
import es.dam.accesodatos.ut01.model.Pedido;
import es.dam.accesodatos.ut01.model.Producto;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.Scanner;

public class App {

    static void main(String[] args) {

        try (Connection conexion = ConexionDB.obtenerConexion();
             Scanner sc = new Scanner(System.in)) {

            ProductoDAO productos = AbstractFactoryDAO.productos().crear(conexion);
            PedidoDAO pedidos = AbstractFactoryDAO.pedidos().crear(conexion);

            ejecutarMenu(sc, productos, pedidos);

        } catch (Exception e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }

    private static void ejecutarMenu(Scanner sc, ProductoDAO productos, PedidoDAO pedidos) {

        int opcion;

        do {
            mostrarMenu();

            try {
                opcion = leerEntero(sc, "Opción: ");

                switch (opcion) {
                    case 1 -> insertarProducto(sc, productos);
                    case 2 -> consultarProducto(sc, productos);
                    case 3 -> actualizarStock(sc, productos);
                    case 4 -> insertarPedido(sc, pedidos);
                    case 5 -> listarPedidos(pedidos);
                    case 6 -> System.out.println("Hasta pronto.");
                    default -> System.out.println("Opción no válida.");
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                opcion = 0;
            }

        } while (opcion != 6);
    }

    private static void mostrarMenu() {
        System.out.print("""
                
                ===== GESTIÓN DE PRODUCTOS Y PEDIDOS =====
                
                1. Insertar producto
                2. Consultar producto por ID
                3. Actualizar stock
                4. Insertar pedido
                5. Listar pedidos
                6. Salir
                
                """);
    }

    private static void insertarProducto(Scanner sc, ProductoDAO productos) throws Exception {

        System.out.print("Nombre: ");
        String nombre = sc.nextLine();

        BigDecimal precio = leerDecimal(sc, "Precio: ");
        int stock = leerEntero(sc, "Stock: ");

        Producto producto = new Producto(null, nombre, precio, stock);
        productos.insertar(producto);


        System.out.println("Producto insertado correctamente.");
        System.out.println("ID generado: " + producto.getIdProducto());
    }

    private static void consultarProducto(Scanner sc, ProductoDAO productos) throws Exception {

        int id = leerEntero(sc, "ID del producto: ");

        Producto producto = productos.buscarPorId(id);

        System.out.println(producto == null ? "Producto no encontrado." : producto);
    }

    private static void actualizarStock(Scanner sc, ProductoDAO productos) throws Exception {
        int id = leerEntero(sc, "ID del producto: ");
        int stock = leerEntero(sc, "Nuevo stock: ");

        boolean actualizado = productos.actualizarStock(id, stock);

        System.out.println(actualizado
                ? "Stock actualizado correctamente."
                : "Producto no encontrado.");
    }

    private static void insertarPedido(Scanner sc, PedidoDAO pedidos) throws Exception {

        int idProducto = leerEntero(sc, "ID del producto: ");
        int cantidad = leerEntero(sc, "Cantidad: ");

        Pedido pedido = new Pedido(null, LocalDate.now(), cantidad, idProducto, null);

        pedidos.insertarYReducirStock(pedido);

        System.out.println("Pedido insertado y stock reducido.");
    }

    private static void listarPedidos(PedidoDAO pedidos) throws Exception {
        pedidos.listar().forEach(System.out::println);
    }

    private static int leerEntero(Scanner sc, String mensaje) {
        System.out.print(mensaje);
        return Integer.parseInt(sc.nextLine());
    }

    private static BigDecimal leerDecimal(Scanner sc, String mensaje) {
        System.out.print(mensaje);
        return new BigDecimal(sc.nextLine());
    }
}
