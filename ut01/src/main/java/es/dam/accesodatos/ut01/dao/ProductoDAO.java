package es.dam.accesodatos.ut01.dao;

import es.dam.accesodatos.ut01.model.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO implements GenericDAO<Producto> {

    private final Connection conexion;

    public ProductoDAO(Connection conexion) {
        this.conexion = conexion;
    }

    @Override
    public void insertar(Producto p) throws SQLException {
        String sql = "INSERT INTO productos(nombre, precio, stock) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conexion.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getNombre());
            ps.setBigDecimal(2, p.getPrecio());
            ps.setInt(3, p.getStock());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setIdProducto(rs.getInt(1));
                } else {
                    throw new SQLException("No se pudo recuperar el ID del producto.");
                }
            }
        }
    }

    @Override
    public Producto buscarPorId(int id) throws SQLException {
        String sql = "SELECT id_producto, nombre, precio, stock FROM productos WHERE id_producto = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new Producto(rs.getInt("id_producto"), rs.getString("nombre"), rs.getBigDecimal("precio"), rs.getInt("stock"));
            }
        }
    }

    @Override
    public List<Producto> listar() throws SQLException {
        List<Producto> productos = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement("SELECT id_producto, nombre, precio, stock FROM productos ORDER BY id_producto"); ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                productos.add(new Producto(rs.getInt(1), rs.getString(2), rs.getBigDecimal(3), rs.getInt(4)));
        }
        return productos;
    }

    public boolean actualizarStock(int id, int nuevoStock) throws SQLException {
        if (nuevoStock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }

        try (PreparedStatement ps = conexion.prepareStatement("UPDATE productos SET stock = ? WHERE id_producto = ?")) {
            ps.setInt(1, nuevoStock);
            ps.setInt(2, id);
            return ps.executeUpdate() == 1;
        }
    }
}
