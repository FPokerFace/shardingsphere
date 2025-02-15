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

package org.apache.shardingsphere.migration.distsql.handler.query;

import org.apache.shardingsphere.data.pipeline.api.job.progress.InventoryIncrementalJobItemProgress;
import org.apache.shardingsphere.data.pipeline.api.pojo.InventoryIncrementalJobItemInfo;
import org.apache.shardingsphere.data.pipeline.core.api.InventoryIncrementalJobAPI;
import org.apache.shardingsphere.data.pipeline.core.api.PipelineJobAPI;
import org.apache.shardingsphere.distsql.handler.ral.query.QueryableRALExecutor;
import org.apache.shardingsphere.infra.merge.result.impl.local.LocalDataQueryResultRow;
import org.apache.shardingsphere.infra.util.spi.type.typed.TypedSPILoader;
import org.apache.shardingsphere.migration.distsql.statement.ShowMigrationStatusStatement;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Show migration job status executor.
 */
public final class ShowMigrationJobStatusExecutor implements QueryableRALExecutor<ShowMigrationStatusStatement> {
    
    @Override
    public Collection<LocalDataQueryResultRow> getRows(final ShowMigrationStatusStatement sqlStatement) {
        InventoryIncrementalJobAPI jobAPI = (InventoryIncrementalJobAPI) TypedSPILoader.getService(PipelineJobAPI.class, "MIGRATION");
        List<InventoryIncrementalJobItemInfo> jobItemInfos = jobAPI.getJobItemInfos(sqlStatement.getJobId());
        long currentTimeMillis = System.currentTimeMillis();
        return jobItemInfos.stream().map(each -> {
            LocalDataQueryResultRow row;
            InventoryIncrementalJobItemProgress jobItemProgress = each.getJobItemProgress();
            if (null != jobItemProgress) {
                String incrementalIdleSeconds = "";
                if (jobItemProgress.getIncremental().getIncrementalLatestActiveTimeMillis() > 0) {
                    long latestActiveTimeMillis = Math.max(each.getStartTimeMillis(), jobItemProgress.getIncremental().getIncrementalLatestActiveTimeMillis());
                    incrementalIdleSeconds = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(currentTimeMillis - latestActiveTimeMillis));
                }
                row = new LocalDataQueryResultRow(each.getShardingItem(), jobItemProgress.getDataSourceName(), jobItemProgress.getStatus(),
                        jobItemProgress.isActive() ? Boolean.TRUE.toString() : Boolean.FALSE.toString(), jobItemProgress.getProcessedRecordsCount(), each.getInventoryFinishedPercentage(),
                        incrementalIdleSeconds, each.getErrorMessage());
            } else {
                row = new LocalDataQueryResultRow(each.getShardingItem(), "", "", "", "", "", "", each.getErrorMessage());
            }
            return row;
        }).collect(Collectors.toList());
    }
    
    @Override
    public Collection<String> getColumnNames() {
        return Arrays.asList("item", "data_source", "status", "active", "processed_records_count", "inventory_finished_percentage", "incremental_idle_seconds", "error_message");
    }
    
    @Override
    public String getType() {
        return ShowMigrationStatusStatement.class.getName();
    }
}
