FROM java:latest
MAINTAINER Romina Liuzzi
WORKDIR /opt/docker
ADD opt /opt
RUN ["chown", "-R", "daemon:daemon", "."]

# Install vi editor
FROM  confluent/postgres-bw:0.1
RUN ["apt-get", "update"]
RUN ["apt-get", "install", "-y", "vim"]


# Install ruby and packages
#
RUN apt-get install -y ruby-full
RUN ruby --version

# Set ruby to use utf-8
ENV RUBYOPT "-KU -E utf-8:utf-8"

# Install fastlane
RUN gem install fastlane
RUN fastlane --version

RUN gpg --keyserver hkp://keys.gnupg.net --recv-keys 409B6B1796C275462A1703113804BB82D39DC0E3
RUN \curl -L https://get.rvm.io | bash -s stable
RUN /bin/bash -l -c "rvm requirements"
RUN /bin/bash -l -c "rvm install 2.0"
RUN /bin/bash -l -c "rvm install ruby --latest"
RUN /bin/bash -l -c "gem install bundler --no-ri --no-rdoc"
RUN /bin/bash -l -c "gem install fastlane --no-rdoc --no-ri"
RUN /bin/bash -l -c "ruby -v"




USER daemon
ENTRYPOINT ["bin/uuid-manager"]
CMD []
