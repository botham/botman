package models;

public class DBSuccessResult<U> implements DBResult {
    private U result;

    public DBSuccessResult(U result) {
        this.result = result;
    }

    public U getResult() {
        return result;
    }

    @Override
    public boolean isSuccess() {
        return true;
    }
}
