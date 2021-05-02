# This is our Java base image which has Java installed on a Redhat image.
FROM 792711658142.dkr.ecr.ap-southeast-2.amazonaws.com/systems/corretto-openjdk11:latest as builder

# These fields are required to inject Nexus credentials into the build stage
# of the docker image.
ARG SNYK_TOKEN
ARG BUILD_BRANCH

# This creates the /app directory and makes it the working directory for
# the remainder of the build process.
WORKDIR /app

#RUN rpm --rebuilddb && yum clean all && yum update -y && yum install -y git && yum clean all
RUN yum install -y git && yum clean all

# This copies all of the files from the local repository into the Docker image
COPY . .

# Runs the gradle build process for creating the WAR file.
RUN chmod +x gradlew && GRADLE_USER_HOME=.cache ./gradlew --parallel --max-workers=2 clean build

# This is our Tomcat base image which has Java and Tomcat installed on a Redhat
# Image.
# By having multiple FROM statements we're using multi stage Docker builds,
# Using this strategy it allows us to keep our images small by not having all the build
# dependencies in the final image. More information: https://docs.docker.com/develop/develop-images/multistage-build/
FROM 792711658142.dkr.ecr.ap-southeast-2.amazonaws.com/systems/tomcat-corretto-openjdk11:latest

# Specify the API Name
ARG API_NAME=httpclient-1.0.0-SNAPSHOT

# Switch to the tomcat user
USER tomcat:tomcat

# Copying files over from the first stage of the docker build.
COPY --from=builder --chown=tomcat:tomcat /app/bin/start bin/start
COPY --from=builder --chown=tomcat:tomcat /app/build/libs/${API_NAME}*.war /usr/local/tomcat/webapps/ROOT.war
COPY --from=builder /app/application_config.yaml /usr/local/config/application_config.yaml

# Run the bin/start script as the container command.
CMD ["/bin/bash", "bin/start"]
