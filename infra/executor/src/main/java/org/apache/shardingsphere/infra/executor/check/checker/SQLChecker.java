/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.infra.executor.check.checker;

import org.apache.shardingsphere.infra.binder.statement.SQLStatementContext;
import org.apache.shardingsphere.infra.metadata.database.ShardingSphereDatabase;
import org.apache.shardingsphere.infra.metadata.database.rule.ShardingSphereRuleMetaData;
import org.apache.shardingsphere.infra.metadata.user.Grantee;
import org.apache.shardingsphere.infra.rule.ShardingSphereRule;
import org.apache.shardingsphere.infra.util.spi.annotation.SingletonSPI;
import org.apache.shardingsphere.infra.util.spi.type.ordered.OrderedSPI;

import java.util.List;
import java.util.function.BiPredicate;

/**
 * SQL checker.
 */
@SingletonSPI
public interface SQLChecker<T extends ShardingSphereRule> extends OrderedSPI<T> {
    
    /**
     * Check database.
     *
     * @param databaseName database name
     * @param grantee grantee
     * @param rule rule
     * @return check result
     */
    boolean check(String databaseName, Grantee grantee, T rule);
    
    /**
     * Check SQL.
     * 
     * @param sqlStatementContext SQL statement context
     * @param params SQL parameters
     * @param grantee grantee
     * @param globalRuleMetaData global rule meta data
     * @param database current database
     * @param rule rule
     */
    void check(SQLStatementContext<?> sqlStatementContext, List<Object> params, Grantee grantee, ShardingSphereRuleMetaData globalRuleMetaData, ShardingSphereDatabase database, T rule);
    
    /**
     * Check User.
     * @param grantee grantee
     * @param rule rule
     * @return check result
     */
    boolean check(Grantee grantee, T rule);
    
    /**
     * Check user.
     * 
     * @param grantee grantee
     * @param validator password validator
     * @param cipher cipher
     * @param rule rule
     * @return check result
     */
    boolean check(Grantee grantee, BiPredicate<Object, Object> validator, Object cipher, T rule);
}
