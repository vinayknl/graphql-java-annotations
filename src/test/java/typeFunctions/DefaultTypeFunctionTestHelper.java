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
package typeFunctions;

import graphql.annotations.GraphQLAnnotations;
import graphql.annotations.graphQLProcessors.GraphQLInputProcessor;
import graphql.annotations.graphQLProcessors.GraphQLOutputProcessor;
import graphql.annotations.typeFunctions.DefaultTypeFunction;

public class DefaultTypeFunctionTestHelper {
    public static DefaultTypeFunction testedDefaultTypeFunction() {
        // wire up the ability
        GraphQLAnnotations graphQLAnnotations = new GraphQLAnnotations();
        DefaultTypeFunction defaultTypeFunction = new DefaultTypeFunction(new GraphQLInputProcessor(),new GraphQLOutputProcessor());
        defaultTypeFunction.setAnnotationsProcessor(graphQLAnnotations);
        return defaultTypeFunction;
    }
}
