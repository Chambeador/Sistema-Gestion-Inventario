import java.util.ArrayList;
import java.util.List;


// 1. Patron de comportamiento observer
interface ObservadorStock {
    String actualizar(String nombreProducto, int stockActual);
}

class AlertaStock implements ObservadorStock {
    @Override
    public String actualizar(String nombreProducto, int stockActual) {
        if (stockActual <= 1) {
            return " Stock bajo: " + nombreProducto + " (" + stockActual + ")";
        }
        return "";
    }
}



class Existencia {
    private int cantidad;
    private ObservadorStock observador = new AlertaStock();
    public Existencia(int cantidadI){
        cantidad = cantidadI;
    }

    public String notificarAlerta(String nombreProducto) {
        return observador.actualizar(nombreProducto, cantidad);
    }

    public boolean ingresar(int cant){
        if(cant <= 0) return false;
        cantidad += cant;
        return true;
    }
    
    public boolean retirar(int cant){
        if(cant <= 0 || cant > cantidad) return false;
        cantidad -= cant;
        return true;
    }
    
    public int getCantidad(){ return cantidad; }
}


abstract class Producto {
    protected String codigo;
    protected String nombre;
    protected double precio;
    protected Existencia existencia;
    
    public Producto(String codigo, String nombre, double precio, int stockI){
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.existencia = new Existencia(stockI);
    }   
    
    public boolean tieneDescuento(){ return false;}
    public double porcentajeDescuento(){return 0.0;}
    public String getCodigo(){return codigo;}
    public String getNombre(){return nombre;}
    public double getPrecio(){return precio;}
    public Existencia getExistencia(){return existencia;}
}

class Celular extends Producto {
    public Celular(String codigo, String nombre, double precio, int stockI){
        super(codigo, nombre, precio, stockI);
    }
    @Override
    public boolean tieneDescuento(){ return true;}
    @Override
    public double porcentajeDescuento(){ return 0.05;}
}

class Laptop extends Producto {
    public Laptop(String codigo, String nombre, double precio, int stockI){
        super(codigo, nombre, precio, stockI);
    }
    @Override
    public boolean tieneDescuento(){ return true;}
    @Override
    public double porcentajeDescuento(){ return 0.10;}
}

class PC extends Producto {
    public PC(String codigo, String nombre, double precio, int stockI){
        super(codigo, nombre, precio, stockI);
    }
}

class CalculadoraPrecio {
    public double calcularPrecioFinal(Producto producto){
        double precioF = producto.getPrecio();
        if(producto.tieneDescuento()){
            precioF = producto.getPrecio()*(1.0-producto.porcentajeDescuento());
        }
        return precioF;
    }
}

interface BuscarProducto {
    Producto buscar(String codigo);
}

interface MovimientoStock {
    boolean aplicar(Existencia existencia, int cantidad);
    String getTipo();
}

class EntradaStock implements MovimientoStock {
    @Override
    public boolean aplicar(Existencia existencia, int cantidad){
        return existencia.ingresar(cantidad);
    }
    @Override
    public String getTipo(){ return "Entrada";}
}

class SalidaStock implements MovimientoStock {
    @Override
    public boolean aplicar(Existencia existencia, int cantidad){
        return existencia.retirar(cantidad);
    }
    @Override
    public String getTipo(){ return "Salida";}
}


// 2.patron creacional singleton
class Inventario implements BuscarProducto {
    private static Inventario instanciaUnica;
    private List<Producto> productos = new ArrayList<>();
    private Inventario() {}
    public static synchronized Inventario getInstancia() {
        if (instanciaUnica == null) {
            instanciaUnica = new Inventario();
        }
        return instanciaUnica;
    }

    public boolean registrar(Producto producto){
        if(buscar(producto.getCodigo()) != null){
            return false;
        }
        productos.add(producto);
        return true;
    }

    @Override
    public Producto buscar(String codigo){
        for(Producto p: productos){
            if(p.getCodigo().equals(codigo)) return p;
        }
        return null;
    }

    public List<Producto> listar(){
        return new ArrayList<>(productos);
    }
}

class ServicioInventario {
    private BuscarProducto catalogo;
    
    public ServicioInventario(BuscarProducto catalogo){
        this.catalogo = catalogo;
    }
    
    public String realizarMovimiento(String codigo, int cantidad, MovimientoStock movimiento){
        Producto producto = catalogo.buscar(codigo);
        if(producto == null) return "Error: No encontrado";
        
        if(!movimiento.aplicar(producto.getExistencia(), cantidad)){
            return "Error: " + movimiento.getTipo() + " rechazada (Stock insuficiente)";
        }
        String alertaObserver = producto.getExistencia().notificarAlerta(producto.getNombre());
        return movimiento.getTipo() + " OK. Stock de " + producto.getNombre() + ": " + producto.getExistencia().getCantidad() + alertaObserver;
    }
}

// 3. Patron estructural fecade
class TiendaFacade {
    private Inventario inventario = Inventario.getInstancia();
    
    public String obtenerInventarioFinal() {
        CalculadoraPrecio calc = new CalculadoraPrecio();
        StringBuilder sb = new StringBuilder("\n----Inventario Fnal----\n");
        for (Producto p : inventario.listar()) {
            sb.append(p.getCodigo()).append(" | ").append(p.getNombre())
              .append(" | Stock: ").append(p.getExistencia().getCantidad())
              .append(" | Bs. ").append(calc.calcularPrecioFinal(p)).append("\n");
        }
        return sb.toString();
    }
}

public class Main {
    public static void main(String [] args) {
        Inventario inventario = Inventario.getInstancia();
        ServicioInventario servicio = new ServicioInventario(inventario);
        CalculadoraPrecio calculadora = new CalculadoraPrecio();
        // Registro de productos
        inventario.registrar(new Celular("P01", "Celular Samsung", 2000.0, 5));
        inventario.registrar(new Laptop("P02", "Laptop Lenovo", 5000.0, 2));
        inventario.registrar(new PC("P03", "PC Gamer Desktop", 6000.0, 3));

        System.out.println(" PRECIOS FINALES ");
        System.out.println(" Celular 5%: " + calculadora.calcularPrecioFinal(inventario.buscar("P01")));
        System.out.println(" Laptop 10% : " + calculadora.calcularPrecioFinal(inventario.buscar("P02")));
        System.out.println(" PC: " + calculadora.calcularPrecioFinal(inventario.buscar("P03")));
        System.out.println("\nMOVIMIENTOS DE STOCK ");
        // Retiramos 1 laptop (tenía 2, queda 1 -> ¡Debe saltar la alerta del Observer!)
        System.out.println(servicio.realizarMovimiento("P02", 1, new SalidaStock()));
        System.out.println();
        System.out.println(servicio.realizarMovimiento("P01", 2, new EntradaStock()));
        System.out.println();
        System.out.println(servicio.realizarMovimiento("P03", 10, new SalidaStock())); // Rechazado por stock insuficiente
        // Mostramos el reporte usando la Fachada Estructural
        TiendaFacade tienda = new TiendaFacade();
        System.out.println(tienda.obtenerInventarioFinal());
    }
}