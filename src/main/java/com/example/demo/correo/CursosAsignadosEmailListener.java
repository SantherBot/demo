package com.example.demo.correo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class CursosAsignadosEmailListener {

    private static final Logger log = LoggerFactory.getLogger(CursosAsignadosEmailListener.class);

    private final CorreoService correoService;

    public CursosAsignadosEmailListener(CorreoService correoService) {
        this.correoService = correoService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @CacheEvict(cacheNames = "cursosAsignadosPorEstudiante", key = "#event.estudianteId()", beforeInvocation = true)
    public void onCoursesAssigned(CursosAsignadosEvent event) {
        if (event.cursos().isEmpty()) {
            return;
        }

        try {
            correoService.sendAssignedCourses(
                    event.correo(), event.nombreCompleto(), event.cursos());
        } catch (Exception exception) {
            log.error(
                    "Courses were assigned for student {} but email could not be sent to {}. "
                            + "Configure spring.mail.host when email delivery is required.",
                    event.estudianteId(), event.correo(), exception);
        }
    }
}
