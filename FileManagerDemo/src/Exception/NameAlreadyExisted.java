package Exception;

public class NameAlreadyExisted extends Exception{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public String getMessage() {
        return "Name Already Existed";
    }
    
}