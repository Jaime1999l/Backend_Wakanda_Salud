package org.example.backend_wakanda_salud.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.AfterReturning;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditAspect {

    private static final Logger logger = LoggerFactory.getLogger(AuditAspect.class);

    @AfterReturning("execution(* org.example.backend_wakanda_salud.service.usuario.UsuarioService.update(..))")
    public void auditUserUpdate(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Long userId = (Long) args[0];
        logger.info("El usuario con ID {} ha sido actualizado.", userId);
    }

    @AfterReturning("execution(* org.example.backend_wakanda_salud.service.citas.CitaService.crearCita*(..))")
    public void auditCitaCreation(JoinPoint joinPoint) {
        logger.info("Se ha creado una nueva cita: {}", joinPoint.getArgs());
    }
}

