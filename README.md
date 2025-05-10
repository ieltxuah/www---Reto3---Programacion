# Reto 3 - Gestión de Biblioteca

## Centro Formativo Somorrostro

### Descripción del Proyecto

Este proyecto, desarrollado por el Grupo 1 (WWW), consiste en la creación de un programa en Java que permite gestionar una base de datos (BBDD) para una biblioteca. El sistema incluye funcionalidades para realizar altas, bajas, modificaciones y consultas de datos relacionados con autores, libros, usuarios y la gestión de préstamos.

### Funcionalidades

El programa incluye las siguientes funcionalidades:

- **Gestión de Autores**:

    - Alta de nuevos autores.
    - Baja de autores existentes.
    - Modificación de datos de autores.
    - Consulta de información de autores.
- **Gestión de Libros**:

    - Alta de nuevos libros.
    - Baja de libros existentes.
    - Modificación de datos de libros.
    - Consulta de información de libros.
- **Gestión de Usuarios**:

    - Alta de nuevos usuarios.
    - Baja de usuarios existentes.
    - Modificación de datos de usuarios.
    - Consulta de información de usuarios.
- **Gestión de Préstamos**:

    - Registro de préstamos de libros a usuarios.
    - Consulta del estado de los préstamos.
    - Devolución de libros.

### Tecnologías Utilizadas

- **Lenguaje de Programación**: Java
- **Base de Datos**: [Especificar el tipo de base de datos utilizada, por ejemplo, MySQL, SQLite, etc.]
- **IDE**: [Especificar el entorno de desarrollo utilizado, por ejemplo, IntelliJ IDEA, Eclipse, Visual Studio Code, etc.]
- **Control de Versiones**: Git

### Instalación

1. Clona el repositorio en tu máquina local:


bash

- `git clone [URL del repositorio]`

- Navega al directorio del proyecto:


bash

- `cd reto3-gestion-biblioteca`

- Crea una carpeta llamada `lib` en el directorio del proyecto:


bash

- `mkdir lib`

- Descárgate el conector JDBC correspondiente a tu base de datos (por ejemplo, MySQL Connector/J) desde [el sitio oficial de MySQL](https://dev.mysql.com/downloads/connector/j/).

- Copia el archivo JAR del conector descargado en la carpeta `lib`.

- Enlaza el conector JDBC al proyecto en Visual Studio Code:

    - Abre Visual Studio Code y carga el proyecto.
    - Asegúrate de tener instalada la extensión "Java Extension Pack" para facilitar el desarrollo en Java.
    - Abre el archivo `settings.json` de tu proyecto (puedes encontrarlo en la carpeta `.vscode` o crear uno si no existe).
    - Añade la siguiente configuración para incluir el JAR del conector en el classpath:


json

- - `{   "java.project.referencedLibraries": [     "lib/*.jar"   ] }`

    - Guarda los cambios.
- Asegúrate de tener las dependencias necesarias instaladas. [Incluir instrucciones específicas si es necesario].

- Compila y ejecuta el programa