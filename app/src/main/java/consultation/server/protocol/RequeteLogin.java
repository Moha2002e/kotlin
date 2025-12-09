package consultation.server.protocol;

import java.io.Serializable;

/**
 * Classe Java compatible avec RequeteLogin du serveur CAP
 * Utilisée pour garantir la compatibilité exacte de sérialisation
 */
public class RequeteLogin implements Serializable {
    private static final long serialVersionUID = -5968792954984562726L;
    
    private final String login;
    private final String password;
    
    public RequeteLogin(String login, String password) {
        this.login = login;
        this.password = password;
    }
    
    // Getters pour accès depuis Kotlin si nécessaire
    public String getLogin() {
        return login;
    }
    
    public String getPassword() {
        return password;
    }
}

