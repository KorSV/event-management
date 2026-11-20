package com.ksv.view;

import com.ksv.exceptions.NoActionToUndoException;
import com.ksv.service.ActionHistoryService;

public class ActionHistoryView {
    private final ActionHistoryService actionHistoryService;
    private final InputReader inputReader;

    public ActionHistoryView(ActionHistoryService actionHistoryService, InputReader inputReader) {
        this.actionHistoryService = actionHistoryService;
        this.inputReader = inputReader;
    }

    public void undoActionHandler() {
        try {
            if (actionHistoryService.existsLastAction()){
                String requestCancelMessage = inputReader
                        .getString(actionHistoryService.getDescriptionLastAction() + " (Y/N): ");
                if (requestCancelMessage.toUpperCase().equals("Y")) {
                    this.actionHistoryService.undoLastAction();
                    System.out.println("Мероприятие отменено!");
                } else if (requestCancelMessage.toUpperCase().equals("N")) {
                    return;
                }else{
                    System.out.println("Введите правильное значение либо Y либо N");
                    undoActionHandler();
                }
            }else{
                System.out.println("В истории нет операций для отмены");
            }
        }catch (NoActionToUndoException e){
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}
