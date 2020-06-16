package Exception;

public class DoesntExist extends Exception{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public String getMessage() {
        return "Doesn't Exist";
    }
    
}