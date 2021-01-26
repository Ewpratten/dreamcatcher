package ca.retrylife.dreamcatcher;

public class AuthManager {

    // Internal instance reference
    private static AuthManager instance = null;
    
    /**
     * Get the global instance of AuthManager
     *
     * @return AuthManager instance
     */
    public static AuthManager getInstance() {
        if (instance == null) {
            instance = new AuthManager();
        }
        return instance;
    }
    
    // Hidden constructor to force singleton usage
    private AuthManager() {
    }
    
    public String getReverseBeaconNetworkKey() {
        // TODO: This is temp
        return "va3zza";
    }
    
    
}