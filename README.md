# WebTemplate tests

## Configuring maven project to run tests from better-web-template-tests dependency

1. Add maven dependencies

```
<dependency>
    <groupId>care.better.platform.web-template</groupId>
    <artifactId>web-template-tests</artifactId>
    <version>${web-template-tests.version}</version>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>care.better.platform.web-template</groupId>
    <artifactId>web-template-tests</artifactId>
    <version>${web-template-tests.version}</version>
    <classifier>tests</classifier>
    <type>test-jar</type>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-engine</artifactId>
    <version>${jupiter.version></version>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <version>${jupiter.version></version>
    <scope>test</scope>
</dependency>
```

2. Configure maven-surefire-plugin

```
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>${maven-surefire-plugin.version}</version>
    <configuration>
        <dependenciesToScan>
            <dependency>care.better.platform.web-template:web-template-tests</dependency>
        </dependenciesToScan>
    </configuration>
</plugin>
```

3. Add service provider configuration files

    * src/test/META-INF/services/care.better.platform.web.template.converter.CompositionConverter
    * src/test/META-INF/services/care.better.platform.web.template.provider.WebTemplateProvider
    * src/test/META-INF/services/care.better.platform.web.template.validator.CompositionValidator
    
For example, if an implementation of CompositionConverter is located in package care.better.platform.web.template.converter.impl
and has a name BetterCompositionConverter.class, the content of care.better.platform.web.template.converter.CompositionConverter file should look like:

```
 care.better.platform.web.template.converter.impl.BetterCompositionConverter
```

4. Run the tests

```
mvn clean test
```

## Configuring maven project to run both jupiter and testNG tests simultaneously

```
<dependency>
    <groupId>care.better.platform.web-template</groupId>
    <artifactId>web-template-tests</artifactId>
    <version>${web-template-tests.version}</version>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>care.better.platform.web-template</groupId>
    <artifactId>web-template-tests</artifactId>
    <version>${web-template-tests.version}</version>
    <classifier>tests</classifier>
    <type>test-jar</type>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.testng</groupId>
    <artifactId>testng</artifactId>
    <version>${testng.version}</version> <!-- Use version 7.1.0 or higher -->
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>com.github.testng-team</groupId>
    <artifactId>testng-junit5</artifactId>
    <version>${testng-junit5.version></version> <!-- Use version 0.0.1 or higher -->
    <scope>test</scope>
    <exclusions>
        <exclusion>
            <groupId>org.junit.platform</groupId>
            <artifactId>junit-platform-engine</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-engine</artifactId>
    <version>${jupiter.version></version>  <!-- Use version 5.6.3 or higher -->
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <version>${jupiter.version></version>  <!-- Use version 5.6.3 or higher -->
    <scope>test</scope>
</dependency>

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>${maven-surefire-plugin.version}</version>
    <dependencies>
        <dependency>
            <groupId>org.apache.maven.surefire</groupId>
            <artifactId>surefire-junit-platform</artifactId>
            <version>2.22.2</version>
        </dependency>
    </dependencies>
    <configuration>
        <dependenciesToScan>
            <dependency>care.better.platform.web-template:web-template-tests</dependency>
        </dependenciesToScan>
    </configuration>
</plugin>
```