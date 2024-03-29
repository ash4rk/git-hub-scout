package com.ash4rk.githubscout.ui.users

import androidx.annotation.MainThread
import androidx.databinding.Bindable
import androidx.lifecycle.viewModelScope
import com.ash4rk.core.data.repository.MainRepository
import com.ash4rk.core.model.User
import com.skydoves.bindables.BindingViewModel
import com.skydoves.bindables.asBindingProperty
import com.skydoves.bindables.bindingProperty
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : BindingViewModel() {

    @get:Bindable
    var isLoading: Boolean by bindingProperty(false)
        private set

    @get:Bindable
    var toastMessage: String? by bindingProperty(null)
        private set

    private val userFetchingIndex: MutableStateFlow<Int> = MutableStateFlow(0)
    private val userListFlow = userFetchingIndex.flatMapLatest { page ->
        mainRepository.fetchUserList(
            page = page,
            onStart = { isLoading = true },
            onComplete = { isLoading = false },
            onError = {
                toastMessage = it
                Timber.d(it)
            }
        )
    }

    @get:Bindable
    val userList: List<User> by userListFlow.asBindingProperty(viewModelScope, emptyList())

    init {
        Timber.d("Init UsersViewModel")
    }

    @MainThread
    fun fetchNextUserList() {
        if (!isLoading) {
            userFetchingIndex.value++
        }
    }
}