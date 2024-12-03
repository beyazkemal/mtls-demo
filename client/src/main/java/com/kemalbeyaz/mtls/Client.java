package com.kemalbeyaz.mtls;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.*;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.KeyStore;

/**
 * @author Kemal Beyaz
 * @date 3.12.2024
 */
public class Client {

    public static void main(String[] args) throws Exception {
        String host = "127.0.0.1"; // REST servisinin adresi
        int port = 8443; // HTTPS portu
        String pfxPath = "client-keystore.pfx"; // PFX dosyanızın yolu
        String pfxPathNotTrusted = "not-trusted-client-keystore.pfx"; // PFX dosyanızın yolu
        String pfxPassword = "123456"; // PFX şifresi

        // PFX Dosyasını yükleyin
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (InputStream pfxStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(pfxPath)) {
            if (pfxStream == null) {
                throw new FileNotFoundException("PFX dosyası bulunamadı: " + pfxPath);
            }
            keyStore.load(pfxStream, pfxPassword.toCharArray());
        }

        // KeyManager ve TrustManager yapılandırma
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keyStore, pfxPassword.toCharArray());

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        trustManagerFactory.init(keyStore);

        // SSL/TLS yapılandırması
        SslContext sslContext = SslContextBuilder.forClient()
                .keyManager(keyManagerFactory)
                .trustManager(trustManagerFactory)
                .build();

        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(sslContext.newHandler(ch.alloc(), host, port));
                            ch.pipeline().addLast(new HttpClientCodec());
                            ch.pipeline().addLast(new HttpObjectAggregator(8192));
                            ch.pipeline().addLast(new SimpleChannelInboundHandler<FullHttpResponse>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse response) {
                                    System.out.println("Response: " + response.content().toString(io.netty.util.CharsetUtil.UTF_8));
                                }
                            });
                        }
                    });

            // HTTP isteğini gönderin
            Channel channel = bootstrap.connect(host, port).sync().channel();
            HttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/hello");
            request.headers().set(HttpHeaderNames.HOST, host);
            request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);

            channel.writeAndFlush(request).sync();
            channel.closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

}
