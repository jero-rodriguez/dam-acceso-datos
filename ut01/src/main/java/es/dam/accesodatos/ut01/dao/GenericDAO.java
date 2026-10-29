package es.dam.accesodatos.ut01.dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Operaciones comunes que comparten todos los DAO.
 */
public interface GenericDAO<T> {
    void insertar(T entidad) throws SQLException;

    T buscarPorId(int id) throws SQLException;

    List<T> listar() throws SQLException;
}
