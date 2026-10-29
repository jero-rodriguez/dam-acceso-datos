package es.dam.accesodatos.ut01.dao;

import java.sql.Connection;

/**
 * Factoría abstracta genérica para crear DAOs usando una conexión compartida.
 */
public abstract class AbstractFactoryDAO<T> {

    public static AbstractFactoryDAO<ProductoDAO> productos() {
        return new AbstractFactoryDAO<>() {
            @Override
            public ProductoDAO crear(Connection conexion) {
                return new ProductoDAO(conexion);
            }
        };
    }

    public static AbstractFactoryDAO<PedidoDAO> pedidos() {
        return new AbstractFactoryDAO<>() {
            @Override
            public PedidoDAO crear(Connection conexion) {
                return new PedidoDAO(conexion);
            }
        };
    }

    public abstract T crear(Connection conexion);
}
