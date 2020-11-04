package engine.general.save;

/**
 * This class is used as a return value for save, load and delete operations.
 * It always contains a ResultKind to easily compare it and a description with more detailed
 * information about the result: in case of a failure it contains the reason or the exception
 * message and in case of a success it contains a success message ore the information the was
 * loaded
 */
@SuppressWarnings("unused")
public class FileWriteReadResult {

    /**
     * general kind of the result
      */
    public enum ResultKind {
        SUCCESS,CANT_CREATE_FOLDERS,CANT_CREATE_FILE,FAILED_TO_WRITE_DATA,CANT_READ_FILE,FILE_DOES_NOT_EXIST,FAILED_TO_DELETE_FILE
    }

    /**
     * general kind of the result
     */
    private final ResultKind resultKind;

    /**
     * more detailed description of the result
     */
    private final String description;

    FileWriteReadResult(ResultKind resultKind, String description) {
        this.resultKind = resultKind;
        this.description = description;
    }

    public ResultKind getResultKind() {
        return resultKind;
    }

    public String getDescription() {
        return description;
    }
}
