/**
 * Copyright 2016 Yurii Rashkovskii
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 */
package graphql.annotations.processor;

import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLTypeExtension;
import graphql.annotations.processor.exceptions.GraphQLAnnotationsException;
import graphql.annotations.processor.graphQLProcessors.GraphQLAnnotationsProcessor;
import graphql.annotations.processor.graphQLProcessors.GraphQLInputProcessor;
import graphql.annotations.processor.graphQLProcessors.GraphQLOutputProcessor;
import graphql.annotations.processor.retrievers.GraphQLObjectHandler;
import graphql.annotations.processor.typeFunctions.DefaultTypeFunction;
import graphql.annotations.processor.typeFunctions.TypeFunction;
import graphql.relay.Relay;
import graphql.schema.GraphQLObjectType;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.HashSet;
import java.util.Map;

import static graphql.annotations.processor.util.NamingKit.toGraphqlName;

/**
 * A utility class for extracting GraphQL data structures from annotated
 * elements.
 */
@Component
public class GraphQLAnnotations implements GraphQLAnnotationsProcessor {

    private GraphQLObjectHandler graphQLObjectHandler;
    private ProcessingElementsContainer container;

    public GraphQLAnnotations() {
        this(new DefaultTypeFunction(new GraphQLInputProcessor(), new GraphQLOutputProcessor()), new GraphQLObjectHandler());
    }

    public GraphQLAnnotations(TypeFunction defaultTypeFunction, GraphQLObjectHandler graphQLObjectHandler) {
        this.graphQLObjectHandler = graphQLObjectHandler;
        this.container = new ProcessingElementsContainer(defaultTypeFunction);
    }

    public static GraphQLAnnotations instance = new GraphQLAnnotations();

    public static GraphQLAnnotations getInstance() {
        return instance;
    }

    public void setRelay(Relay relay) {
        this.container.setRelay(relay);
    }

    public String getTypeName(Class<?> objectClass) {
        GraphQLName name = objectClass.getAnnotation(GraphQLName.class);
        return toGraphqlName(name == null ? objectClass.getSimpleName() : name.value());
    }

    public static GraphQLObjectType object(Class<?> object) throws GraphQLAnnotationsException {
        GraphQLAnnotations instance = getInstance();
        return instance.graphQLObjectHandler.getObject(object, instance.getContainer());
    }

    public void registerTypeExtension(Class<?> objectClass) {
        GraphQLTypeExtension typeExtension = objectClass.getAnnotation(GraphQLTypeExtension.class);
        if (typeExtension == null) {
            throw new GraphQLAnnotationsException("Class is not annotated with GraphQLTypeExtension", null);
        } else {
            Class<?> aClass = typeExtension.value();
            if (!container.getExtensionsTypeRegistry().containsKey(aClass)) {
                container.getExtensionsTypeRegistry().put(aClass, new HashSet<>());
            }
            container.getExtensionsTypeRegistry().get(aClass).add(objectClass);
        }
    }

    public void unregisterTypeExtension(Class<?> objectClass) {
        GraphQLTypeExtension typeExtension = objectClass.getAnnotation(GraphQLTypeExtension.class);
        if (typeExtension == null) {
            throw new GraphQLAnnotationsException("Class is not annotated with GraphQLTypeExtension", null);
        } else {
            Class<?> aClass = typeExtension.value();
            if (container.getExtensionsTypeRegistry().containsKey(aClass)) {
                container.getExtensionsTypeRegistry().get(aClass).remove(objectClass);
            }
        }
    }

    public void registerType(TypeFunction typeFunction) {
        ((DefaultTypeFunction) container.getDefaultTypeFunction()).register(typeFunction);
    }

    public static void register(TypeFunction typeFunction) {
        getInstance().registerType(typeFunction);
    }

    public Map<String, graphql.schema.GraphQLType> getTypeRegistry() {
        return container.getTypeRegistry();
    }

    public ProcessingElementsContainer getContainer() {
        return container;
    }

    public void setContainer(ProcessingElementsContainer container) {
        this.container = container;
    }

    @Reference(target = "(type=default)")
    public void setDefaultTypeFunction(TypeFunction function) {
        this.container.setDefaultTypeFunction(function);
    }

}
