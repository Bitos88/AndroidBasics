package io.keepcoding.chat.conversation

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.keepcoding.chat.Message
import io.keepcoding.chat.Repository
import io.keepcoding.chat.common.DateManager.Companion.getCurrentDate
import io.keepcoding.chat.databinding.ViewMessageBinding
import io.keepcoding.chat.databinding.ViewMessageMeBinding

import io.keepcoding.chat.extensions.inflater

class MessagesAdapter(
	diffUtilCallback: DiffUtil.ItemCallback<Message> = DIFF
) : ListAdapter<Message, RecyclerView.ViewHolder>(diffUtilCallback) {

	private var myMessage = 0
	private var responseMessage = 1

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder{

		return when(viewType){
			myMessage -> MessageViewMeHolder(parent)
			else -> MessageViewHolder(parent)
		}
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

		when(holder){
			is MessageViewHolder -> holder.bind(getItem(position))
			is MessageViewMeHolder -> holder.bind(getItem(position))
		}

	}

	override fun getItemViewType(position: Int): Int {
		val idUser = Repository.currentSender.id
		val message = currentList[position]
		val senderID = message.sender.id

		return when(idUser == senderID){
			true -> myMessage
			else -> responseMessage
		}

	}

	companion object {
		val DIFF = object : DiffUtil.ItemCallback<Message>() {
			override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean =
				oldItem.id == newItem.id

			override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean =
				oldItem == newItem
		}
	}

	class MessageViewHolder(
		parent: ViewGroup,
		private val binding: ViewMessageBinding = ViewMessageBinding.inflate(
			parent.inflater,
			parent,
			false
		)
	) : RecyclerView.ViewHolder(binding.root) {

		fun bind(message: Message) {
			binding.channelUserName.text = "${message.sender.name} says:"
			binding.channelName.text = "${message.text}"
			binding.imageView.setBackgroundResource(message.sender.profileImageRes)
			binding.textView2.text = getCurrentDate(message.timestamp)
		}
	}

	class MessageViewMeHolder(
		parent: ViewGroup,
		private val binding: ViewMessageMeBinding = ViewMessageMeBinding.inflate(
			parent.inflater,
			parent,
			false
		)
	) : RecyclerView.ViewHolder(binding.root) {

		fun bind(message: Message) {
			binding.channelUserName.text = "${message.sender.name} says:"
			binding.channelName.text = "${message.text}"
			binding.imageView.setBackgroundResource(message.sender.profileImageRes)
			binding.textView2.text = getCurrentDate(message.timestamp)
		}
	}

}