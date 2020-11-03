package engine.general.save;

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
     * description of the result
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
