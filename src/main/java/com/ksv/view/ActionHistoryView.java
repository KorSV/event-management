package com.ksv.view;

import com.ksv.exceptions.NoActionToUndoException;
import com.ksv.service.ActionHistoryService;

public class ActionHistoryView {
    private final ActionHistoryService actionHistoryService;

    public ActionHistoryView(ActionHistoryService actionHistoryService) {
        this.actionHistoryService = actionHistoryService;
    }

    public void undoActionHandler() {
        try {
            this.actionHistoryService.undoLastAction();
            System.out.println("Действие отменено");
        }catch (NoActionToUndoException e){
            System.out.println("Ошибка: " + e.getMessage());
        }

    }
}
