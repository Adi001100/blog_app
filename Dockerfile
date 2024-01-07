FROM adoptopenjdk:11-jre-hotspot
RUN mkdir /opt/app
COPY target/angular-blog-1.0-SNAPSHOT.jar /opt/app/blog.jar
COPY src/main/resources/logback-spring.xml /opt/app/logback-spring.xml
COPY src/main/resources/templates/index.html /opt/app
COPY src/main/resources/templates/paymentSuccess.html /opt/app
COPY src/main/resources/templates/paymenntError.html /opt/app
COPY src/main/resources/templates/paymentCancel.html /opt/app
CMD ["java","-Dlogging.config=opt/app/logback-spring.xml", "-jar", "/opt/app/blog.jar"]