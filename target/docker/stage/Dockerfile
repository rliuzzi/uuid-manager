FROM java:latest
MAINTAINER Romina Liuzzi
WORKDIR /opt/docker
ADD opt /opt
RUN ["chown", "-R", "daemon:daemon", "."]
USER daemon
ENTRYPOINT ["bin/uuid-manager"]
CMD []
