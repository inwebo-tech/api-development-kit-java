# inWebo API Java WebApp Code Samples

This is an example project

## Requirements

- [Oracle Sun JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Maven](https://maven.apache.org/)

## Getting Started

1. When logged into inWebo Administration Console, go to tab "Secure Sites". From this screen, you will be able to get:
    - Your inWebo Service ID
    - An inWebo Web Services API Access certificate file in PKCS12 format (.p12)
2. copy the certificate (.p12) in src/main/resources/certificate/
3. Edit src/main/resources/application.properties and change the properties values: 
    - inwebo.api.certificate.path
    - inwebo.api.certificate.password
    - inwebo.api.service-id
    
## Building and launching from Source

1. Execute command `mvn clean package spring-boot:run`
2. Open a browser and go to http://localhost:8080/

## Maven Plugin to generate SOAP inWebo Client:

CXF includes a Maven plugin which can generate java artifacts from WSDL. Here is a simple example:
```xml
<plugin>
    <groupId>org.apache.cxf</groupId>
    <artifactId>cxf-codegen-plugin</artifactId>
	<version>${cxf.version}</version>
	<executions>
	    <execution>
		    <id>generate-sources</id>
			<phase>generate-sources</phase>
			<configuration>
			    <sourceRoot>${project.build.directory}/generated-sources/cxf</sourceRoot>
				<wsdlOptions>
				    <wsdlOption>
					    <wsdl>${basedir}/src/main/resources/wsdl/Authentication.wsdl</wsdl>
						<wsdlLocation>wsdl/Authentication.wsdl</wsdlLocation>
					</wsdlOption>
				    <wsdlOption>
				        <wsdl>${basedir}/src/main/resources/wsdl/Provisioning.wsdl</wsdl>
				        <wsdlLocation>wsdl/Provisioning.wsdl</wsdlLocation>
				    </wsdlOption>
				</wsdlOptions>
			</configuration>
			<goals>
			    <goal>wsdl2java</goal>
			</goals>
		</execution>
	</executions>
</plugin>
```




