package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.rmi.ConnectException;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.gson.Gson;
import org.snf4j.core.SelectorLoop;
import org.snf4j.core.session.IStreamSession;

public class Main {
    static final String PREFIX = "org.snf4j.";
    static final String HOST = System.getProperty(PREFIX+"Host", "127.0.0.1");
    static final int PORT = Integer.getInteger(PREFIX+"Port", 8002);
    static final Integer BYE_TYPED = 0;

    public String answer;


    //ChatClientHandler clien;

    //IStreamSession session;


    public static void send(Request request, IStreamSession session){
        Gson gson = new Gson();
        String jsonString = gson.toJson(request);
        //session.write(jsonString);
        session.write(jsonString.getBytes());
    }

    public static void main(String[] args) throws Exception {
        SelectorLoop loop = new SelectorLoop();

        try {
            // запуск клиента - потока
            loop.start();

            // Initialize the connection
            // создание слушателя
            SocketChannel channel = SocketChannel.open();
            // Запрет на принятие сразу несколько подключений (тобиш можно подключиться тока к 1 серверу)
            channel.configureBlocking(false);
            //  подключение к серверу по адрессу и порту
            channel.connect(new InetSocketAddress(InetAddress.getByName(HOST), PORT));

            // Register the channel
            // Регистрация канала (сосздание ссесии) и создание объекта каласса
            ChatClientHandler clien = new ChatClientHandler();
            IStreamSession session = (IStreamSession) loop.register(channel, clien).sync().getSession();

            // Confirm that the connection was successful
            // подтверждение успешно установленного потключения
            session.getReadyFuture().sync();

            ArrayList<String> org = new ArrayList<>(Arrays.asList("1234", "ArtikDemonik"));
            ArrayList<String> org_2 = new ArrayList<>(Arrays.asList("12345", "ArtikDemonik"));
            ArrayList<String> org_3 = new ArrayList<>(Arrays.asList("1234", "ArtikDemonik", "хер"));
            Request request = new Request("Сравнение", org);
            Request request_2 = new Request("Сравнение",org_2);
            Request request_3 = new Request("Сравнение",org_3);


            // Чет нихера не понял
            send(request, session);

            // без ожидания не успевает(
            loop.join(5000);
            send(request_2, session);
            //loop.join(2000);
            /*send(request_3, session);

            send(request_2, session);
            send(request_3, session);*/


            //session.getAttributes().put(BYE_TYPED, BYE_TYPED);  //?????7
            loop.join();  //??????????


            //ОСТАВЛЮ НА СЛУЧАЙ ПРОВЕРКИ

            //читает строку с консоли
            /*BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String line;
            // если не пустое
            while ((line = in.readLine()) != null) {
                // если соединение открыто
                if (session.isOpen()) {
                    // отправка на сервер строки line по байтам
                    session.write((line).getBytes());
                }
                // если введено bue
                if ("bye".equalsIgnoreCase(line)) {
                    //хз пока
                    session.getAttributes().put(BYE_TYPED, BYE_TYPED);
                    //конец цикла
                    break;
                }
            }
            loop.join();*/
        }
        // Найти Exception сработающий на не подключение сервреа
        catch (Exception ex){
            System.out.println("Сервер не подключен");
        }
        finally {

            // Gently stop the loop
            // Остановка потока
            loop.stop();
        }
    }
}