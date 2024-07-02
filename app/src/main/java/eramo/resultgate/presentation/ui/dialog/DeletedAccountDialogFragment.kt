package eramo.resultgate.presentation.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import eramo.resultgate.R
import eramo.resultgate.databinding.FragmentDeletedAccountDialogBinding
import eramo.resultgate.util.LocalHelperUtil


@AndroidEntryPoint
class DeletedAccountDialogFragment : DialogFragment(R.layout.fragment_deleted_account_dialog) {
    private lateinit var binding: FragmentDeletedAccountDialogBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        LocalHelperUtil.loadLocal(requireActivity())
        setStyle(STYLE_NORMAL, R.style.DialogStyle)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDeletedAccountDialogBinding.bind(view)

        binding.apply {
            DFCancelBtnCancel.setOnClickListener { findNavController().popBackStack() }

            DFCancelBtnConfirm.setOnClickListener {

//                dialog?.dismiss()
                dismiss()

                findNavController().navigate(R.id.contactUsAuthFragment)
            }
        }

    }

}