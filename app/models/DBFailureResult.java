package models;

public class DBFailureResult implements DBResult {

    private int reason;

    public DBFailureResult(int reason) {
        this.reason = reason;
    }

    public int getReason() {
        return reason;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }
}
