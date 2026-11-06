package com.ksv.service;


import com.ksv.exceptions.NoActionToUndoException;
import com.ksv.model.Action;

import java.util.ArrayDeque;
import java.util.Deque;

public class ActionHistoryService {
    private final Deque<Action> history = new ArrayDeque<>();

    void push(Action action){
        history.push(action);
    }

    public void undoLastAction(){
        Action action = history.pollFirst();
        if (action == null){
            throw new NoActionToUndoException("В истории нет действия для отмены");
        }
        action.undo();
    }


}
