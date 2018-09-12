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

```
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

```
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

```
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








