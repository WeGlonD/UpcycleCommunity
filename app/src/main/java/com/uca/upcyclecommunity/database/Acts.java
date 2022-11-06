package com.uca.upcyclecommunity.database;
//끝나면 할일을 넘겨줌
public interface Acts {
    void ifSuccess(Object task);
    void ifFail(Object task);
}
