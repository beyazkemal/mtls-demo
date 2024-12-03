# mTLS ile Spring Boot Server ve Netty Client

Bu proje, çift yönlü TLS (mutual TLS veya mTLS) kullanarak güvenli bir iletişim sunar.  
**Server** Spring Boot kullanılarak geliştirilmiş olup yalnızca mTLS ile gelen istekleri kabul eder.  
**Client** ise Netty ile geliştirilmiş olup Spring Boot sunucusuna bağlanarak mTLS aracılığıyla istek gönderir.

## Proje Yapısı

- **Server**: Spring Boot tabanlıdır ve yalnızca kimlik doğrulaması yapılmış istemcilerden gelen HTTPS isteklerini kabul eder.
- **Client**: Netty tabanlı bir istemci uygulamasıdır ve sunucuya mTLS bağlantısı kurarak veri alışverişi yapar.

## Gereksinimler

- **Java 11+** (Client için)
- **Java 17+** (Server için)
- Maven
- mTLS için gerekli olan aşağıdaki dosyalar:
    - **CA sertifikası** (Certificate Authority)
    - **Server sertifikası** ve **private key**
    - **Client sertifikası** ve **private key**

## Debug
Her iki uygulamayı da debug modda çalıştırarak SSL katmanında neler olduğunu kontrol edebilirsiniz:

```
-Djavax.net.debug=ssl,handshake
```

Bu VM options, SSL handshake işlemi sırasında detaylı bilgileri verir. Sertifika doğrulama hatalarını daha kolay bulabilirsiniz.
