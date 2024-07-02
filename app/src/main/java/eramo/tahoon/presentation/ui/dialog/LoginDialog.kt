package eramo.tahoon.presentation.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import eramo.tahoon.R
import eramo.tahoon.databinding.DialogLoginBinding
import eramo.tahoon.presentation.ui.auth.LoginFragmentArgs

class LoginDialog : DialogFragment(R.layout.dialog_login) {
    private lateinit var binding: DialogLoginBinding
    private val args by navArgs<LoginDialogArgs>()
    private val proceedRequire get() = args.proceedRequire

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setStyle(STYLE_NORMAL, R.style.DialogStyle)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogLoginBinding.bind(view)

        binding.apply {
            DFLogInBtnCancel.setOnClickListener { findNavController().popBackStack() }

            DFLogInBtnLogin.setOnClickListener {
                findNavController().popBackStack()
                Navigation.findNavController(requireActivity(), R.id.main_navHost)
                    .navigate(
                        R.id.loginFragment,
                        LoginFragmentArgs(proceedRequire).toBundle(),
                        NavOptions.Builder().setPopUpTo(R.id.loginDialog, true).build()
                    )
            }
        }
    }
}