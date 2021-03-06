FROM openjdk:8-jdk
MAINTAINER Cangol  <wxw404@gmail.com>

ENV SDK_HOME /usr/local

RUN apt-get --quiet update --yes
RUN apt-get --quiet install --yes wget tar unzip lib32stdc++6 lib32z1 git --no-install-recommends
RUN apt-get --quiet install --yes libqt5widgets5 usbutils

# Gradle
#ENV GRADLE_VERSION 4.4
#ENV GRADLE_SDK_URL https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip
#RUN curl -sSL "${GRADLE_SDK_URL}" -o gradle-${GRADLE_VERSION}-bin.zip  \
#	&& unzip gradle-${GRADLE_VERSION}-bin.zip -d ${SDK_HOME}  \
#	&& rm -rf gradle-${GRADLE_VERSION}-bin.zip
#ENV GRADLE_HOME ${SDK_HOME}/gradle-${GRADLE_VERSION}
#ENV PATH ${GRADLE_HOME}/bin:$PATH

# android sdk|build-tools|image
ENV ANDROID_TARGET_SDK="android-27" \
    ANDROID_BUILD_TOOLS="27.0.3" \
    ANDROID_SDK_TOOLS="3859397" \
    ANDROID_IMAGES="system-images;android-22;google_apis;armeabi-v7a"   
ENV ANDROID_SDK_URL https://dl.google.com/android/repository/sdk-tools-linux-${ANDROID_SDK_TOOLS}.zip
RUN curl -sSL "${ANDROID_SDK_URL}" -o android-sdk-linux.zip \
    && unzip android-sdk-linux.zip -d android-sdk-linux \
  && rm -rf android-sdk-linux.zip
  
# Set ANDROID_HOME
ENV ANDROID_HOME /android-sdk-linux
ENV ANDROID_ZIPALIGN /android-sdk-linux/build-tools/$ANDROID_BUILD_TOOLS/zipalign
ENV PATH ${ANDROID_HOME}/tools:${ANDROID_HOME}/platform-tools:${ANDROID_ZIPALIGN}:/android-sdk-linux/build-tools/$ANDROID_BUILD_TOOLS:$PATH 

# licenses
RUN mkdir $ANDROID_HOME/licenses
RUN echo 8933bad161af4178b1185d1a37fbf41ea5269c55 > $ANDROID_HOME/licenses/android-sdk-license
RUN echo d56f5187479451eabf01fb78af6dfcb131a6481e >> $ANDROID_HOME/licenses/android-sdk-license
RUN echo 84831b9409646a918e30573bab4c9c91346d8abd > $ANDROID_HOME/licenses/android-sdk-preview-license

# Update and install using sdkmanager 
RUN echo yes | $ANDROID_HOME/tools/bin/sdkmanager --licenses
#RUN echo yes | $ANDROID_HOME/tools/bin/sdkmanager --update
RUN echo yes | $ANDROID_HOME/tools/bin/sdkmanager "tools" "platform-tools" "emulator"
RUN echo yes | $ANDROID_HOME/tools/bin/sdkmanager "build-tools;${ANDROID_BUILD_TOOLS}"
RUN echo yes | $ANDROID_HOME/tools/bin/sdkmanager "platforms;${ANDROID_TARGET_SDK}"
RUN echo yes | $ANDROID_HOME/tools/bin/sdkmanager "extras;android;m2repository" "extras;google;google_play_services" "extras;google;m2repository"
RUN echo yes | $ANDROID_HOME/tools/bin/sdkmanager "extras;m2repository;com;android;support;constraint;constraint-layout;1.0.2"
RUN echo yes | $ANDROID_HOME/tools/bin/sdkmanager "extras;m2repository;com;android;support;constraint;constraint-layout-solver;1.0.2"
RUN echo yes | $ANDROID_HOME/tools/bin/sdkmanager ${ANDROID_IMAGES}

ENV PATH ${SDK_HOME}/bin:$PATH
RUN echo "sdk.dir=$ANDROID_HOME" > local.properties
