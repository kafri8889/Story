package com.anafthdev.story.ui.register

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.anafthdev.story.R
import com.anafthdev.story.databinding.FragmentRegisterBinding
import com.anafthdev.story.databinding.LoadingDialogBinding
import com.anafthdev.story.foundation.common.EmailValidator
import com.anafthdev.story.foundation.common.EmptyTextValidator
import com.anafthdev.story.foundation.common.PasswordValidator
import com.anafthdev.story.foundation.extension.toast
import com.anafthdev.story.foundation.worker.WorkerUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    @Inject lateinit var workManager: WorkManager

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var progressDialog: Dialog

    private val viewModel: RegisterViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        progressDialog = Dialog(context).apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)

            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            setContentView(
                LoadingDialogBinding.inflate(layoutInflater).apply {
                    tvTitle.text = context.getString(R.string.registering_your_account)
                    tvText.text = context.getString(R.string.please_wait)
                }.root
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()

        // Observe register worker, saat user mengklik register,
        // work info akan di observe dengan kode dibawah
        lifecycleScope.launch {
            viewModel.registerWorkId.asFlow().flatMapLatest { uuid ->
                workManager.getWorkInfoByIdFlow(uuid)
            }.filterNotNull().collectLatest { workInfo ->
                when (workInfo.state) {
                    WorkInfo.State.SUCCEEDED -> {
                        viewModel.setLoading(false)
                        context?.let {
                            it.toast(
                                message = it.getString(R.string.registration_successful),
                                length = Toast.LENGTH_LONG
                            )
                        }

                        findNavController().navigate(R.id.action_register_fragment_to_loginFragment)
                    }
                    WorkInfo.State.FAILED -> {
                        viewModel.setLoading(false)
                        context?.toast(
                            message = workInfo.outputData.getString(WorkerUtil.EXTRA_ERROR_MESSAGE) ?: "Unknown error",
                            length = Toast.LENGTH_LONG
                        )
                    }
                    WorkInfo.State.RUNNING, WorkInfo.State.ENQUEUED -> {
                        viewModel.setLoading(true)
                    }
                    WorkInfo.State.CANCELLED -> {
                        viewModel.setLoading(false)
                        requireContext().toast(requireContext().getString(R.string.cancelled))
                    }
                    else -> {}
                }
            }
        }

        viewModel.showPassword.observe(viewLifecycleOwner) { isShowing ->
            binding.tfPassword.run {
                if (isShowing) {
                    transformationMethod = null
                    setTrailingIcon {
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_outline_visibility
                        ) as Drawable
                    }
                } else {
                    transformationMethod = PasswordTransformationMethod()
                    setTrailingIcon {
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_visibility_off
                        ) as Drawable
                    }
                }

                hideTrailingIcon()
                showTrailingIcon()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) progressDialog.show() else progressDialog.dismiss()
        }

        viewModel.enableRegisterButton.observe(viewLifecycleOwner) { enabled ->
            binding.buttonRegister.isEnabled = enabled
        }
    }

    private fun initView() = with(binding) {
        tvAlreadyHaveAnAccount.apply {
            val spannable = SpannableString(text).apply {
                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        findNavController().navigate(R.id.action_register_fragment_to_loginFragment)
                    }
                }

                val loginText = requireContext().getString(R.string.login)
                val start = text.indexOf(loginText)

                setSpan(clickableSpan, start, start + loginText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            text = spannable
            movementMethod = LinkMovementMethod.getInstance()
        }

        tfUsername.apply {
            addOnTextChangedListener { s ->
                viewModel.setUsername(s.toString())
            }

            addValidator("empty_text", EmptyTextValidator())
        }

        tfEmail.apply {
            addOnTextChangedListener { s ->
                viewModel.setEmail(s.toString())
            }

            addValidator("email", EmailValidator())
        }

        tfPassword.apply {
            setTrailingIcon {
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_outline_visibility
                ) as Drawable
            }

            showTrailingIcon()

            addOnTextChangedListener { s ->
                viewModel.setPassword(s.toString())
            }

            setOnTrailingIconClickListener {
                viewModel.setShowPassword(!viewModel.showPassword.value!!)
            }

            addValidator("password", PasswordValidator())
        }

        buttonRegister.setOnClickListener {
            viewModel.register()
        }
    }
}