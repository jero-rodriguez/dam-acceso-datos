package es.dam.accesodatos.ut01.dao;

import es.dam.accesodatos.ut01.model.Pedido;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO implements GenericDAO<Pedido> {
    private final Connection conexion;

    public PedidoDAO(Connection conexion) {
        this.conexion = conexion;
    }

    @Override
    public void insertar(Pedido pedido) throws SQLException {
        insertarYReducirStock(pedido);
    }

    public void insertarYReducirStock(Pedido pedido) throws SQLException {
        boolean autoCommit = conexion.getAutoCommit();
        try {
            conexion.setAutoCommit(false);
            int actualizado;
            try (PreparedStatement ps = conexion.prepareStatement("UPDATE productos SET stock = stock - ? WHERE id_producto = ? AND stock >= ?")) {
                ps.setInt(1, pedido.getCantidad());
                ps.setInt(2, pedido.getIdProducto());
                ps.setInt(3, pedido.getCantidad());
                actualizado = ps.executeUpdate();
            }
            if (actualizado != 1) throw new SQLException("Producto inexistente o stock insuficiente");
            try (PreparedStatement ps = conexion.prepareStatement("INSERT INTO pedidos(fecha_pedido, cantidad, id_producto) VALUES (?, ?, ?)")) {
                ps.setDate(1, Date.valueOf(pedido.getFechaPedido()));
                ps.setInt(2, pedido.getCantidad());
                ps.setInt(3, pedido.getIdProducto());
                ps.executeUpdate();
            }
            conexion.commit();
        } catch (SQLException | RuntimeException e) {
            conexion.rollback();
            throw e;
        } finally {
            conexion.setAutoCommit(autoCommit);
        }
    }

    @Override
    public Pedido buscarPorId(int id) throws SQLException {
        String sql = "SELECT pe.id_pedido, pe.fecha_pedido, pe.cantidad, pe.id_producto, pr.nombre FROM pedidos pe JOIN productos pr ON pe.id_producto = pr.id_producto WHERE pe.id_pedido = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new Pedido(rs.getInt(1), rs.getDate(2).toLocalDate(), rs.getInt(3), rs.getInt(4), rs.getString(5));
            }
        }
    }

    public List<Pedido> listar() throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT pe.id_pedido, pe.fecha_pedido, pe.cantidad, pe.id_producto, pr.nombre FROM pedidos pe JOIN productos pr ON pe.id_producto = pr.id_producto ORDER BY pe.id_pedido";
        try (PreparedStatement ps = conexion.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                pedidos.add(new Pedido(rs.getInt(1), rs.getDate(2).toLocalDate(), rs.getInt(3), rs.getInt(4), rs.getString(5)));
        }
        return pedidos;
    }
}
