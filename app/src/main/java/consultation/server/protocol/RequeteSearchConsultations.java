package consultation.server.protocol;

import java.io.Serializable;
import org.threeten.bp.LocalDate;

/**
 * Classe Java compatible avec RequeteSearchConsultations du serveur CAP
 */
public class RequeteSearchConsultations implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final Integer doctorId;
    private final Integer patientId;
    private final LocalDate fromDate;
    private final LocalDate toDate;
    
    public RequeteSearchConsultations(Integer doctorId, Integer patientId, LocalDate fromDate, LocalDate toDate) {
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }
    
    public Integer getDoctorId() {
        return doctorId;
    }
    
    public Integer getPatientId() {
        return patientId;
    }
    
    public LocalDate getFromDate() {
        return fromDate;
    }
    
    public LocalDate getToDate() {
        return toDate;
    }
}

