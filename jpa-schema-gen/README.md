# jpa-schema-gen

Generates DDLs using the Hibernate with the provided dialect into 
a new sourceSet within the build directory. All that is necessary is a `META-INF/persistence.xml`
with the proper configuration of dialect and set of entity classes.

Disclaimer: In contrast to virtually all other related plugins, this plugin does not intend to
execute migrations. It is rather a developer tool to setup and manage migrations. The execution
is left to tools that ship along with the application and, this way, establish a clear contract
between setup and deployment. Example are the mechanisms of Spring Boot or the complementing
CLI clients of https://github.com/rmee/boot.

Warning: Some frameworks like Spring Boot allow the setup of JPA without a `META-INF/persistence.xml`.
This comes at the cost of not working outside Spring and, as a result, is so far also not supported by
this plugin either. PRs welcomed. For now it should be minor inconvenience with the 
prospect of a many benefits to the development lifecycle.


## Setup

Add this library to the classpath:

```groovy
buildscript {
	dependencies {
	    ...
		classpath 'com.github.rmee:jpa-schema-gen:<VERSION>'
	}
}
```
 

## Tasks

- `generateSchema` to generate the schema files. The output is written to `build/generated/source/schema/`.


## Flyway Example

```groovy
apply plugin: 'jpa-schema-gen'
jpaSchemaGen {
	packageName = 'com.example.demo'
	persistenceUnitName = 'DEMO-UNIT'
	configuration = 'runtime'
	target = 'FLYWAY'
	version = project.version
}
```

The output folder will host three different files following the Flyway naming conventions to setup tables,
constraints and indices. 


## Liquibase Example

```groovy
apply plugin: 'jpa-schema-gen'
jpaSchemaGen {
	packageName = 'com.example.demo'
	persistenceUnitName = 'DEMO-UNIT'
	configuration = 'runtime'
	target = 'LIQUIBASE'
	version = project.version
	liquibase{
		user = 'john' // USER and USERNAME from environment used as default
	 	fileName = 'liquibase-changelog.xml' 
	}
}
```

## Additional Features
### Filter Persistence Unit
To only generate a schema for a subset of `Entity` classes in a persistence unit, you
can use the `includeOnlyPackages` property to list the packages (or package prefixes)
of the classes that you want to include in the schema generation.

This is useful if your application has to include entities in its persistence unit
that it does not manage directly. E.g., if you rely on a library that will create
it's own tables but need to query these tables directly via JPA.

```groovy
apply plugin: 'jpa-schema-gen'
jpaSchemaGen {
	packageName = 'com.example.demo'
	persistenceUnitName = 'DEMO-UNIT'
	configuration = 'runtime'
	target = 'FLYWAY'
	version = project.version
	includeOnlyPackages = ['com.example']
}
```

#### Limitations
The filtering of the persistence unit happens _before_ the persistence provider gets its
hands on the persistence unit. It only works if you explicitly list all Entity classes and
declare `<exclude-unlisted-classes>true</exclude-unlisted-classes>`.  

```xml
<persistence xmlns="http://java.sun.com/xml/ns/persistence"
			 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			 xsi:schemaLocation="http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd"
			 version="2.0">
	<persistence-unit name="DEMO-UNIT" transaction-type="RESOURCE_LOCAL">
		<class>com.example.demo.ExampleEntity</class>
		<class>org.excluded.ExcludedEntity</class>
		<exclude-unlisted-classes>true</exclude-unlisted-classes>
	</persistence-unit>
</persistence>

```

The mechanism used to implement the filtering is quite hack-y. The plugin only chooses
that code path if `includeOnlyPackages` are configured. We recommend, not to configure
that property if you don't _need_ the filtering feature.
 






