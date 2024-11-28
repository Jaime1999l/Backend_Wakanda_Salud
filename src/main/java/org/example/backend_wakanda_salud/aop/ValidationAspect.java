package org.example.backend_wakanda_salud.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.example.backend_wakanda_salud.model.usuarios.pacientes.historialMedico.EntradaHistorialDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Aspect
@Component
public class ValidationAspect {

    private static final Logger logger = LoggerFactory.getLogger(ValidationAspect.class);

    /**
     * Validar la creación de un usuario antes de ejecutar el metodo.
     */
    @Before("execution(* org.example.backend_wakanda_salud.service.usuario.UsuarioService.create(..))")
    public void validateUserCreation(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0 || args[0] == null) {
            throw new IllegalArgumentException("El DTO de usuario no puede ser nulo.");
        }

        Object usuarioDTO = args[0];
        logger.info("Validando datos del usuario para el método create: {}", usuarioDTO);

        try {
            Class<?> dtoClass = usuarioDTO.getClass();

            // Validar email
            String email = (String) dtoClass.getMethod("getEmail").invoke(usuarioDTO);
            if (email == null || email.trim().isEmpty() || !email.contains("@")) {
                throw new IllegalArgumentException("El email del usuario no es válido.");
            }

            // Validar roles
            Object roles = dtoClass.getMethod("getRoles").invoke(usuarioDTO);
            if (roles == null || ((List<?>) roles).isEmpty()) {
                throw new IllegalArgumentException("El usuario debe tener al menos un rol asignado.");
            }

        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Error al validar el DTO del usuario.", e);
        }
    }

    /**
     * Validar la creación de una cita normal antes de ejecutar el metodo.
     */
    @Before("execution(* org.example.backend_wakanda_salud.service.citas.CitaService.crearCitaNormal(..))")
    public void validateCitaNormalCreation(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0 || args[0] == null) {
            throw new IllegalArgumentException("El DTO de cita normal no puede ser nulo.");
        }

        Object citaNormalDTO = args[0];
        logger.info("Validando datos de la cita normal: {}", citaNormalDTO);

        // Validaciones específicas del DTO de la cita normal
        try {
            Class<?> dtoClass = citaNormalDTO.getClass();

            // Validar fecha y hora
            Object fechaHora = dtoClass.getMethod("getFechaHora").invoke(citaNormalDTO);
            if (fechaHora == null) {
                throw new IllegalArgumentException("La fecha y hora de la cita no pueden ser nulas.");
            }

            // Validar paciente ID
            Object pacienteId = dtoClass.getMethod("getPacienteId").invoke(citaNormalDTO);
            if (pacienteId == null || !(pacienteId instanceof Long) || (Long) pacienteId <= 0) {
                throw new IllegalArgumentException("El ID del paciente no es válido.");
            }

            // Validar médico ID
            Object medicoId = dtoClass.getMethod("getMedicoId").invoke(citaNormalDTO);
            if (medicoId == null || !(medicoId instanceof Long) || (Long) medicoId <= 0) {
                throw new IllegalArgumentException("El ID del médico no es válido.");
            }

        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Error al validar el DTO de la cita normal.", e);
        }
    }

    /**
     * Validar la eliminación de una cita antes de ejecutar el metodo.
     */
    @Before("execution(* org.example.backend_wakanda_salud.service.citas.CitaService.eliminarCita(..))")
    public void validateCitaDeletion(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0 || args[0] == null) {
            throw new IllegalArgumentException("El ID de la cita no puede ser nulo.");
        }

        Long citaId = (Long) args[0];
        if (citaId <= 0) {
            throw new IllegalArgumentException("El ID de la cita no es válido: debe ser mayor a cero.");
        }

        logger.info("Validación exitosa para la eliminación de la cita con ID: {}", citaId);
    }

    /**
     * Validar la creación de un historial médico antes de ejecutar el metodo.
     */
    @Before("execution(* org.example.backend_wakanda_salud.service.usuario.pacienteHistorial.HistorialMedicoService.crearHistorial(..))")
    public void validateCrearHistorial(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0 || args[0] == null) {
            throw new IllegalArgumentException("El ID del paciente no puede ser nulo.");
        }

        Long pacienteId = (Long) args[0];
        if (pacienteId <= 0) {
            throw new IllegalArgumentException("El ID del paciente no es válido.");
        }

        logger.info("Validación exitosa para la creación del historial médico del paciente con ID: {}", pacienteId);
    }
}