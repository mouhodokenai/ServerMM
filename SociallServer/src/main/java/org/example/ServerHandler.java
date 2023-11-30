package org.example;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;


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

    //private static String YOUID = "[you]";

    // Создание словаря имеющего названию строки ссесию
    // Можно без словаря
    //static final Map<Long, IStreamSession> sessions = new HashMap<Long, IStreamSession>();

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

        // создание списка
        ArrayList<String> attributes = request.getListAttributes();
        // роверка на пустоту
        if (attributes != null && !attributes.isEmpty()) {
            // Вывод атрибутов
            for (String strok : attributes) {
                System.out.println(strok);
            }
        }

        // Обработчики запросов (их лучше вынуть в отдельный метод?)
        if (request.getRequest().equals("Сравнение")) {
            comparison(request);

        }
    }

    // событие (добаление, удаление клиента)
    @SuppressWarnings("incomplete-switch")
    @Override
    public void event(SessionEvent event) {
        switch (event) {
            case OPENED -> {
                // добавление в словарь id ссесии и саму ссесию
                //sessions.put(getSession().getId(), getSession());
                // Вывод сообщения о создании ссесии
                System.out.println(getSession().getId() + "{connected}" + getSession());
            }
            case CLOSED -> {
                System.out.println(getSession().getId() + "{disconnected}");
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

}