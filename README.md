# LINKs

https://github.com/Sistema-Gestion-Servicios-Wakanda-J-M-N/Backend_Wakanda_Salud.git

# Participantes del proyecto

- Jaime López Díaz
- Marcos García Benito
- Nicolas Jimenez
- Juan Manuel

# Backend Wakanda Salud

Este proyecto es un microservicio de gestión de citas médicas y historial médico, diseñado para gestionar centros de salud, médicos, pacientes, citas y notificaciones. Ofrece funcionalidad a través de una API REST para interactuar con la API principal y de esta manera enviar datos sobre la lógica de negocio, y recibir el token de autentificación con los datos del usuario y sus roles.

## Lógica de Negocio

### 1. **Centros de Salud**

Los centros de salud son entidades que tienen médicos y pacientes asignados. La aplicación permite crear, gestionar y almacenar centros de salud. Estos centros están relacionados con los sistemas de citas, donde los pacientes pueden agendar citas con los médicos.

### 2. **Médicos y Pacientes**

El sistema permite gestionar médicos y pacientes. Los médicos tienen una especialidad y están asignados a un centro de salud. Los pacientes están registrados en el sistema y cada uno tiene un historial médico, que puede contener entradas de consultas anteriores, prescripciones, etc.

- **Médico**: Puede tener especialidades como "Cardiología", "Pediatría", etc.
- **Paciente**: Cada paciente tiene un historial médico asociado.

### 3. **Citas Médicas**

Las citas médicas pueden ser de dos tipos: **normales** y **urgentes**.

- **Cita Normal**: Una cita regular con un médico.
- **Cita Urgente**: Una cita médica que tiene un nivel de prioridad más alto y debe ser atendida con urgencia.

Las citas son asignadas a un sistema de citas perteneciente a un centro de salud y están asociadas con un médico y un paciente.

### 4. **Historial Médico**

El historial médico de un paciente contiene entradas que describen las consultas anteriores con los médicos. Cada entrada puede tener una descripción, fecha y prescripciones asociadas.

### 5. **Notificaciones**

El sistema envía notificaciones a los usuarios (médicos y pacientes) sobre eventos como citas creadas, eliminadas, o actualizadas.

## Endpoints del API

A continuación se detallan los **endpoints** de la API REST disponibles para interactuar con el sistema:

### **Centros de Salud**

- **POST /api/centros**: Crear un nuevo centro de salud.

  - **Body**: `{"nombre": "Centro de Salud Birnin Zana", "direccion": "Avenida del Rey T'Challa", "telefono": "123456789"}`
  - **Response**: `{"id": 1, "nombre": "Centro de Salud Birnin Zana", "direccion": "Avenida del Rey T'Challa", "telefono": "123456789"}`
- **GET /api/centros**: Obtener todos los centros de salud.

  - **Response**: `[{"id": 1, "nombre": "Centro de Salud Birnin Zana", "direccion": "Avenida del Rey T'Challa", "telefono": "123456789"}]`

### **Médicos**

- **POST /api/medicos**: Crear un nuevo médico.

  - **Body**: `{"nombre": "Shuri", "apellidos": "Wakanda", "especialidad": "Cardiología", "numeroLicencia": "LIC-123456", "email": "shuri@wakanda.com"}`
  - **Response**: `{"id": 1, "nombre": "Shuri", "apellidos": "Wakanda", "especialidad": "Cardiología", "numeroLicencia": "LIC-123456", "email": "shuri@wakanda.com"}`
- **GET /api/medicos/{id}**: Obtener información de un médico por ID.

  - **Response**: `{"id": 1, "nombre": "Shuri", "apellidos": "Wakanda", "especialidad": "Cardiología", "numeroLicencia": "LIC-123456", "email": "shuri@wakanda.com"}`

### **Pacientes**

- **POST /api/pacientes**: Crear un nuevo paciente.

  - **Body**: `{"nombre": "Nakia", "apellidos": "Wakanda", "email": "nakia@wakanda.com"}`
  - **Response**: `{"id": 1, "nombre": "Nakia", "apellidos": "Wakanda", "email": "nakia@wakanda.com"}`
- **GET /api/pacientes/{id}**: Obtener información de un paciente por ID.

  - **Response**: `{"id": 1, "nombre": "Nakia", "apellidos": "Wakanda", "email": "nakia@wakanda.com", "historialMedicoId": 1}`

### **Citas Médicas**

- **POST /api/citas/normal**: Crear una cita médica normal.

  - **Body**: `{"fechaHora": "2024-11-28T10:00:00", "medicoId": 1, "pacienteId": 1, "motivo": "Consulta general"}`
  - **Response**: `{"id": 1, "fechaHora": "2024-11-28T10:00:00", "medicoId": 1, "pacienteId": 1, "motivo": "Consulta general"}`
- **POST /api/citas/urgente**: Crear una cita médica urgente.

  - **Body**: `{"fechaHora": "2024-11-28T10:00:00", "medicoId": 1, "pacienteId": 1, "motivoUrgencia": "Dolor agudo", "nivelPrioridad": "ALTA"}`
  - **Response**: `{"id": 1, "fechaHora": "2024-11-28T10:00:00", "medicoId": 1, "pacienteId": 1, "motivoUrgencia": "Dolor agudo", "nivelPrioridad": "ALTA"}`
- **GET /api/citas/normal/{citaId}**: Obtener información de una cita normal por ID.

  - **Response**: `{"id": 1, "fechaHora": "2024-11-28T10:00:00", "medicoId": 1, "pacienteId": 1, "motivo": "Consulta general"}`
- **GET /api/citas/urgente/{citaId}**: Obtener información de una cita urgente por ID.

  - **Response**: `{"id": 1, "fechaHora": "2024-11-28T10:00:00", "medicoId": 1, "pacienteId": 1, "motivoUrgencia": "Dolor agudo", "nivelPrioridad": "ALTA"}`
- **DELETE /api/citas/{sistemaId}/{citaId}**: Eliminar una cita de un sistema de citas.

  - **Response**: `"Entrada eliminada del historial médico."`

### **Historial Médico**

- **POST /api/historial/{medicoId}/{pacienteId}**: Crear una entrada en el historial médico de un paciente.

  - **Body**: `{"descripcion": "Revisión general", "fecha": "2024-11-28T10:00:00"}`
  - **Response**: `{"id": 1, "descripcion": "Revisión general", "fecha": "2024-11-28T10:00:00", "medicoId": 1, "pacienteId": 1}`
- **DELETE /api/historial/{entradaId}**: Eliminar una entrada del historial médico.

  - **Response**: `"Entrada eliminada del historial médico."`
