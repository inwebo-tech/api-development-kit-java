# inWebo API Java Console Code Samples

This is an example project

## Requirements

- [Oracle Sun JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Maven](https://maven.apache.org/)

## Getting Started

Before you start writing code, you need to have an InWebo admin account. You can get one at: https://www.myinwebo.com/signup 
When logged in to InWebo WebConsole, go to "service parameters". From this screen, you will be able to get:
- your service_id
- a certificate file

These 2 items are mandatory. Once your have them, open com.acme.ApiDemo.java, and fill in the 3 variables below with the correct values:
private static String p12file = "path_to_your_certificate.p12"; // Specify here the name of your certificate file.
private static String p12password = "your_password"; // This is the password to access your certificate file
private static int serviceId = 0; // This is the id of your service.

## Building and launching from Source

```bash
$ mvn clean package exec-maven-plugin:java
```

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




