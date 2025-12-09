package consultation.server.protocol;

import java.io.Serializable;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;

/**
 * Classe Java compatible avec RequeteAddConsultation du serveur CAP
 */
public class RequeteAddConsultation implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final int doctorId;
    private final LocalDate date;
    private final LocalTime time;
    private final int count;
    
    public RequeteAddConsultation(int doctorId, LocalDate date, LocalTime time, int count) {
        this.doctorId = doctorId;
        this.date = date;
        this.time = time;
        this.count = count;
    }
    
    public int getDoctorId() {
        return doctorId;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public LocalTime getTime() {
        return time;
    }
    
    public int getCount() {
        return count;
    }
}

