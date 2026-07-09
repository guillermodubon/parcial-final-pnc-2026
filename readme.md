# Guillermo Alejandro Hernandez Dubon 00106423

## Indicaciones

Recientemente, se utilizó AI para crear un sistema de gestion de una biblioteca, el cual ha generado varios errores, su trabajo es arreglarlo. Dado el siguiente caso de uso, explique y/o resuelva cada problema según se le pida.

---

## Consideraciones

La libreria crea automaticamente un correo con los nombres de la persona

---

## Problemas

### 1. Filtro por autor y género (10%)

QA ha reportado que el endpoint para obtener los libros puede filtrar por **autor** y por **género**, o por cualquiera de los dos de manera individual.

Actualmente:

- Filtrar únicamente por autor funciona correctamente.
- Filtrar únicamente por género funciona correctamente.
- Filtrar por **autor y género al mismo tiempo** provoca que el servidor falle.

**Instrucción:** Explique la causa del problema y resuélvalo.

El filtro fallaba cuando se enviaban autor y género al mismo tiempo porque el repositorio recibía el género como String, pero en la entidad Book el campo genre es de tipo enum Genre, otra cosa es que en el servicio se estaban enviando los parámetros en orden incorrecto.
Para solucionar esto cambie el metodo del repositorio para recibir Genre en lugar de String tambien converti el género recibido desde la URL usando trim().toUpperCase() antes de aplicar Genre.valueOf(), y tambien el orden de los parametros para consultar por author y genre correctamente.

---

### 2. Error al volver a prestar un libro (10%)

Un usuario reportó que al pedir prestado el libro **The Selfish Gene**, devolverlo e intentar pedirlo prestado nuevamente, el servidor falla.

**Instrucción:** Explique la causa del problema y resuélvalo.
El libro The Selfish Gene tiene un solo ejemplar disponible cuando se prest,availableCount bajaba a 0 y el libro quedaba como no disponible. El problema era que al devolverlo no se restauraba correctamente el estado del libro para permitir prestarlo otra vez.

cambie la lógica de prestamo y devolución. Al prestar, se valida disponibilidad, se resta availableCount y se actualiza available. Al devolver, se suma availableCount, se marca available como true y se registra el movimiento de devolución. Con esto funciona el flujo prestar, devolver y volver a prestar.

---

### 3. Cantidad de libros por género (10%)

Existe un endpoint que devuelve la cantidad de libros disponibles por género. Sin embargo, actualmente dicho endpoint falla.

**Instrucción:** Explique la causa del problema y resuélvalo.

El endpoint de cantidad de libros disponibles por genero fallaba porque existía un libro con genre null. Al intentar ejecutar book.getGenre().name(), se producía un NullPointerException. Además, el conteo no consideraba correctamente solo los libros disponibles.

cambie la lógica para ignorar libros con genero nulo y contar únicamente libros disponibles, practicamente la solución agrupa por género y suma availableCount para obtener la cantidad real de ejemplares disponibles por cada género.

---

### 4. Error al consultar un libro por ID (10%)

Un miembro del equipo de frontend reporta que la siguiente llamada falla:

```http
GET /books?id=ed16ed1e-7017-4697-a08a-d28c09a74acf
```

**Instrucción:** Explique la causa del problema.

Problema detectado:
Frontend llamaba al endpoint usando GET /books?id=..., pero el backend esperaba el ID como parte de la ruta: GET /books/{id}. Por eso la petición entraba al endpoint general de listado y no al método de búsqueda por ID.

La solucion podria ser modificar el endpoint GET /books para aceptar un parámetro opcional id. Si id viene en la petición, se consulta el libro por ID; si no viene, se mantiene el comportamiento normal de listar o filtrar libros. Se conserva también la ruta GET /books/{id}.


---

### 5. Error al crear un libro (10%)

QA ha reportado que el siguiente payload enviado al endpoint `POST /books` provoca un error:

```json
{
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "genre": "classic",
  "isbn": "978-0132350884",
  "available": true,
  "availableCount": 5
}
```

**Instrucción:** Explique la causa del problema.

El payload enviaba el genero como "classic" en minúsculas, pero el enum Genre define los valores en mayúsculas, por ejemplo CLASSIC. Como Genre.valueOf() distingue entre mayúsculas y minúsculas, la conversión fallaba.

para solucionarlo para convertir el género a enum, se normalizo el texto recibido usando trim().toUpperCase() con esto asi, "classic" se convierte correctamente en CLASSIC y el libro puede crearse sin error.

---

### 6. Devolución de libros no prestados (20%)

QA ha reportado que un usuario es capaz de devolver libros que nunca ha solicitado en préstamo.

**Instrucción:**

- Confirme si este comportamiento es realmente posible.
- Si es posible, explique la causa y resuelva el problema.
- Si no es posible, explique por qué, haciendo referencia al código correspondiente.

Si era posible devolver libros no prestados ademas la devolución solo aumentaba availableCount y registraba un movimiento RETURN, pero no verificaba si el lector realmente tenía un préstamo activo de ese libro.

Para arreglarlo meti una validación antes de devolver. El sistema revisa el historial de movimientos del lector y del libro ordenado por fecha descendente. Si el último movimiento no es BORROWING, se rechaza la devolución porque no existe un prestamo activo. Así se evita devolver libros que nunca fueron prestados o devolver dos veces el mismo prestamo.


---
