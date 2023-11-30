package org.example;

import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.snf4j.core.SelectorLoop;
import org.snf4j.core.factory.AbstractSessionFactory;
import org.snf4j.core.handler.IStreamHandler;

public class Main {
    static final String PREFIX = "localhost";

    //не пон разницы спросить у артема
    static final int PORT = Integer.getInteger(PREFIX+"Port", 8002);

    public static void main(String[] args) throws Exception {
        // создание потока
        SelectorLoop loop = new SelectorLoop();

        try {
            // запуск сервера - потока (бесконечного)
            loop.start();

            // Initialize the listener
            // создание слушателя
            ServerSocketChannel channel = ServerSocketChannel.open();
            // разрешение принимать сразу несколько подключений (то бишь можно подключиться к нескольким клиентам)
            channel.configureBlocking(false);
            // привязка порта к сокету сервера
            channel.socket().bind(new InetSocketAddress(PORT));

            // Register the listener
            // установка слушателя
            loop.register(channel, new AbstractSessionFactory() {

                // метод срабатывающий при подключении клиента
                @Override
                protected IStreamHandler createHandler(SocketChannel channel) {
                    // создание класса Сервера
                    return new ServerHandler();
                }
            }).sync();

            // Wait till the loop ends
            // ожидание конца потока
            loop.
                    join();
        }
        finally {

            // Gently stop the loop
            // Остановка потока при появлении ошибки
            loop.stop();
            System.out.println("сервер закрыт");
        }
    }
}