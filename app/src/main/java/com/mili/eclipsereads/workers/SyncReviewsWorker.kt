package com.mili.eclipsereads.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import com.mili.eclipsereads.data.repository.BooksRepository
import com.mili.eclipsereads.data.repository.ReviewsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class SyncReviewsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val booksRepository: BooksRepository,
    private val reviewsRepository: ReviewsRepository
) : SyncWorker(context, workerParams) {

    override suspend fun doSync() {
        // Warning: This worker fetches all books and then fetches reviews for each book.
        // This can be inefficient and generate a lot of network traffic.
        // Consider a more targeted synchronization strategy.
        val books = booksRepository.getBooks().first()
        books.forEach { book ->
            reviewsRepository.refreshReviews(book.id)
        }
    }
}
