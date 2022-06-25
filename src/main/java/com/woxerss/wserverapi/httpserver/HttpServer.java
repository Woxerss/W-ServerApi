package com.woxerss.wserverapi.httpserver;

import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

// JLHTTP-2.6
import net.freeutils.httpserver.HTTPServer;

public class HttpServer extends HTTPServer
{
    public void startServer(int port)
            throws Exception {
        HTTPServer server = new HTTPServer(port);

        // Обработчик методов
        VirtualHost vHost = server.getVirtualHost(null);
        vHost.addContexts(new HttpHandler());

        // Https
        char[] password = "Ur?B!Sn".toCharArray();
        KeyStore ks = KeyStore.getInstance("jks");
        ks.load(HttpServer.class.getResourceAsStream("/my.jks"), password);
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, password);
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), null, null);
        server.setServerSocketFactory(sslContext.getServerSocketFactory());

        // Запускаем
        server.start();
    }
}