package consultation.server.protocol;

import java.io.Serializable;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;

/**
 * Classe Java compatible avec RequeteUpdateConsultation du serveur CAP
 */
public class RequeteUpdateConsultation implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final int consultationId;
    private final LocalDate newDate;
    private final LocalTime newTime;
    private final Integer patientId;
    private final String reason;
    
    public RequeteUpdateConsultation(int consultationId, LocalDate newDate, LocalTime newTime, Integer patientId, String reason) {
        this.consultationId = consultationId;
        this.newDate = newDate;
        this.newTime = newTime;
        this.patientId = patientId;
        this.reason = reason;
    }
    
    public int getConsultationId() {
        return consultationId;
    }
    
    public LocalDate getNewDate() {
        return newDate;
    }
    
    public LocalTime getNewTime() {
        return newTime;
    }
    
    public Integer getPatientId() {
        return patientId;
    }
    
    public String getReason() {
        return reason;
    }
}

