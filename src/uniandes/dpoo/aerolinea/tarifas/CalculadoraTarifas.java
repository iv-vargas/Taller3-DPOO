package uniandes.dpoo.aerolinea.tarifas;

import uniandes.dpoo.aerolinea.modelo.Ruta;
import uniandes.dpoo.aerolinea.modelo.Aeropuerto;
import uniandes.dpoo.aerolinea.modelo.Vuelo;
import uniandes.dpoo.aerolinea.modelo.cliente.Cliente;

public abstract class CalculadoraTarifas {

    /**
     * El porcentaje constante de impuesto aplicado a las tarifas (28%).
     */
    public static final double IMPUESTO = 0.28;

    /**
     * Calcula la tarifa total para un cliente en un vuelo específico, 
     * incluyendo costo base con descuento aplicado más los impuestos.
     * 
     * @param vuelo El vuelo sobre el cual se calculará la tarifa.
     * @param cliente El cliente que compra el tiquete.
     * @return El valor total final de la tarifa redondeado a entero.
     */
    public int calcularTarifa(Vuelo vuelo, Cliente cliente) {
        int costoBase = calcularCostoBase(vuelo, cliente);
        double porcentajeDescuento = calcularPorcentajeDescuento(cliente);
        
        int costoConDescuento = (int) (costoBase -(costoBase * porcentajeDescuento));
        int valorImpuestos = calcularValorImpuestos(costoConDescuento);
        
        return costoConDescuento + valorImpuestos;
    }
    /**
     * Método abstracto para calcular el costo base según la temporada y tipo de cliente.
     */
    protected abstract int calcularCostoBase(Vuelo vuelo, Cliente cliente);

    /**
     * Método abstracto para obtener el porcentaje de descuento que le corresponde al cliente.
     */
    protected abstract double calcularPorcentajeDescuento(Cliente cliente);

    /**
     * Calcula la distancia en kilómetros entre el aeropuerto origen y el aeropuerto destino.
     * Utiliza el método estático de la clase Aeropuerto para calcular la distancia geográfica.
     * 
     * @param ruta La ruta del vuelo.
     * @return Distancia calculada en kilómetros.
     */
    protected int calcularDistanciaVuelo(Ruta ruta) {
    	Aeropuerto origen = ruta.getOrigen() ;
    	Aeropuerto destino = ruta.getDestino() ;
    	return Aeropuerto.calcularDistancia(origen, destino);
    	
    }

    /**
     * Calcula el valor de los impuestos aplicables sobre un costo base.
     * 
     * @param costoBase El costo base (después de descuentos) sobre el cual aplicar el impuesto.
     * @return El valor total de los impuestos redondeado a entero.
     */
    protected int calcularValorImpuestos(int costoBase) {
        return (int) Math.round(costoBase * IMPUESTO);
    }
    
}