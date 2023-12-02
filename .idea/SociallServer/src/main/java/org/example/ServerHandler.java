package org.example;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import org.snf4j.core.handler.AbstractStreamHandler;
import org.snf4j.core.handler.SessionEvent;
import org.snf4j.core.session.IStreamSession;

public class ServerHandler extends AbstractStreamHandler {

    private static final Integer USERID = 0;

    // Объект gson
    Gson gson = new Gson();

    Database database = new Database();
    Answer AnswerSer;

    // Создание словаря имеющего названию строки ссесию
    // Можно без словаря
    static final Map<Long, IStreamSession> sessions = new HashMap<Long, IStreamSession>();

    //Срабатывает при получении данных из потока
    @Override
    public void read(Object msg) {
        //получние запроса
        String jsonStringRequest = new String((byte[]) msg);
        System.out.println(jsonStringRequest);

        // Сериализация в класс Request
        Request request = gson.fromJson(jsonStringRequest, Request.class);

        //вывод запроса
        System.out.println("Request: " + request.getRequest());
        send("Request: " + request.getRequest());

        // создание списка
        ArrayList<String> attributes = request.getListAttributes();
        // проверка на пустоту
        if (attributes != null && !attributes.isEmpty()) {
            // Вывод атрибутов
            for (String strok : attributes) {
                System.out.println(strok);
            }
        }

        // Обработчики запросов (их лучше вынуть в отдельный метод?)
        if (request.getRequest().equals("Сравнение")) {
            comparison(request);
            send("chto");

        }
    }

    // событие (добавление, удаление клиента)
    @SuppressWarnings("incomplete-switch")
    @Override
    public void event(SessionEvent event) {
        switch (event) {
            case OPENED -> {
                sessions.put(getSession().getId(), getSession());
                getSession().getAttributes().put(USERID, "["+getSession().getRemoteAddress()+"]");
                send("{connected}");
                break;
            }
            case CLOSED -> {
                send("{disconnected}");
                sessions.remove(getSession().getId());
                break;
            }
        }
    }



    public void comparison(Request request) {
        //Если всё совпало
        // Тут должно быть обращение к бд

        String answerbd = database.CheckUser(request.getListAttributes().get(1), request.getListAttributes().get(0));

        AnswerSer = new Answer(new ArrayList<>(Arrays.asList(answerbd)));
        /*if (request.getListAttributes().get(0).equals("1234") && request.getListAttributes().get(1).equals("ArtikDemonik")) {
            answer = new Answer(new ArrayList<>(Arrays.asList("GOOD")));
        } else {
            answer = new Answer(new ArrayList<>(Arrays.asList("BADLY")));
        }*/

        answer(AnswerSer);



        /*Answer answer;
        if (request.getListAttributes().get(0).equals("1234") && request.getListAttributes().get(1).equals("ArtikDemonik")) {
            answer = new Answer(new ArrayList<>(Arrays.asList("GOOD")));
        } else {
            answer = new Answer(new ArrayList<>(Arrays.asList("BADLY")));
        }
        answer(answer);*/
    }

    // Отправка ответа
    private void answer(Answer answer) {
        // создание json
        String jsonStringAnswer = gson.toJson(answer);
        // получение id сессии
        getSession().write(("%s".formatted(jsonStringAnswer)).getBytes());
    }
    private void send(String message) {
        long youId = getSession().getId();
        String userId = (String) getSession().getAttributes().get(USERID);

        for (IStreamSession session : sessions.values()) {
            String YOUID = "[you]";
            session.write(((session.getId() == youId ? YOUID : userId) + ' ' + message).getBytes());
        }
    }

}