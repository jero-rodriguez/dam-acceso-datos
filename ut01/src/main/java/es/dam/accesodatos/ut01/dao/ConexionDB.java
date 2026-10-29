package es.dam.accesodatos.ut01.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class ConexionDB {
    private static final String FICHERO_CONFIGURACION = "db.properties";

    private ConexionDB() {
    }

    public static Connection obtenerConexion() throws SQLException {
        Properties propiedades = new Properties();
        try (InputStream entrada = ConexionDB.class.getClassLoader()
                .getResourceAsStream(FICHERO_CONFIGURACION)) {
            if (entrada == null) {
                throw new SQLException("No se encontró " + FICHERO_CONFIGURACION + " en resources");
            }
            propiedades.load(entrada);
        } catch (IOException e) {
            throw new SQLException("No se pudo leer " + FICHERO_CONFIGURACION, e);
        }

        String url = propiedades.getProperty("db.url");
        String usuario = propiedades.getProperty("db.usuario");
        String password = propiedades.getProperty("db.password");
        if (url == null || usuario == null || password == null) {
            throw new SQLException("Faltan propiedades db.url, db.usuario o db.password");
        }
        return DriverManager.getConnection(url, usuario, password);
    }
}
