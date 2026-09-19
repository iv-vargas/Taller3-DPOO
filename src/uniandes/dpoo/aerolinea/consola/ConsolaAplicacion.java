package uniandes.dpoo.aerolinea.consola;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import uniandes.dpoo.aerolinea.exceptions.InformacionInconsistenteException;
import uniandes.dpoo.aerolinea.exceptions.VueloSobrevendidoException;
import uniandes.dpoo.aerolinea.modelo.Aerolinea;
import uniandes.dpoo.aerolinea.modelo.Aeropuerto;
import uniandes.dpoo.aerolinea.modelo.Avion;
import uniandes.dpoo.aerolinea.modelo.Ruta;
import uniandes.dpoo.aerolinea.modelo.Vuelo;
import uniandes.dpoo.aerolinea.modelo.cliente.Cliente;
import uniandes.dpoo.aerolinea.persistencia.CentralPersistencia;
import uniandes.dpoo.aerolinea.persistencia.TipoInvalidoException;
import uniandes.dpoo.aerolinea.tiquetes.Tiquete;

/**
 * Consola de la aplicación de la aerolínea.
 * 
 * Permite cargar y salvar la aerolínea (JSON), programar vuelos, vender tiquetes, registrar vuelos realizados y consultar el saldo pendiente de un cliente. Todas las
 * operaciones le muestran al usuario una respuesta completa: lo que se hizo o, si no fue posible, la razón.
 */
public class ConsolaAplicacion extends ConsolaBasica
{
    private static final String CARPETA_DATOS = "./datos/";

    private static final String ARCHIVO_AEROLINEA = "aerolinea_1.json";

    private static final String ARCHIVO_AEROLINEA_SALVADA = "aerolinea_salvada.json";

    private static final String ARCHIVO_TIQUETES = "tiquetes.json";

    private static final String ARCHIVO_TIQUETES_SALVADO = "tiquetes_salvado.json";

    private static final Locale LOCALE_CO = Locale.forLanguageTag( "es-CO" );

    private Aerolinea unaAerolinea;

    
    private boolean aerolineaCargada;

    public ConsolaAplicacion( )
    {
        unaAerolinea = new Aerolinea( );
        aerolineaCargada = false;
    }


    public void correrAplicacion( )
    {

        String[] opciones = { "Cargar aerolínea (JSON)", "Salvar aerolínea (JSON)", "Cargar clientes y tiquetes (JSON)", "Salvar clientes y tiquetes (JSON)",
                "Programar un vuelo", "Vender tiquetes", "Registrar vuelo realizado", "Consultar saldo pendiente de un cliente",
                "Ver información de la aerolínea", "Salir" };

        boolean continuar = true;
        while( continuar )
        {
            int opcion = mostrarMenu( "Menú principal", opciones );
            try
            {
                if (opcion == 1)
                {
                    cargarAerolinea();
                }
                else if (opcion == 2)
                {
                    salvarAerolinea();
                }
                else if (opcion == 3)
                {
                    cargarTiquetes();
                }
                else if (opcion == 4)
                {
                    salvarTiquetes();
                }
                else if (opcion == 5)
                {
                    programarVuelo();
                }
                else if (opcion == 6)
                {
                    venderTiquetes();
                }
                else if (opcion == 7)
                {
                    registrarVueloRealizado();
                }
                else if (opcion == 8)
                {
                    consultarSaldoPendiente();
                }
                else if (opcion == 9)
                {
                    mostrarInformacion();
                }
                else if (opcion == 10)
                {
                    System.out.println("\nGracias por usar la aplicación. ¡Hasta pronto!");
                    continuar = false;
                }
            }
            catch (Exception e)
            {        
                System.out.println("\nOcurrió un error inesperado: " + e);
            }
        }
    }


    private void cargarAerolinea( )
    {
        System.out.println( "\n-- Cargar aerolínea (aviones, aeropuertos, rutas y vuelos) --" );
        if( aerolineaCargada )
        {
            System.out.println( "La aerolínea ya fue cargada en esta ejecución. Para cargar otro archivo, cierre y vuelva a abrir la aplicación." );
            return;
        }

        String archivo = pedirArchivo( "Nombre del archivo JSON de la aerolínea", ARCHIVO_AEROLINEA );
        try
        {
            unaAerolinea.cargarAerolinea( archivo, CentralPersistencia.JSON );
            aerolineaCargada = true;
            System.out.println( "\nLa aerolínea se cargó correctamente desde " + archivo + "." );
            mostrarResumen( );
        }
        catch( TipoInvalidoException e )
        {
            System.out.println( "\nTipo de archivo inválido: " + e.getMessage( ) );
        }
        catch( IOException e )
        {
            System.out.println( "\nNo se pudo leer el archivo " + archivo + ": " + e.getMessage( ) );
        }
        catch( InformacionInconsistenteException e )
        {
            System.out.println( "\nEl archivo tiene información inconsistente: " + e.getMessage( ) );
            System.out.println( "La carga se interrumpió. Corrija el archivo y reinicie la aplicación antes de intentarlo de nuevo." );
        }
    }

    private void salvarAerolinea( )
    {
        System.out.println( "\n-- Salvar aerolínea (aviones, aeropuertos, rutas y vuelos) --" );
        String archivo = pedirArchivo( "Nombre del archivo JSON donde se salvará", ARCHIVO_AEROLINEA_SALVADA );
        if( !confirmarSobrescritura( archivo ) )
        {
            System.out.println( "No se salvó nada." );
            return;
        }

        try
        {
            crearCarpetaPadre( archivo );
            unaAerolinea.salvarAerolinea( archivo, CentralPersistencia.JSON );
            System.out.println( "\nLa aerolínea se salvó correctamente en " + archivo + "." );
            mostrarResumen( );
        }
        catch( TipoInvalidoException e )
        {
            System.out.println( "\nTipo de archivo inválido: " + e.getMessage( ) );
        }
        catch( IOException e )
        {
            System.out.println( "\nNo se pudo escribir el archivo " + archivo + ": " + e.getMessage( ) );
        }
    }

    private void cargarTiquetes( )
    {
        System.out.println( "\n-- Cargar clientes y tiquetes --" );
        if( unaAerolinea.getVuelos( ).isEmpty( ) )
        {
            System.out.println( "Antes de cargar los tiquetes debe cargar la aerolínea (opción 1), porque cada tiquete pertenece a un vuelo existente." );
            return;
        }

        String archivo = pedirArchivo( "Nombre del archivo JSON de clientes y tiquetes", ARCHIVO_TIQUETES );
        try
        {
            unaAerolinea.cargarTiquetes( archivo, CentralPersistencia.JSON );
            System.out.println( "\nLos clientes y tiquetes se cargaron correctamente desde " + archivo + "." );
            mostrarResumen( );
        }
        catch( TipoInvalidoException e )
        {
            System.out.println( "\nTipo de archivo inválido: " + e.getMessage( ) );
        }
        catch( IOException e )
        {
            System.out.println( "\nNo se pudo leer el archivo " + archivo + ": " + e.getMessage( ) );
        }
        catch( InformacionInconsistenteException e )
        {
            System.out.println( "\nEl archivo tiene información inconsistente: " + e.getMessage( ) );
        }
    }

    private void salvarTiquetes( )
    {
        System.out.println( "\n-- Salvar clientes y tiquetes --" );
        String archivo = pedirArchivo( "Nombre del archivo JSON donde se salvará", ARCHIVO_TIQUETES_SALVADO );
        if( !confirmarSobrescritura( archivo ) )
        {
            System.out.println( "No se salvó nada." );
            return;
        }

        try
        {
            crearCarpetaPadre( archivo );
            unaAerolinea.salvarTiquetes( archivo, CentralPersistencia.JSON );
            System.out.println( "\nSe salvaron " + unaAerolinea.getClientes( ).size( ) + " clientes y " + unaAerolinea.getTiquetes( ).size( ) + " tiquetes en " + archivo + "." );
        }
        catch( TipoInvalidoException e )
        {
            System.out.println( "\nTipo de archivo inválido: " + e.getMessage( ) );
        }
        catch( IOException e )
        {
            System.out.println( "\nNo se pudo escribir el archivo " + archivo + ": " + e.getMessage( ) );
        }
    }


    private void programarVuelo( )
    {
        System.out.println( "\n-- Programar un vuelo --" );
        if( unaAerolinea.getRutas( ).isEmpty( ) || unaAerolinea.getAviones( ).isEmpty( ) )
        {
            System.out.println( "Para programar un vuelo la aerolínea debe tener rutas y aviones. Cargue primero la aerolínea (opción 1)." );
            return;
        }

        mostrarRutas( );
        mostrarAviones( );
        String fecha = pedirFecha( "\nFecha del vuelo" );
        String codigoRuta = pedirCadenaAlUsuario( "Código de la ruta" ).trim( );
        String nombreAvion = pedirCadenaAlUsuario( "Nombre del avión" ).trim( );

        try
        {
            unaAerolinea.programarVuelo( fecha, codigoRuta, nombreAvion );
            Vuelo vuelo = unaAerolinea.getVuelo( codigoRuta, fecha );
            Ruta ruta = vuelo.getRuta( );
            System.out.println( "\nEl vuelo se programó con éxito:" );
            System.out.println( "  Fecha:    " + vuelo.getFecha( ) );
            System.out.println( "  Ruta:     " + describirRuta( ruta ) );
            System.out.println( "  Duración: " + formatearDuracion( ruta.getDuracion( ) ) );
            System.out.println( "  Avión:    " + describirAvion( vuelo.getAvion( ) ) );
            System.out.println( "  Total de vuelos programados: " + unaAerolinea.getVuelos( ).size( ) );
        }
        catch( Exception e )
        {
            System.out.println( "\nNo se pudo programar el vuelo: " + e.getMessage( ) );
        }
    }

    private void venderTiquetes( )
    {
        System.out.println( "\n-- Vender tiquetes --" );
        if( unaAerolinea.getVuelos( ).isEmpty( ) )
        {
            System.out.println( "No hay vuelos programados. Cargue la aerolínea (opción 1) o programe un vuelo (opción 5)." );
            return;
        }
        if( unaAerolinea.getClientes( ).isEmpty( ) )
        {
            System.out.println( "No hay clientes registrados. Cargue los clientes y tiquetes (opción 3)." );
            return;
        }

        mostrarVuelos( );
        mostrarClientes( );
        String fecha = pedirFecha( "\nFecha del vuelo" );
        String codigoRuta = pedirCadenaAlUsuario( "Código de la ruta" ).trim( );
        String identificadorCliente = pedirCadenaAlUsuario( "Identificador del cliente (nombre de la persona o de la empresa)" ).trim( );
        int cantidad = pedirCantidad( "Cantidad de tiquetes a comprar" );

        try
        {
            int valorTotal = unaAerolinea.venderTiquetes( identificadorCliente, fecha, codigoRuta, cantidad );
            Vuelo vuelo = unaAerolinea.getVuelo( codigoRuta, fecha );
            Cliente cliente = unaAerolinea.getCliente( identificadorCliente );

            System.out.println( "\nLa venta se realizó con éxito:" );
            System.out.println( "  Cliente:            " + cliente.getIdentificador( ) + " (" + cliente.getTipoCliente( ) + ")" );
            System.out.println( "  Vuelo:              " + describirVuelo( vuelo ) );
            System.out.println( "  Tiquetes vendidos:  " + cantidad );
            System.out.println( "  Valor por tiquete:  " + formatearDinero( valorTotal / cantidad ) );
            System.out.println( "  Valor total:        " + formatearDinero( valorTotal ) );
            System.out.println( "  Cupos disponibles:  " + cuposDisponibles( vuelo ) );
        }
        catch( VueloSobrevendidoException e )
        {
            System.out.println( "\nNo se realizó la venta: " + e.getMessage( ) + "." );
            Vuelo vuelo = unaAerolinea.getVuelo( codigoRuta, fecha );
            if( vuelo != null )
            {
                System.out.println( "  Cupos disponibles: " + cuposDisponibles( vuelo ) + " (se solicitó " + ( cantidad == 1 ? "1 tiquete" : cantidad + " tiquetes" ) + ")" );
            }
        }
        catch( Exception e )
        {
            System.out.println( "\nNo se pudo realizar la venta: " + e.getMessage( ) );
        }
    }

    private void registrarVueloRealizado( )
    {
        System.out.println( "\n-- Registrar vuelo realizado --" );
        if( unaAerolinea.getVuelos( ).isEmpty( ) )
        {
            System.out.println( "No hay vuelos programados." );
            return;
        }

        mostrarVuelos( );
        String fecha = pedirFecha( "\nFecha del vuelo" );
        String codigoRuta = pedirCadenaAlUsuario( "Código de la ruta" ).trim( );

        Vuelo vuelo = unaAerolinea.getVuelo( codigoRuta, fecha );
        if( vuelo == null )
        {
            System.out.println( "\nNo existe un vuelo de la ruta " + codigoRuta + " en la fecha " + fecha + "." );
            return;
        }

        int usadosAntes = contarTiquetesUsados( vuelo );
        unaAerolinea.registrarVueloRealizado( fecha, codigoRuta );
        int usadosDespues = contarTiquetesUsados( vuelo );

        System.out.println( "\nEl vuelo quedó registrado como realizado:" );
        System.out.println( "  Vuelo: " + describirVuelo( vuelo ) );
        if( vuelo.getTiquetes( ).isEmpty( ) )
        {
            System.out.println( "  El vuelo no tenía tiquetes vendidos." );
        }
        else
        {
            System.out.println( "  Tiquetes del vuelo:              " + vuelo.getTiquetes( ).size( ) );
            System.out.println( "  Marcados como usados ahora:      " + ( usadosDespues - usadosAntes ) );
            System.out.println( "  Ya estaban usados anteriormente: " + usadosAntes );
        }
    }

    private void consultarSaldoPendiente( )
    {
        System.out.println( "\n-- Consultar saldo pendiente de un cliente --" );
        if( unaAerolinea.getClientes( ).isEmpty( ) )
        {
            System.out.println( "No hay clientes registrados. Cargue los clientes y tiquetes (opción 3)." );
            return;
        }

        mostrarClientes( );
        String identificadorCliente = pedirCadenaAlUsuario( "\nIdentificador del cliente (nombre de la persona o de la empresa)" ).trim( );

        if( !unaAerolinea.existeCliente( identificadorCliente ) )
        {
            System.out.println( "\nNo existe un cliente con el identificador '" + identificadorCliente + "'." );
            return;
        }

        Cliente cliente = unaAerolinea.getCliente( identificadorCliente );
        String saldo = unaAerolinea.consultarSaldoPendienteCliente( identificadorCliente );
        String saldoFormateado;
        try
        {
            saldoFormateado = formatearDinero( Long.parseLong( saldo.trim( ) ) );
        }
        catch( NumberFormatException e )
        {
            saldoFormateado = saldo;
        }

        System.out.println( "\nSaldo pendiente de " + cliente.getIdentificador( ) + " (" + cliente.getTipoCliente( ) + "): " + saldoFormateado );
        System.out.println( "(Suma de lo pagado por los tiquetes que todavía no ha utilizado)" );
    }


    private void mostrarInformacion( )
    {
        System.out.println( "\n=== Información de la aerolínea ===" );
        mostrarAviones( );
        mostrarRutas( );
        mostrarVuelos( );
        mostrarClientes( );
        System.out.println( "\nTiquetes vendidos en total: " + unaAerolinea.getTiquetes( ).size( ) );
    }

    private void mostrarResumen( )
    {
        System.out.println( "  Aviones:  " + unaAerolinea.getAviones( ).size( ) );
        System.out.println( "  Rutas:    " + unaAerolinea.getRutas( ).size( ) );
        System.out.println( "  Vuelos:   " + unaAerolinea.getVuelos( ).size( ) );
        System.out.println( "  Clientes: " + unaAerolinea.getClientes( ).size( ) );
        System.out.println( "  Tiquetes: " + unaAerolinea.getTiquetes( ).size( ) );
    }

    private void mostrarAviones( )
    {
        System.out.println( "\nAviones:" );
        if( unaAerolinea.getAviones( ).isEmpty( ) )
            System.out.println( "  (no hay aviones)" );
        for( Avion avion : unaAerolinea.getAviones( ) )
            System.out.println( "  - " + describirAvion( avion ) );
    }

    private void mostrarRutas( )
    {
        System.out.println( "\nRutas:" );
        if( unaAerolinea.getRutas( ).isEmpty( ) )
            System.out.println( "  (no hay rutas)" );
        for( Ruta ruta : unaAerolinea.getRutas( ) )
            System.out.println( "  - " + describirRuta( ruta ) );
    }

    private void mostrarVuelos( )
    {
        System.out.println( "\nVuelos programados:" );
        if( unaAerolinea.getVuelos( ).isEmpty( ) )
            System.out.println( "  (no hay vuelos)" );
        for( Vuelo vuelo : unaAerolinea.getVuelos( ) )
            System.out.println( "  - " + describirVuelo( vuelo ) );
    }

    private void mostrarClientes( )
    {
        System.out.println( "\nClientes:" );
        if( unaAerolinea.getClientes( ).isEmpty( ) )
            System.out.println( "  (no hay clientes)" );
        for( Cliente cliente : unaAerolinea.getClientes( ) )
            System.out.println( "  - " + cliente.getIdentificador( ) + " (" + cliente.getTipoCliente( ) + ")" );
    }

  
    private String pedirArchivo( String mensaje, String nombrePorDefecto )
    {
        String nombre = pedirCadenaAlUsuario( mensaje + " (Enter para usar '" + nombrePorDefecto + "')" );
        if( nombre == null || nombre.trim( ).isEmpty( ) )
            nombre = nombrePorDefecto;
        nombre = nombre.trim( );

        if( nombre.contains( "/" ) || nombre.contains( "\\" ) )
            return nombre;
        return CARPETA_DATOS + nombre;
    }

   
    private String pedirFecha( String mensaje )
    {
        while( true )
        {
            String fecha = pedirCadenaAlUsuario( mensaje + " (formato AAAA-MM-DD)" ).trim( );
            try
            {
                LocalDate.parse( fecha );
                return fecha;
            }
            catch( DateTimeParseException e )
            {
                System.out.println( "La fecha '" + fecha + "' no es válida. Use el formato AAAA-MM-DD, por ejemplo 2024-11-05." );
            }
        }
    }

 
    private int pedirCantidad( String mensaje )
    {
        int cantidad = pedirEnteroAlUsuario( mensaje );
        while( cantidad <= 0 )
        {
            System.out.println( "La cantidad debe ser mayor que cero." );
            cantidad = pedirEnteroAlUsuario( mensaje );
        }
        return cantidad;
    }


    private boolean confirmarSobrescritura( String archivo )
    {
        if( new File( archivo ).exists( ) )
            return pedirConfirmacionAlUsuario( "El archivo " + archivo + " ya existe. ¿Desea sobrescribirlo?" );
        return true;
    }

    private void crearCarpetaPadre( String archivo )
    {
        File padre = new File( archivo ).getAbsoluteFile( ).getParentFile( );
        if( padre != null )
            padre.mkdirs( );
    }



    private String describirAvion( Avion avion )
    {
        return avion.getNombre( ) + " (capacidad: " + avion.getCapacidad( ) + " pasajeros)";
    }

    private String describirRuta( Ruta ruta )
    {
        Aeropuerto origen = ruta.getOrigen( );
        Aeropuerto destino = ruta.getDestino( );
        return ruta.getCodigoRuta( ) + ": " + origen.getCodigo( ) + " (" + origen.getNombreCiudad( ) + ") -> " + destino.getCodigo( ) + " (" + destino.getNombreCiudad( ) + "), sale "
                + formatearHora( ruta.getHoraSalida( ) ) + " y llega " + formatearHora( ruta.getHoraLlegada( ) );
    }

    private String describirVuelo( Vuelo vuelo )
    {
        Ruta ruta = vuelo.getRuta( );
        return vuelo.getFecha( ) + " | ruta " + ruta.getCodigoRuta( ) + " " + ruta.getOrigen( ).getCodigo( ) + " -> " + ruta.getDestino( ).getCodigo( ) + " | avión "
                + vuelo.getAvion( ).getNombre( ) + " | tiquetes " + vuelo.getTiquetes( ).size( ) + "/" + vuelo.getAvion( ).getCapacidad( );
    }

    private String formatearHora( String hora )
    {
        return String.format( "%02d:%02d", Ruta.getHoras( hora ), Ruta.getMinutos( hora ) );
    }

    private String formatearDuracion( int minutos )
    {
        return ( minutos / 60 ) + " h " + ( minutos % 60 ) + " min";
    }

    private String formatearDinero( long valor )
    {
        return String.format( LOCALE_CO, "$%,d", valor );
    }

    private int cuposDisponibles( Vuelo vuelo )
    {
        return vuelo.getAvion( ).getCapacidad( ) - vuelo.getTiquetes( ).size( );
    }

    private int contarTiquetesUsados( Vuelo vuelo )
    {
        int usados = 0;
        for( Tiquete tiquete : vuelo.getTiquetes( ) )
        {
            if( tiquete.esUsado( ) )
                usados++;
        }
        return usados;
    }

    
    public static void main( String[] args )
    {
        ConsolaAplicacion consola = new ConsolaAplicacion( );
        consola.correrAplicacion( );
    }
}
