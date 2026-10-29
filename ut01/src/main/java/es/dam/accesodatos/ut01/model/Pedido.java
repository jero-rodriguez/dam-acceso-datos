package es.dam.accesodatos.ut01.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    private Integer idPedido;
    private LocalDate fechaPedido;
    private Integer cantidad;
    private Integer idProducto;
    private String nombreProducto;
}
