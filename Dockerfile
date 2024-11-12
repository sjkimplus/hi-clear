# JDK 17을 사용하는 경량화된 베이스 이미지
FROM openjdk:17-jdk-slim

# 애플리케이션 JAR 파일을 컨테이너에 복사
COPY build/libs/hi-clear-0.0.1-SNAPSHOT.jar /app/hi-clear.jar

# 애플리케이션이 실행될 위치 설정
WORKDIR /app

# 애플리케이션 실행 명령
CMD ["java", "-jar", "hi-clear.jar"]