package org.example;

import com.google.gson.Gson;
import org.snf4j.core.EndingAction;
import org.snf4j.core.handler.AbstractStreamHandler;
import org.snf4j.core.handler.SessionEvent;
import org.snf4j.core.session.DefaultSessionConfig;
import org.snf4j.core.session.ISessionConfig;
import org.snf4j.core.session.IStreamSession;

public class

ChatClientHandler extends AbstractStreamHandler {

    Gson gson = new Gson();
    //IStreamSession session;

    //Срабатывает при получении данных из потока
    @Override
    public void read(Object msg) {
        // получение ответа
        String jsonStringRequest = new String((byte[]) msg);

        // Сериализация в класс Request
        Answer answer = gson.fromJson(jsonStringRequest, Answer.class);

        //вывод ответа
        System.err.println("Список");
        for (String str: answer.getListAttributes()){
            System.err.println(str);
        }
    }

    // событие (закрытие сервера)
    @Override
    public void event(SessionEvent event) {
        if (event == SessionEvent.CLOSED) {

            // Notify if the closing initiated by the server
            if (!getSession().getAttributes().containsKey(Main.BYE_TYPED)) {
                System.out.println("Соединие потеряно Сервер отключился");
            }
        }
    }

    // при завершении подключения
    @Override
    public ISessionConfig getConfig() {

        // Gently stop the selector loop if session associated
        // with this handler ends
        // Остановка цикла выбора, если сеанс, связанный
        // с этим обработчиком, завершается
        return new DefaultSessionConfig()
                .setEndingAction(EndingAction.STOP);
    }

    public void send(Request request) {
        IStreamSession session = getSession();
        Gson gson = new Gson();
        String jsonString = gson.toJson(request);
        session.write(jsonString);

    }
}