package com.github.rmee.jpa.schemagen.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.sql.DataSource;

import org.hibernate.jpa.boot.internal.ParsedPersistenceXmlDescriptor;
import org.hibernate.jpa.boot.internal.PersistenceXmlParser;

/**
 * <p>Filters an XML persistence unit that explicitly lists its classes using a list of white-listed package prefixes.</p>
 * <p>This class does not directly implement from <code>PersistenceUnitInfo</code> because we need to use the exact class that
 * the project classpath is using. It uses the hibernate {@link PersistenceXmlParser} to implement the
 * <code>PersistenceUnitInfo</code> interface via a dynamic proxy.</p>
 * <p>The actual filtering takes place in the {@link PersistenceUnitAdapter#getManagedClassNames()} method.</p>
 */
public final class FilteredPersistenceUnit {
    private FilteredPersistenceUnit(){}
    public static Object fromXmlPersistenceUnit(URLClassLoader classloader, URL resourceUrl, List<String> includeOnlyPackages, String persistenceUnitName)
            throws ClassNotFoundException {
        Class<?> persistenceUnitInfoClass = classloader.loadClass("javax.persistence.spi.PersistenceUnitInfo");

        ParsedPersistenceXmlDescriptor parsedXml;
        if(persistenceUnitName == null){
            parsedXml = PersistenceXmlParser.locateIndividualPersistenceUnit(resourceUrl);
        } else {
            parsedXml = PersistenceXmlParser.locateNamedPersistenceUnit(resourceUrl, persistenceUnitName);
        }
        Class<?> validationModeClass = classloader.loadClass("javax.persistence.ValidationMode");
        Class<?> sharedCacheModeClass = classloader.loadClass("javax.persistence.SharedCacheMode");
        Class<?> persistenceUnitTransactionTypeClass =
                classloader.loadClass("javax.persistence.spi.PersistenceUnitTransactionType");

        return Proxy.newProxyInstance(classloader, new Class<?>[] { persistenceUnitInfoClass },
                new PersistenceUnitAdapter(parsedXml, persistenceUnitTransactionTypeClass, includeOnlyPackages,
                        sharedCacheModeClass, validationModeClass, classloader));
    }

    private static class PersistenceUnitAdapter implements InvocationHandler {

        private final ParsedPersistenceXmlDescriptor parsedXml;
        private final Class<?> persistenceUnitTransactionTypeClass;
        private final List<String> includeOnlyPackages;
        private final Class<?> sharedCacheModeClass;
        private final Class<?> validationModeClass;
        private final URLClassLoader classloader;

        public PersistenceUnitAdapter(ParsedPersistenceXmlDescriptor parsedXml, Class<?> persistenceUnitTransactionTypeClass,
                List<String> includeOnlyPackages, Class<?> sharedCacheModeClass, Class<?> validationModeClass,
                URLClassLoader classloader) {
            this.parsedXml = parsedXml;
            this.persistenceUnitTransactionTypeClass = persistenceUnitTransactionTypeClass;
            this.includeOnlyPackages = includeOnlyPackages;
            this.sharedCacheModeClass = sharedCacheModeClass;
            this.validationModeClass = validationModeClass;
            this.classloader = classloader;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args)
                throws Throwable {
            switch (method.getName()) {
                case "getPersistenceUnitName":
                    return getPersistenceUnitName();
                case "getPersistenceProviderClassName":
                    return getPersistenceProviderClassName();
                case "getTransactionType":
                    return getTransactionType();
                case "getJtaDataSource":
                    return getJtaDataSource();
                case "getNonJtaDataSource":
                    return getNonJtaDataSource();
                case "getMappingFileNames":
                    return getMappingFileNames();
                case "getJarFileUrls":
                    return getJarFileUrls();
                case "getPersistenceUnitRootUrl":
                    return getPersistenceUnitRootUrl();
                case "getManagedClassNames":
                    return getManagedClassNames();
                case "excludeUnlistedClasses":
                    return excludeUnlistedClasses();
                case "getSharedCacheMode":
                    return getSharedCacheMode();
                case "getValidationMode":
                    return getValidationMode();
                case "getProperties":
                    return getProperties();
                case "getPersistenceXMLSchemaVersion":
                    return getPersistenceXMLSchemaVersion();
                case "getClassLoader":
                    return getClassLoader();
                case "addTransformer":
                    addTransformer(args[0]);
                    return null;
                case "getNewTempClassLoader":
                    return getNewTempClassLoader();
                case "equals":
                    return parsedXml.equals(args[0]);
                case "hashCode":
                    return parsedXml.hashCode();
                case "toString":
                    return parsedXml.toString();
                default:
                    throw new UnsupportedOperationException(
                            "Method " + method.getName() + " not supported.");
            }
        }

        String getPersistenceUnitName() {
            return parsedXml.getName();
        }

        String getPersistenceProviderClassName() {
            return parsedXml.getProviderClassName();
        }

        Object getTransactionType() {
            return convertEnumValue(parsedXml.getTransactionType(),
                    persistenceUnitTransactionTypeClass);
        }

        DataSource getJtaDataSource() {
            return (DataSource) parsedXml.getJtaDataSource();
        }


        DataSource getNonJtaDataSource() {
            return (DataSource) parsedXml.getNonJtaDataSource();
        }

        List<String> getMappingFileNames() {
            return parsedXml.getMappingFileNames();
        }

        List<URL> getJarFileUrls() {
            return parsedXml.getJarFileUrls();
        }

        URL getPersistenceUnitRootUrl() {
            return parsedXml.getPersistenceUnitRootUrl();
        }

        List<String> getManagedClassNames() {
            return parsedXml.getManagedClassNames().stream()
                    .filter(className ->
                            includeOnlyPackages.stream()
                                    .anyMatch(className::startsWith))
                    .collect(Collectors.toList());
        }

        boolean excludeUnlistedClasses() {
            return parsedXml.isExcludeUnlistedClasses();
        }

        Object getSharedCacheMode() {
            return convertEnumValue(parsedXml.getSharedCacheMode(), sharedCacheModeClass);
        }

        Object getValidationMode() {
            return convertEnumValue(parsedXml.getValidationMode(), validationModeClass);
        }

        private Object convertEnumValue(Enum<?> enumValue, Class<?> targetEnumType) {
            if (enumValue == null) {
                return null;
            }
            @SuppressWarnings("unchecked")
            Object targetValue = Enum.valueOf((Class) targetEnumType, enumValue.name());
            return targetValue;
        }

        Properties getProperties() {
            return parsedXml.getProperties();
        }

        String getPersistenceXMLSchemaVersion() {
            return "2.0";
        }

        ClassLoader getClassLoader() {
            return classloader;
        }

        void addTransformer(Object transformer) {
            throw new UnsupportedOperationException(
                    "Can't add a class transformer while generating the schema creation scripts.");
        }

        ClassLoader getNewTempClassLoader() {
            // Unfortunately required by hibernate
            return new ClassLoader(getClassLoader()) {
            };
        }

    }
}
