# Sistema de Gestión de Inventario

Sistema de gestión de inventario desarrollado en **Java**, que permite administrar productos, controlar existencias y gestionar movimientos de stock mediante diferentes patrones de diseño.

## Características

- Registro y búsqueda de productos.
- Gestión de existencias (entrada y salida de stock).
- Validación de movimientos de inventario.
- Alertas automáticas cuando el stock es bajo.
- Cálculo de precios finales aplicando descuentos.
- Generación de reportes del inventario.

## Patrones de diseño utilizados

| Patrón | Tipo | Aplicación |
|---|---|---|
| Singleton | Creacional | Garantiza una única instancia de `Inventario`. |
| Observer | Comportamiento | Notifica cuando un producto tiene stock bajo. |
| Strategy | Comportamiento | Permite gestionar diferentes movimientos de stock mediante `MovimientoStock`. |
| Facade | Estructural | Simplifica la consulta y visualización del inventario mediante `TiendaFacade`. |

## Tecnologías

- Java
- Programación Orientada a Objetos (POO)
- Patrones de diseño de software

## Ejecución

Compilar:

```bash
javac Main.java
```

Ejecutar:

```bash
java Main
```

## Autor

**Chambeador**

---

Proyecto académico desarrollado para poner en práctica principios de diseño de software y patrones de diseño.
