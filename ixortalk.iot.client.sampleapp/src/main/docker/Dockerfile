#
# The MIT License (MIT)
#
# Copyright (c) 2016-present IxorTalk CVBA
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in all
# copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
# SOFTWARE.
#

FROM frolvlad/alpine-oraclejdk8:slim
VOLUME /tmp
ADD ixortalk.iot.client.sampleapp-*.jar app.jar
RUN sh -c 'touch /app.jar'
VOLUME ["/var/log"]
ENV JAVA_OPTS=""
EXPOSE 8100 8600
HEALTHCHECK CMD wget -q http://localhost:8600/health -O /dev/null
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Dlogging.file=/var/log/ixortalk.iot.client.sampleapp.log -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]