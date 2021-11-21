package io.keepcoding.chat.conversation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import io.keepcoding.chat.Channel
import io.keepcoding.chat.Message
import io.keepcoding.chat.Repository
import io.keepcoding.chat.common.TextChangedWatcher
import io.keepcoding.chat.databinding.ActivityConversationBinding
import kotlinx.coroutines.delay

class ConversationActivity : AppCompatActivity() {

	private val binding: ActivityConversationBinding by lazy {
		ActivityConversationBinding.inflate(layoutInflater)
	}
	private val vm: ConversationViewModel by viewModels {
		ConversationViewModel.ConversationViewModelProviderFactory(Repository)
	}
	private val messagesAdapter: MessagesAdapter = MessagesAdapter()
	private val channelId: String by lazy { intent.getStringExtra(CHANNEL_ID)!! }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(binding.root)

		binding.progressBar2.isVisible = true


		binding.conversation.apply {
			layoutManager = LinearLayoutManager(context).apply {
				stackFromEnd = true
			}
			adapter = messagesAdapter
		}
		vm.state.observe(this) {
			when (it) {
				is ConversationViewModel.State.MessagesReceived -> {
					renderMessages(it.messages)
					hideLoading()
					binding.noConversations.isVisible = false
				}
				is ConversationViewModel.State.Error.ErrorLoading -> {
					hideLoading()


					binding.noConversations.isVisible = true

				}
				is ConversationViewModel.State.Error.ErrorWithMessages -> {
					renderMessages(it.messages)
					binding.noConversations.isVisible = true

					hideLoading()
				}
				is ConversationViewModel.State.LoadingMessages.Loading -> {
					binding.noConversations.isVisible = true

					showLoading()
				}
				is ConversationViewModel.State.LoadingMessages.LoadingWithMessages -> {
					renderMessages(it.messages)
				}
			}
		}
		vm.message.observe(this) {
			binding.tvMessage.apply {
				setText(it)
				setSelection(it.length)

				if (it.isBlank()) {
					binding.sendButton.alpha = 0.3f
					binding.sendButton.isEnabled = false
				} else {
					binding.sendButton.isEnabled = true
					binding.sendButton.alpha = 1f
				}
			}
		}
		binding.tvMessage.addTextChangedListener(TextChangedWatcher(vm::onInputMessageUpdated))
		binding.sendButton.setOnClickListener { vm.sendMessage(channelId) }
	}

	private fun renderMessages(messages: List<Message>) {
		messagesAdapter.submitList(messages) { binding.conversation.smoothScrollToPosition(messages.size) }
	}

	private fun showLoading() {
		binding.progressBar2.isVisible = true

	}


	private fun hideLoading() {
		binding.progressBar2.isVisible = false

	}

	override fun onResume() {
		super.onResume()
		vm.loadConversation(channelId)
	}

	companion object {
		const val CHANNEL_ID = "CHANNEL_ID"

		fun createIntent(context: Context, channel: Channel): Intent =
			Intent(
				context,
				ConversationActivity::class.java
			).apply {
				putExtra(CHANNEL_ID, channel.id)
			}
	}
}