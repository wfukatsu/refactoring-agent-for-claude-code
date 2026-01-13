@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  register-secret startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and REGISTER_SECRET_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto execute

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\scalardl-java-client-sdk-3.10.0.jar;%APP_HOME%\lib\scalardl-common-3.10.0.jar;%APP_HOME%\lib\scalardb-3.14.0.jar;%APP_HOME%\lib\scalardl-rpc-3.10.0.jar;%APP_HOME%\lib\scalar-admin-2.2.1.jar;%APP_HOME%\lib\javax.json-api-1.1.4.jar;%APP_HOME%\lib\javax.json-1.1.4.jar;%APP_HOME%\lib\cassandra-driver-core-3.11.5.jar;%APP_HOME%\lib\azure-cosmos-4.64.0.jar;%APP_HOME%\lib\azure-core-1.53.0.jar;%APP_HOME%\lib\jackson-datatype-jsr310-2.18.1.jar;%APP_HOME%\lib\jackson-annotations-2.18.1.jar;%APP_HOME%\lib\jackson-core-2.18.1.jar;%APP_HOME%\lib\jackson-databind-2.18.1.jar;%APP_HOME%\lib\log4j-slf4j-impl-2.24.1.jar;%APP_HOME%\lib\metrics-jmx-4.2.28.jar;%APP_HOME%\lib\metrics-core-4.2.28.jar;%APP_HOME%\lib\applicationautoscaling-2.28.6.jar;%APP_HOME%\lib\dynamodb-2.28.6.jar;%APP_HOME%\lib\aws-json-protocol-2.28.6.jar;%APP_HOME%\lib\protocol-core-2.28.6.jar;%APP_HOME%\lib\aws-core-2.28.6.jar;%APP_HOME%\lib\auth-2.28.6.jar;%APP_HOME%\lib\regions-2.28.6.jar;%APP_HOME%\lib\sdk-core-2.28.6.jar;%APP_HOME%\lib\http-auth-aws-2.28.6.jar;%APP_HOME%\lib\http-auth-2.28.6.jar;%APP_HOME%\lib\http-auth-spi-2.28.6.jar;%APP_HOME%\lib\identity-spi-2.28.6.jar;%APP_HOME%\lib\apache-client-2.28.6.jar;%APP_HOME%\lib\netty-nio-client-2.28.6.jar;%APP_HOME%\lib\http-client-spi-2.28.6.jar;%APP_HOME%\lib\metrics-spi-2.28.6.jar;%APP_HOME%\lib\json-utils-2.28.6.jar;%APP_HOME%\lib\retries-2.28.6.jar;%APP_HOME%\lib\retries-spi-2.28.6.jar;%APP_HOME%\lib\profiles-2.28.6.jar;%APP_HOME%\lib\utils-2.28.6.jar;%APP_HOME%\lib\slf4j-api-1.7.36.jar;%APP_HOME%\lib\guice-5.1.0.jar;%APP_HOME%\lib\grpc-services-1.68.1.jar;%APP_HOME%\lib\grpc-stub-1.68.1.jar;%APP_HOME%\lib\grpc-netty-1.68.1.jar;%APP_HOME%\lib\grpc-protobuf-1.68.1.jar;%APP_HOME%\lib\grpc-util-1.68.1.jar;%APP_HOME%\lib\grpc-core-1.68.1.jar;%APP_HOME%\lib\grpc-protobuf-lite-1.68.1.jar;%APP_HOME%\lib\grpc-context-1.68.1.jar;%APP_HOME%\lib\grpc-api-1.68.1.jar;%APP_HOME%\lib\protobuf-java-util-3.25.5.jar;%APP_HOME%\lib\guava-33.2.1-jre.jar;%APP_HOME%\lib\toml4j-0.7.2.jar;%APP_HOME%\lib\picocli-4.7.6.jar;%APP_HOME%\lib\log4j-core-2.24.1.jar;%APP_HOME%\lib\bcpkix-jdk15on-1.70.jar;%APP_HOME%\lib\bcutil-jdk15on-1.70.jar;%APP_HOME%\lib\bcprov-jdk15on-1.70.jar;%APP_HOME%\lib\simpleclient_dropwizard-0.16.0.jar;%APP_HOME%\lib\simpleclient_servlet-0.16.0.jar;%APP_HOME%\lib\simpleclient_hotspot-0.16.0.jar;%APP_HOME%\lib\jetty-servlet-9.4.56.v20240826.jar;%APP_HOME%\lib\commons-text-1.12.0.jar;%APP_HOME%\lib\jooq-3.14.16.jar;%APP_HOME%\lib\commons-dbcp2-2.12.0.jar;%APP_HOME%\lib\mysql-connector-j-8.4.0.jar;%APP_HOME%\lib\postgresql-42.7.4.jar;%APP_HOME%\lib\ojdbc8-21.15.0.0.jar;%APP_HOME%\lib\mssql-jdbc-11.2.3.jre8.jar;%APP_HOME%\lib\sqlite-jdbc-3.46.1.3.jar;%APP_HOME%\lib\jdbc-yugabytedb-42.7.3-yb-1.jar;%APP_HOME%\lib\javax.inject-1.jar;%APP_HOME%\lib\aopalliance-1.0.jar;%APP_HOME%\lib\failureaccess-1.0.2.jar;%APP_HOME%\lib\listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar;%APP_HOME%\lib\jsr305-3.0.2.jar;%APP_HOME%\lib\checker-qual-3.42.0.jar;%APP_HOME%\lib\gson-2.11.0.jar;%APP_HOME%\lib\error_prone_annotations-2.28.0.jar;%APP_HOME%\lib\log4j-api-2.24.1.jar;%APP_HOME%\lib\simpleclient_servlet_common-0.16.0.jar;%APP_HOME%\lib\simpleclient_common-0.16.0.jar;%APP_HOME%\lib\simpleclient-0.16.0.jar;%APP_HOME%\lib\jetty-security-9.4.56.v20240826.jar;%APP_HOME%\lib\jetty-util-ajax-9.4.56.v20240826.jar;%APP_HOME%\lib\commons-lang3-3.14.0.jar;%APP_HOME%\lib\endpoints-spi-2.28.6.jar;%APP_HOME%\lib\checksums-2.28.6.jar;%APP_HOME%\lib\checksums-spi-2.28.6.jar;%APP_HOME%\lib\http-auth-aws-eventstream-2.28.6.jar;%APP_HOME%\lib\annotations-2.28.6.jar;%APP_HOME%\lib\third-party-jackson-core-2.28.6.jar;%APP_HOME%\lib\azure-core-http-netty-1.15.5.jar;%APP_HOME%\lib\reactor-netty-http-1.0.45.jar;%APP_HOME%\lib\reactor-netty-core-1.0.45.jar;%APP_HOME%\lib\netty-handler-proxy-4.1.110.Final.jar;%APP_HOME%\lib\netty-codec-http2-4.1.112.Final.jar;%APP_HOME%\lib\netty-codec-http-4.1.112.Final.jar;%APP_HOME%\lib\netty-resolver-dns-native-macos-4.1.109.Final-osx-x86_64.jar;%APP_HOME%\lib\netty-resolver-dns-classes-macos-4.1.109.Final.jar;%APP_HOME%\lib\netty-resolver-dns-4.1.109.Final.jar;%APP_HOME%\lib\netty-handler-4.1.112.Final.jar;%APP_HOME%\lib\jnr-posix-3.0.44.jar;%APP_HOME%\lib\jnr-ffi-2.1.7.jar;%APP_HOME%\lib\micrometer-core-1.9.17.jar;%APP_HOME%\lib\HdrHistogram-2.1.12.jar;%APP_HOME%\lib\reactor-core-3.4.38.jar;%APP_HOME%\lib\reactive-streams-1.0.4.jar;%APP_HOME%\lib\jaxb-api-2.3.1.jar;%APP_HOME%\lib\commons-pool2-2.12.0.jar;%APP_HOME%\lib\httpclient-4.5.13.jar;%APP_HOME%\lib\commons-logging-1.3.0.jar;%APP_HOME%\lib\jakarta.transaction-api-1.3.3.jar;%APP_HOME%\lib\proto-google-common-protos-2.41.0.jar;%APP_HOME%\lib\googleapis-common-protos-0.0.3.jar;%APP_HOME%\lib\protobuf-java-3.25.5.jar;%APP_HOME%\lib\perfmark-api-0.27.0.jar;%APP_HOME%\lib\netty-transport-native-epoll-4.1.110.Final-linux-x86_64.jar;%APP_HOME%\lib\netty-transport-native-kqueue-4.1.110.Final-osx-x86_64.jar;%APP_HOME%\lib\netty-transport-classes-epoll-4.1.112.Final.jar;%APP_HOME%\lib\netty-transport-classes-kqueue-4.1.110.Final.jar;%APP_HOME%\lib\netty-transport-native-unix-common-4.1.112.Final.jar;%APP_HOME%\lib\dnsjava-3.6.1.jar;%APP_HOME%\lib\javax.annotation-api-1.2.jar;%APP_HOME%\lib\simpleclient_tracer_otel-0.16.0.jar;%APP_HOME%\lib\simpleclient_tracer_otel_agent-0.16.0.jar;%APP_HOME%\lib\jetty-server-9.4.56.v20240826.jar;%APP_HOME%\lib\jetty-http-9.4.56.v20240826.jar;%APP_HOME%\lib\jetty-io-9.4.56.v20240826.jar;%APP_HOME%\lib\jetty-util-9.4.56.v20240826.jar;%APP_HOME%\lib\netty-codec-socks-4.1.110.Final.jar;%APP_HOME%\lib\netty-codec-dns-4.1.109.Final.jar;%APP_HOME%\lib\netty-codec-4.1.112.Final.jar;%APP_HOME%\lib\netty-transport-4.1.112.Final.jar;%APP_HOME%\lib\netty-buffer-4.1.112.Final.jar;%APP_HOME%\lib\netty-resolver-4.1.112.Final.jar;%APP_HOME%\lib\netty-common-4.1.112.Final.jar;%APP_HOME%\lib\jffi-1.2.16.jar;%APP_HOME%\lib\jffi-1.2.16-native.jar;%APP_HOME%\lib\asm-commons-5.0.3.jar;%APP_HOME%\lib\asm-analysis-5.0.3.jar;%APP_HOME%\lib\asm-util-5.0.3.jar;%APP_HOME%\lib\asm-tree-5.0.3.jar;%APP_HOME%\lib\asm-5.0.3.jar;%APP_HOME%\lib\jnr-x86asm-1.0.2.jar;%APP_HOME%\lib\jnr-constants-0.9.9.jar;%APP_HOME%\lib\azure-json-1.3.0.jar;%APP_HOME%\lib\azure-xml-1.1.0.jar;%APP_HOME%\lib\netty-tcnative-boringssl-static-2.0.65.Final.jar;%APP_HOME%\lib\LatencyUtils-2.0.3.jar;%APP_HOME%\lib\javax.activation-api-1.2.0.jar;%APP_HOME%\lib\annotations-4.1.1.4.jar;%APP_HOME%\lib\animal-sniffer-annotations-1.24.jar;%APP_HOME%\lib\j2objc-annotations-2.8.jar;%APP_HOME%\lib\simpleclient_tracer_common-0.16.0.jar;%APP_HOME%\lib\javax.servlet-api-3.1.0.jar;%APP_HOME%\lib\eventstream-1.0.1.jar;%APP_HOME%\lib\httpcore-4.4.16.jar;%APP_HOME%\lib\commons-codec-1.17.1.jar;%APP_HOME%\lib\netty-tcnative-classes-2.0.65.Final.jar


@rem Execute register-secret
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %REGISTER_SECRET_OPTS%  -classpath "%CLASSPATH%" com.scalar.dl.client.tool.SecretRegistration %*

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable REGISTER_SECRET_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%REGISTER_SECRET_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
