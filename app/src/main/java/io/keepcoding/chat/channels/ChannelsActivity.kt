package io.keepcoding.chat.channels

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.keepcoding.chat.Channel
import io.keepcoding.chat.R
import io.keepcoding.chat.Repository
import io.keepcoding.chat.conversation.ConversationActivity
import io.keepcoding.chat.databinding.ActivityChannelsBinding

class ChannelsActivity : AppCompatActivity() {

	val binding: ActivityChannelsBinding by lazy { ActivityChannelsBinding.inflate(layoutInflater) }
	val channelsAdapter: ChannelsAdapter by lazy { ChannelsAdapter(::openChannel) }
	val vm: ChannelsViewModel by viewModels {
		ChannelsViewModel.ChannelsViewModelProviderFactory(Repository)
	}

	lateinit var swipeContainer: SwipeRefreshLayout

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(binding.root)

		swipeContainer = findViewById(R.id.swipeContainer)

		binding.noChannels.isVisible = false

		binding.topics.apply {
			adapter = channelsAdapter
			addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
		}


		swipeContainer.setOnRefreshListener {
			// Your code to refresh the list here.
			// Make sure you call swipeContainer.setRefreshing(false)
			// once the network request has completed successfully.
			binding.progressBar.isVisible = true
			vm.loadChannels()
		}

		vm.state.observe(this) {
			when (it) {
				is ChannelsViewModel.State.ChannelsReceived -> {
					binding.noChannels.isVisible = false
					channelsAdapter.submitList(it.channels)
					swipeContainer.isRefreshing = false
					binding.topics.isVisible = false

					binding.root.postDelayed({
						binding.topics.isVisible = true
						hideLoading()
					}, 2000)

				}
				is ChannelsViewModel.State.Error.ErrorLoading -> {
					Toast.makeText(this, "No Channels", Toast.LENGTH_SHORT).show()
					swipeContainer.isRefreshing = false
					binding.imageView2.isVisible = true
					binding.textView.isVisible = true
					binding.noChannels.isVisible = true

					binding.root.postDelayed({
						binding.topics.isVisible = true
						hideLoading()
					}, 3000)

					hideLoading()
				}
				is ChannelsViewModel.State.Error.ErrorWithChannels -> {
					Toast.makeText(this, "Error With Channels", Toast.LENGTH_SHORT).show()

					channelsAdapter.submitList(it.channels)
					swipeContainer.isRefreshing = false
					binding.noChannels.isVisible = false

					binding.root.postDelayed({
						binding.topics.isVisible = true
						hideLoading()
					}, 3000)


					hideLoading()
				}
				is ChannelsViewModel.State.LoadingChannels.Loading -> {
					showLoading()
					binding.noChannels.isVisible = false
					swipeContainer.isRefreshing = false

					binding.root.postDelayed({
						binding.topics.isVisible = true
						hideLoading()
					}, 2000)

				}
				is ChannelsViewModel.State.LoadingChannels.LoadingWithChannels -> {
					channelsAdapter.submitList(it.channels)

					binding.root.postDelayed({
						binding.topics.isVisible = true
						hideLoading()
					}, 2000)
				}
			}
		}
	}

	private fun showLoading() {
		binding.progressBar.isVisible = true
	}

	private fun hideLoading() {
		binding.progressBar.isVisible = false
	}

	override fun onResume() {
		super.onResume()
		binding.progressBar.isVisible = true
		vm.loadChannels()
	}

	private fun openChannel(channel: Channel) {
		startActivity(ConversationActivity.createIntent(this, channel))
	}
}