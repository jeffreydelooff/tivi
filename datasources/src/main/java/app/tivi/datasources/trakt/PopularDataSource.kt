/*
 * Copyright 2017 Google, Inc.
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
 * limitations under the License.
 */

package app.tivi.datasources.trakt

import android.arch.paging.DataSource
import app.tivi.data.daos.PopularDao
import app.tivi.data.entities.PopularListItem
import app.tivi.datasources.PaginatedDataSource
import app.tivi.util.AppRxSchedulers
import io.reactivex.Flowable
import javax.inject.Inject

class PopularDataSource @Inject constructor(
    private val popularDao: PopularDao,
    private val schedulers: AppRxSchedulers
) : PaginatedDataSource<Unit, PopularListItem> {
    override fun data(param: Unit): Flowable<List<PopularListItem>> {
        return popularDao.entries()
                .subscribeOn(schedulers.io)
                .distinctUntilChanged()
    }

    override fun data(page: Int): Flowable<List<PopularListItem>> {
        return popularDao.entriesPage(page)
                .subscribeOn(schedulers.io)
                .distinctUntilChanged()
    }

    override fun dataSourceFactory(): DataSource.Factory<Int, PopularListItem> = popularDao.entriesDataSource()
}