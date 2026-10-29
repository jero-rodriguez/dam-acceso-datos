CREATE DATABASE IF NOT EXISTS tiendadb;

USE tiendadb;

CREATE TABLE IF NOT EXISTS productos
(
    id_producto INT auto_increment PRIMARY KEY,
    nombre      VARCHAR (150) NOT NULL,
    precio      DECIMAL (10, 2) NOT NULL,
    stock       INT NOT NULL DEFAULT 0
    );

CREATE TABLE IF NOT EXISTS pedidos
(
    id_pedido    INT auto_increment PRIMARY KEY,
    fecha_pedido DATE NOT NULL,
    cantidad     INT NOT NULL,
    id_producto  INT NOT NULL,
    CONSTRAINT fk_pedido_producto FOREIGN KEY ( id_producto ) REFERENCES
    productos ( id_producto )
    );